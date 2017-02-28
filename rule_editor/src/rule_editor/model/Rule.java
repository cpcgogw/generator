package rule_editor.model;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import rule_editor.Log;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vilddjur on 1/28/17.
 */
public class Rule {
    public static final boolean DEBUG_MODE = true;
    public Pattern matchingPattern;
    public ArrayList<Pattern> possibleTranslations;
    private Random rand;
    public Rule(Pattern matchingPattern){
        rand = new Random();
        this.matchingPattern = matchingPattern;
        possibleTranslations = new ArrayList<Pattern>();
    }

    public Rule(Pattern match, ArrayList<Pair<ArrayList<Node>, ArrayList<Edge>>> translations) {
        this.matchingPattern = match;
        possibleTranslations = new ArrayList<Pattern>();
        rand = new Random();
        for (Pair<ArrayList<Node>, ArrayList<Edge>> pair : translations) {
            Pattern p = new Pattern(pair);
            possibleTranslations.add(p);
        }
    }

    public void save(String path){
        try {
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            //<Rule></Rule>
            Element elemRule = doc.createElement("Rule");
            doc.appendChild(elemRule);

            //<Rule><MatchingPattern></MatchingPattern></Rule>
            Element elemMatchingPattern = doc.createElement("MatchingPattern");
            elemRule.appendChild(elemMatchingPattern);
            //<Rule><MatchingPattern><Pattern></Pattern></MatchingPattern></Rule>
            Element elemNodes = doc.createElement("Pattern");
            elemMatchingPattern.appendChild(elemNodes);
            //<Rule><MatchingPattern><Pattern>...</Pattern></MatchingPattern></Rule>
            insertNodesInto(matchingPattern.nodes, elemNodes, doc);


            //<Rule><PossibleTranslations></PossibleTranslations></Rule>
            Element elemPosTranslations = doc.createElement("PossibleTranslations");
            elemRule.appendChild(elemPosTranslations);
            //<Rule><PossibleTranslations><Pattern></Pattern>....</PossibleTranslations></Rule>
            for (Pattern p : possibleTranslations) {
                Element elemSinglePattern = doc.createElement("Pattern");
                elemPosTranslations.appendChild(elemSinglePattern);
                //<Rule><PossibleTranslations><Pattern>...</Pattern>[..]</PossibleTranslations></Rule>
                insertNodesInto(p.nodes, elemSinglePattern, doc);
            }

            /* Saves File at specific directory
                */

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            //formatting for human readability.
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            try {
                FileWriter fileWriter = new FileWriter(path);
                StreamResult streamResult = new StreamResult(fileWriter);
                transformer.transform(source, streamResult);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (TransformerException ex) {
            Log.print("Error outputting document", Log.LEVEL.ERROR);
        } catch (ParserConfigurationException ex) {
            Log.print("Error building document", Log.LEVEL.ERROR);
        } catch (Exception e) {
            Log.print("Rule::save(String): There was an error saving the Rule: ", Log.LEVEL.ERROR);
            e.printStackTrace();
        }
    }

    private void insertNodesInto(ArrayList<Node> nodes, Element elemNodes, Document doc) {
        //<Node><ID>id</ID><Tag>tag</Tag><X>x</X><Y>y</Y></Node>
        for (Node node : nodes) {
            Element elemNode = doc.createElement("Node");
            elemNodes.appendChild(elemNode);

            Element elemId = doc.createElement("ID");
            elemId.appendChild(doc.createTextNode(String.valueOf(node.getNodeId())));
            elemNode.appendChild(elemId);

            Element elemTag = doc.createElement("Tag");
            elemTag.appendChild(doc.createTextNode(String.valueOf(node.getType())));
            elemNode.appendChild(elemTag);

            Element elemX = doc.createElement("X");
            elemX.appendChild(doc.createTextNode(String.valueOf(node.getCenterX())));
            elemNode.appendChild(elemX);

            Element elemY = doc.createElement("Y");
            elemY.appendChild(doc.createTextNode(String.valueOf(node.getCenterY())));
            elemNode.appendChild(elemY);

            Element elemEdges = doc.createElement("Edges");
            elemNode.appendChild(elemEdges);
            for (Edge edge : node.getEdges()) {
                Element elemEdge = doc.createElement("Edge");
                elemEdges.appendChild(elemEdge);

                Element elemStart = doc.createElement("StartID");
                elemStart.appendChild(doc.createTextNode(String.valueOf(edge.getStartNode().getNodeId())));
                elemEdge.appendChild(elemStart);

                Element elemEnd = doc.createElement("EndID");
                elemEnd.appendChild(doc.createTextNode(String.valueOf(edge.getEndNode().getNodeId())));
                elemEdge.appendChild(elemEnd);
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if(o.hashCode() == this.hashCode()){
            if(o instanceof Rule){
                Rule tmp = (Rule) o;
                return super.equals(tmp)
                        && tmp.matchingPattern.equals(this.matchingPattern)
                        && tmp.possibleTranslations.equals(this.possibleTranslations); // maybe sort the lists.
            }else{
                return false;
            }

        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int code = 0;
        code += matchingPattern.hashCode()*3;
        code += possibleTranslations.hashCode()*5;
        return super.hashCode() + code;
    }

    public void replace(Pattern p) {
        Pattern tr = randomPossiblePattern();
        for (Node node : p.nodes) {
            ArrayList<Edge> outsideEdges = node.extractOutgoingEdges(p);
            Node n = findCorrespondingNode(node, tr);
            if(n != null){
                if(Rule.DEBUG_MODE)
                    Log.print("replace: found corresponding node; "+ "Type: " + n.getType() + ", id: " + n.getNodeId() + ", #edges: " + n.getEdges().size(), Log.LEVEL.INFO);

                n.addAllEdges(outsideEdges);
                for (Edge e :
                        n.getEdges()) {
                    e.replaceNode(n, node);
                }
                node.setCenterX(n.getCenterX());
                node.setCenterY(n.getCenterY());
                node.setEdges(n.getEdges());
                node.setType(n.getType());
                node.setNodeId(n.getNodeId());
            }
        }
        addAllNotIn(p, tr);
    }

    /**
     * adds all nodes in tr that are not in p,
     * @param p
     * pattern to add into
     * @param tr
     * pattern to take nodes from
     */
    public void addAllNotIn(Pattern p, Pattern tr) {
        for (Node node :
                tr.nodes) {
            boolean contains = false;
            for (Node n : p.nodes) {
                if(n.getNodeId() == node.getNodeId()){
                    contains = true;
                }
            }
            if(!contains){
                Log.print("addAllNotIn: adding node; " + "Type: " + node.getType() + ", id:" + node.getNodeId()
                        + ", #edges: " + node.getEdges().size(), Log.LEVEL.DEBUG);
                p.nodes.add(node);
            }
        }
    }

    private Node findCorrespondingNode(Node node, Pattern p) {
        Node ret = null;
        for (Node n :
                matchingPattern.nodes) {
            if (node.getType() == n.getType()){
                for (Node n2 : p.nodes) {
                    if (n2.getNodeId() == n.getNodeId())
                        return n2;
                }
            }
        }
        return ret;
    }


    public boolean nodeContainsSubPattern(Node node, ArrayList<Node> checkedNodes, Pattern p){
        boolean returnBool = false;
        if(checkedNodes.contains(node)){
            return true;
        }
        for (Node n : matchingPattern.nodes) {
            // find node in matching pattern with same type.
            if(node.getType() == n.getType()) {
                checkedNodes.add(node);
                Log.print("nodeContainsSubPattern: found matching type", Log.LEVEL.DEBUG);
                /**
                 * all edges in node n must be in node "node"
                 * also traverses the nodes to check
                 */
                returnBool = allEdgeAreContainedIn(n, node, checkedNodes, p);
                if(returnBool){
                    Log.print("nodeContainsSubPattern: edges were correct, adding to pattern", Log.LEVEL.DEBUG);
                    p.nodes.add(node);
                }
            }
            /**
             * once we have found a node which has the correct edges we can check all the subnodes to that node.
             * issue here is that we need to keep track of which nodes we have check in order to not have a circular dep.
             */

        }
        return returnBool;
    }
    private boolean allEdgeAreContainedIn(Node n, Node node, ArrayList<Node> checkedNodes, Pattern p){
        boolean returnBool = true;
        boolean nooneChecked = true;
        if(n.getEdges().size() > node.getEdges().size()){
            Log.print("allEdgeAreContainedIn: given node had less edges than other given node", Log.LEVEL.DEBUG);
            return false;
        }
        for (Edge e : n.getEdges()) { // for each edge in node from matching pattern
            for (Edge gE : node.getEdges()) { // for each edge in given node
                if(e.getStartNode() == n){ // if n is start node then we check end node
                    if(gE.getStartNode() == node){ // if node is start node in its edge
                        nooneChecked = false;
                        if(gE.getEndNode().getType() != e.getEndNode().getType()){
                            /**
                             * if they are not the same we need to continue to look, but we set flag to false to keep track
                             */
                            Log.print("allEdgeAreContainedIn: found edge where end nodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }else{
                            /**
                             * if we find a matching node-edge pair we can set flag to true and check this subnode for subpattern.
                             * finally we break loop
                             */
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = nodeContainsSubPattern(gE.getEndNode(), checkedNodes, p);
                            break;
                        }
                    }
                }else if(e.getEndNode() == n){
                    if(gE.getEndNode() == node){
                        nooneChecked = false;
                        if(gE.getStartNode().getType() != e.getStartNode().getType()){
                            Log.print("allEdgeAreContainedIn: found edge where start nodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }else{
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = nodeContainsSubPattern(gE.getStartNode(), checkedNodes, p);
                            break;
                        }
                    }
                }
            }
            if(nooneChecked){
                return false;
            }
        }
        Log.print("returning: " + returnBool, Log.LEVEL.DEBUG);
        return returnBool;
    }
    public boolean matches(Pattern p){
        return p.equals(matchingPattern);
    }

    private Pattern randomPossiblePattern() {
        return possibleTranslations.get(rand.nextInt(possibleTranslations.size()));
    }

    public void execute(Pattern pattern, Pattern p) {
        replace(p);
        addAllNotIn(pattern, p);
    }
}
