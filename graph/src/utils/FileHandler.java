package utils;

import javafx.util.Pair;
import model.*;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This is to save and/or store drawableAreaNodes & edges
 *
 *
 */

public class FileHandler {

    public static void saveNodes(ArrayList<? extends AreaNode> areaNodes, String path) {

        try {

            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            Element elemNodes = doc.createElement("Nodes");

            doc.appendChild(elemNodes);

            insertNodesInto(areaNodes, elemNodes, doc);
            saveDocAt(doc, path);

        } catch (TransformerException ex) {
            System.out.println("Error outputting document");

        } catch (ParserConfigurationException ex) {
            System.out.println("Error building document");
        }
    }

    private static void saveDocAt(Document doc, String path) throws TransformerConfigurationException {
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

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Pair<ArrayList<DrawableAreaNode>,ArrayList<DrawableEdge>> loadNodes(File file){
        HashMap<Integer, model.Node> nodeMap = new HashMap<>();
        ArrayList<DrawableEdge> drawableEdges = new ArrayList<>();

        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize(); //normalizes document


            //Defines all drawableAreaNodes...

            NodeList xnodeList = doc.getElementsByTagName("AreaNode"); //grab all "AreaNode" from XML-file
            nodeMap = extractNodes(xnodeList);



            //Defines all drawableEdges...
            HashMap<Integer,ArrayList<Integer>> edgeMap = new HashMap<>(); //HashMap used for easy fix of duplicate drawableEdges.
            xnodeList = doc.getElementsByTagName("Edge");//grab all "Edge" from XML-file
            //edgeMap = extractEdges(xnodeList, nodeMap, drawableEdges);


        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfigurationException: ");
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("SAXException: ");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException: ");
            e.printStackTrace();
        }

        //Returns the entries as an ArrayList
        Pair pair = new Pair(new ArrayList<>(nodeMap.values()), drawableEdges);
        return pair;
    }

    public static List<List<DrawableAreaNode>> loadTranslations(File file) {
        List<List<DrawableAreaNode>> translations = new ArrayList<>();

        try {
            DocumentBuilder document = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = document.parse(file);

            doc.getDocumentElement().normalize();

            Element elemTranslations = (Element) doc.getElementsByTagName("PossibleTranslations").item(0);
            translations = extractTranslations(elemTranslations);

            for (List<DrawableAreaNode> translation : translations) {
                for (DrawableAreaNode node : translation) {
                    node.updateSubnodes();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return translations;
    }

    /**
     * Extracts all translations from element.
     *
     * @param element
     * The element to extract from.
     * @return
     * A list of translations as a list of list of nodes.
     */
    private static List<List<DrawableAreaNode>> extractTranslations(Element element) {
        List<List<DrawableAreaNode>> translations = new ArrayList<>();
        List<Set<DrawableEdge>> edges = new ArrayList<>();
        Set<DrawableEdge> edge = new HashSet<>();
        NodeList translationNodes = element.getElementsByTagName("Pattern");

        for (int i = 0; i < translationNodes.getLength(); i++) {
            Element elemTranslation = (Element) translationNodes.item(i);
            List<DrawableAreaNode> translation = extractNodes(elemTranslation);
            translations.add(translation);
        }
        for (int i = 0; i < translationNodes.getLength(); i++) {
            Element elemTranslation = (Element) translationNodes.item(i);
            List<DrawableAreaNode> translation = extractNodes(elemTranslation);
            edge = extractEdges(elemTranslation, translations.get(i));
            edges.add(edge);
        }

        return translations;
    }

    private static ArrayList<NodeList> extractTranslations(Document doc) {
        ArrayList<NodeList> nodeListList = new ArrayList<NodeList>();
        Element posTrans = (Element) doc.getElementsByTagName("PossibleTranslations").item(0);
        NodeList patterns = posTrans.getElementsByTagName("Pattern");
        for (int i = 0; i < patterns.getLength(); i++) {
            nodeListList.add((NodeList) patterns.item(i));
        }
        return nodeListList;
    }

    public static List<DrawableAreaNode> loadMatch(File file) {
        List<DrawableAreaNode> nodes = new ArrayList<>();
        Set<DrawableEdge> edges = new HashSet<>();

        try {
            DocumentBuilder document = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = document.parse(file);

            doc.getDocumentElement().normalize();

            Element match = (Element) doc.getElementsByTagName("MatchingPattern").item(0);
            Element pattern = (Element) match.getElementsByTagName("Pattern").item(0);

            nodes = extractNodes(pattern);
            edges = extractEdges(pattern, nodes);

            for (DrawableAreaNode node : nodes) {
                node.updateSubnodes();
            }

        } catch (Exception e) {
            Log.print("FileHandler: Match failed to load!", Log.LEVEL.ERROR);
            e.printStackTrace();
        }

        return nodes;
    }

    /**
     * Used to extract all nodes from a given xml element.
     *
     * @param element
     * Element to extract nodes from.
     * @return
     * A list of DrawableAreaNodes'.
     */
    private static List<DrawableAreaNode> extractNodes(Element element) {
        ArrayList<DrawableAreaNode> nodes = new ArrayList<>();
        NodeList areaNodes = element.getElementsByTagName("AreaNode");

        for (int i = 0; i < areaNodes.getLength(); i++) {
            Element elemNode = (Element) areaNodes.item(i);
            DrawableAreaNode node = extractNode(elemNode);
            nodes.add(node);
        }

        return nodes;
    }

    /**
     * Extracts a single DrawableAreaNode from a xml element.
     *
     * @param element
     * Element to extract node from.
     * @return
     * The extracted DrawableAreaNode.
     */
    private static DrawableAreaNode extractNode(Element element) {
        int id = Integer.parseInt(element.getAttribute("ID"));
        int x = Integer.parseInt(element.getAttribute("X"));
        int y = Integer.parseInt(element.getAttribute("Y"));
        AREA_TYPE type = AREA_TYPE.valueOf(element.getAttribute("Type"));

        Element subnodes = (Element) element.getElementsByTagName("SubNodes").item(0);

        List<DrawableSubnode> subNodes = extractSubnodes(subnodes);
        DrawableAreaNode node = new DrawableAreaNode(x, y, id, type);
        node.setSubnodes(subNodes);

        return node;
    }

    /**
     * Extracts subnodes from given xml element.
     *
     * @param element
     * Element to extract subnodes from.
     * @return
     * A list of the extracted subnodes.
     */
    private static List<DrawableSubnode> extractSubnodes(Element element) {
        ArrayList<DrawableSubnode> subnodes = new ArrayList<>();
        NodeList subnodeNodes = element.getElementsByTagName("SubNode");

        for (int i = 0; i < subnodeNodes.getLength(); i++) {
            Element elemNode = (Element) subnodeNodes.item(i);
            DrawableSubnode subnode = extractSubnode(elemNode);
            subnodes.add(subnode);
        }

        return subnodes;
    }

    /**
     * Extracts a single DrawableSubnode from a xml element.
     *
     * @param element
     * Element to extract from.
     * @return
     * The extracted DrawableSubnode.
     */
    private static DrawableSubnode extractSubnode(Element element) {
        int id = Integer.parseInt(element.getAttribute("ID"));
        OBJECT_TYPE type = OBJECT_TYPE.valueOf(element.getAttribute("Type"));

        return new DrawableSubnode(0, 0, id, type);
    }

    /**
     * Extracts all edges from xml element.
     *
     * @param element
     * Element to extract from.
     * @return
     * A list with all extracted edges.
     */
    private static Set<DrawableEdge> extractEdges(Element element, List<DrawableAreaNode> nodes) {
        Set<DrawableEdge> edges = new HashSet<>();
        NodeList edgeNodes = element.getElementsByTagName("Edge");
        List<Integer> finds = new ArrayList<>();

        for (DrawableAreaNode node : nodes) {
            finds.add(node.getNodeId());
            for (DrawableSubnode subnode : node.getDrawableSubnodes()) {
                finds.add(subnode.getNodeId());
            }
        }

        Log.print("FileHandler: Sending in nodes with following ID's: "+finds, Log.LEVEL.DEBUG);

        for (int i = 0; i < edgeNodes.getLength(); i++) {
            Element elemEdge = (Element) edgeNodes.item(i);
            DrawableEdge edge = extractEdge(elemEdge, nodes);
            edges.add(edge);
        }

        return edges;
    }

    /**
     * Extracts a single edge from element.
     *
     * @param element
     * Element to extract from.
     * @return
     * The extracted DrawableEdge.
     */
    private static DrawableEdge extractEdge(Element element, List<DrawableAreaNode> nodes) {
        int startID = Integer.parseInt(element.getAttribute("StartID"));
        int endID = Integer.parseInt(element.getAttribute("EndID"));
        //TODO: Fix so Node is used instead of DrawableAreaNode/DrawableSubnode
        DrawableAreaNode start = null;
        DrawableAreaNode end = null;
        DrawableSubnode startS = null;
        DrawableSubnode endS = null;

        for (DrawableAreaNode node : nodes) {
            if (node.getNodeId() == startID) {
                start = node;
            } else if (node.getNodeId() == endID) {
                end = node;
            }

            for (DrawableSubnode subnode : node.getDrawableSubnodes()) {
                if (subnode.getNodeId() == startID) {
                    startS = subnode;
                } else if (subnode.getNodeId() == endID) {
                    endS = subnode;
                }
            }
        }

        DrawableEdge edge;

        // Absolute cancer
        if (start == null && end == null) {
            edge = new DrawableEdge(endS, startS);
        } else {
            edge = new DrawableEdge(start, end);
        }

        return edge;
    }

    private static HashMap<Integer, model.Node> extractNodes(NodeList xnodeList) {
        HashMap<Integer, model.Node> nodeMap = new HashMap<>();
        for(int i = 0; i < xnodeList.getLength(); i++){
            org.w3c.dom.Node xNode = xnodeList.item(i);

            if (xNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element) xNode;

                //Extract elements of drawableAreaNode
                int id = Integer.parseInt(element.getElementsByTagName("ID").item(0).getTextContent());
                AREA_TYPE type = AREA_TYPE.valueOf(element.getElementsByTagName("Tag").item(0).getTextContent());
                double x = Double.parseDouble(element.getElementsByTagName("X").item(0).getTextContent());
                double y = Double.parseDouble(element.getElementsByTagName("Y").item(0).getTextContent());
                //Store the extracted DrawableAreaNode
                DrawableAreaNode drawableAreaNode = new DrawableAreaNode(x, y, id, type);

                nodeMap.put(id, drawableAreaNode);
                //extract subnodes
                NodeList subNodeList = ((Element) xNode).getElementsByTagName("SubNode");
                for (int j = 0; j < subNodeList.getLength(); j++) {
                    Node sNode = subNodeList.item(j);
                    int nodeId = Integer.parseInt(sNode.getAttributes().getNamedItem("id").getNodeValue());
                    OBJECT_TYPE objType = OBJECT_TYPE.valueOf(sNode.getAttributes().getNamedItem("type").getNodeValue());
                    SubNode node = new SubNode(nodeId, objType);
                    drawableAreaNode.addObject(node);
                    nodeMap.put(nodeId, node);
                }
            }
        }

        return nodeMap;
    }

    public static void saveRule(Rule rule, String path){
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
            //<Rule><MatchingPattern><DrawablePattern></DrawablePattern></MatchingPattern></Rule>
            Element elemNodes = doc.createElement("Pattern");
            elemMatchingPattern.appendChild(elemNodes);
            //<Rule><MatchingPattern><DrawablePattern>...</DrawablePattern></MatchingPattern></Rule>
            insertNodesInto(rule.matchingDrawablePattern.getNodes(), elemNodes, doc);


            //<Rule><PossibleTranslations></PossibleTranslations></Rule>
            Element elemPosTranslations = doc.createElement("PossibleTranslations");
            elemRule.appendChild(elemPosTranslations);
            //<Rule><PossibleTranslations><DrawablePattern></DrawablePattern>....</PossibleTranslations></Rule>
            for (DrawablePattern p : rule.possibleTranslations) {
                Element elemSinglePattern = doc.createElement("Pattern");
                elemPosTranslations.appendChild(elemSinglePattern);
                //<Rule><PossibleTranslations><DrawablePattern>...</DrawablePattern>[..]</PossibleTranslations></Rule>
                insertNodesInto(p.getNodes(), elemSinglePattern, doc);
            }

            //Saves File at specific directory

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            //formatting for human readability.
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            try {
                File saves = new File("saves");
                File rules = new File("saves/rules");
                File levels = new File("saves/levels");
                if (!saves.exists())
                    saves.mkdir();
                if (!rules.exists())
                    rules.mkdir();
                if (!levels.exists())
                    levels.mkdir();

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

    private static void insertSubNodesInto(List<? extends Subnode> subnodes, Element element, Document doc) {
        for (Subnode subnode : subnodes) {
            Element elemSubnode = doc.createElement("SubNode");
            elemSubnode.setAttribute("ID", String.valueOf(subnode.getNodeId()));
            elemSubnode.setAttribute("Type", String.valueOf(subnode.getType()));
            Element elemEdges = doc.createElement("Edges");
            insertEdgesInto(subnode.getEdges(), elemEdges, doc);
            elemSubnode.appendChild(elemEdges);
            element.appendChild(elemSubnode);
        }
    }

    private static void insertNodesInto(ArrayList<? extends AreaNode> areaNodes, Element elemNodes, Document doc) {
        //<DrawableAreaNode><ID>id</ID><Tag>tag</Tag><X>x</X><Y>y</Y></DrawableAreaNode>
        for (AreaNode areaNode : areaNodes) {
            Element elemNode = doc.createElement("AreaNode");
            elemNodes.appendChild(elemNode);

            Element elemObjects = doc.createElement("SubNodes");
            elemNode.appendChild(elemObjects);
            insertSubNodesInto(areaNode.getSubnodes(), elemObjects, doc);

            elemNode.setAttribute("ID", String.valueOf(areaNode.getNodeId()));
            elemNode.setAttribute("Type", String.valueOf(areaNode.getType()));

            int x = 0, y = 0;
            if(areaNode instanceof DrawableAreaNode){
                x = (int) ((DrawableAreaNode) areaNode).getCenterX();
                y = (int) ((DrawableAreaNode) areaNode).getCenterY();
            }

            elemNode.setAttribute("X", String.valueOf(x));
            elemNode.setAttribute("Y", String.valueOf(y));

            Element elemEdges = doc.createElement("Edges");
            elemNode.appendChild(elemEdges);

            insertEdgesInto(areaNode.getEdges(), elemEdges, doc);
        }
    }

    private static void insertEdgesInto(List<? extends Edge> edges, Element elemEdges, Document doc) {
        for (Edge edge : edges) {
            Element elemEdge = doc.createElement("Edge");
            elemEdges.appendChild(elemEdge);

            elemEdge.setAttribute("EndID", String.valueOf(edge.getTo().getNodeId()));
            elemEdge.setAttribute("StartID", String.valueOf(edge.getFrom().getNodeId()));
        }
    }
}

