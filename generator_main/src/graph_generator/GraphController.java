package graph_generator;

import javafx.util.Pair;
import rule_editor.Log;
import rule_editor.model.Edge;
import rule_editor.model.Node;
import rule_editor.model.Pattern;
import rule_editor.model.Rule;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vilddjur on 2/28/17.
 */
public class GraphController {
    private Random random;
    public GraphController(){
        random = new Random();
    }
    /**
     * Calls applyRandomMatchingRule n number of times
     * @param rules
     * Which rules to try and apply
     * @param n
     * number of times to try replace
     */
    public void applyRandomMatchingRuleNTimes(ArrayList<Rule> rules, int n, Pattern pattern){
        for (int i = 0; i < n; i++) {
            applyRandomMatchingRule(rules, pattern);
        }
    }

    /**
     * Returns all rules that can be applied to the given pattern
     * @param rules
     * A list of rules to match vs the given pattern
     * @param pattern
     * A pattern to match vs the rules
     * @return
     * A list of pairs in which the first element in the pair is the rule that matched and the second is the subpattern
     * the rule matched against
     */
    public ArrayList<Pair<Rule, Pattern>> rulesMatchingPattern(ArrayList<Rule> rules, Pattern pattern) {
        ArrayList<Pair<Rule, Pattern>> rulePatternList = new ArrayList<>();
        for (Rule r : rules) {
            for (int i = 0; i < pattern.nodes.size(); i++) {

                Log.print("checking subpattern:", Log.LEVEL.DEBUG);
                Log.print(info(pattern.nodes.get(i)), Log.LEVEL.DEBUG);
                Log.print("vs", Log.LEVEL.DEBUG);
                for (Node n : r.matchingPattern.nodes) {
                    Log.print(info(n), Log.LEVEL.DEBUG);
                }

                Pattern p = new Pattern();
                boolean result = nodeContainsSubPattern(pattern.nodes.get(i), new ArrayList<Node>(), p, r);

                if(result){
                    rulePatternList.add(new Pair<>(r,p));
                }

                Log.print(""+result, Log.LEVEL.DEBUG);
                Log.print("found: ", Log.LEVEL.DEBUG);
                for (Node n : p.nodes) {
                    Log.print(" Type: " + n.getType() + ", id:" + n.getNodeId() + ", #edges: " + n.getEdges().size(), Log.LEVEL.DEBUG);
                }
            }
        }

        return rulePatternList;
    }

    /**
     * Applies a random matching rule from a given set of rules
     * @param rules
     * Rules to try to apply
     * @param pattern
     * pattern to apply the rules to
     */
    public void applyRandomMatchingRule(ArrayList<Rule> rules, Pattern pattern) {
        ArrayList<Pair<Rule, Pattern>> rulePatternList = rulesMatchingPattern(rules, pattern);
        if (rulePatternList.size() > 0) {
            Pair<Rule, Pattern> pair = rulePatternList.get(random.nextInt(rulePatternList.size()));
            Rule r = pair.getKey();
            Pattern p = pair.getValue();
            pattern.resetIds();
            execute(pattern, p, r);
        }
    }

    public void replace(Pattern p, Rule rule) {
        Pattern tr = rule.randomPossiblePattern();
        for (Node node : p.nodes) {
            ArrayList<Edge> outsideEdges = node.extractOutgoingEdges(p);
            Node n = findCorrespondingNode(node, tr, rule);
            if(n != null){
                Log.print("replace: found corresponding node; "+ info(n), Log.LEVEL.INFO);
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
                Log.print("addAllNotIn: adding node; " + info(node), Log.LEVEL.DEBUG);
                p.nodes.add(node);
            }
        }
    }


    private Node findCorrespondingNode(Node node, Pattern p, Rule rule) {
        Node ret = null;
        for (Node n :
                rule.matchingPattern.nodes) {
            if (node.getType() == n.getType()){
                for (Node n2 : p.nodes) {
                    if (n2.getNodeId() == n.getNodeId())
                        return n2;
                }
            }
        }
        return ret;
    }

    public boolean nodeContainsSubPattern(Node node, ArrayList<Node> checkedNodes, Pattern p, Rule rule){
        boolean returnBool = false;
        if(checkedNodes.contains(node)){
            return true;
        }
        for (Node n : rule.matchingPattern.nodes) {
            // find node in matching pattern with same type.
            if(node.getType() == n.getType()) {
                checkedNodes.add(node);
                Log.print("nodeContainsSubPattern: found matching type", Log.LEVEL.DEBUG);
                /**
                 * all edges in node n must be in node "node"
                 * also traverses the nodes to check
                 */
                returnBool = allEdgeAreContainedIn(n, node, checkedNodes, p, rule);
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

    private boolean allEdgeAreContainedIn(Node n, Node node, ArrayList<Node> checkedNodes, Pattern p, Rule rule){
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
                            returnBool = nodeContainsSubPattern(gE.getEndNode(), checkedNodes, p, rule);
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
                            returnBool = nodeContainsSubPattern(gE.getStartNode(), checkedNodes, p, rule);
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
    public void execute(Pattern pattern, Pattern p,Rule rule) {
        replace(p, rule);
        addAllNotIn(pattern, p);
    }
    private String info(Node node) {
        return " Type: " + node.getType() + ", id:" + node.getNodeId() + ", #edges: " + node.getEdges().size();
    }

}
