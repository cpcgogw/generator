package graph_generator.controller;

import javafx.util.Pair;
import model.Edge;
import model.Node;
import model.Pattern;
import model.Rule;
import utils.Log;

import java.util.ArrayList;
import java.util.List;
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
    public ArrayList<Pair<Rule, Pattern>> rulesMatchingPattern(List<Rule> rules, Pattern pattern) {
        ArrayList<Pair<Rule, Pattern>> rulePatternList = new ArrayList<>();
        for (Rule r : rules) {
            for (int i = 0; i < pattern.nodes.size(); i++) {

                Log.print("rulesMatchingPattern: checking subpattern:", Log.LEVEL.DEBUG);
                Log.print("rulesMatchingPattern: " + pattern.nodes.get(i).toString(), Log.LEVEL.DEBUG);
                Log.print("vs", Log.LEVEL.DEBUG);
                for (Node n : r.matchingPattern.nodes) {
                    Log.print(n.toString(), Log.LEVEL.DEBUG);
                }

                Pattern p = new Pattern();
                boolean result = insertValidSubPatternFromRule(pattern.nodes.get(i), new ArrayList<Node>(), p, r);

                if(result){
                    rulePatternList.add(new Pair<>(r,p));
                }

                Log.print("rulesMatchingPattern: "+result, Log.LEVEL.DEBUG);
                Log.print("rulesMatchingPattern: found: ", Log.LEVEL.DEBUG);
                for (Node n : p.nodes) {
                    Log.print("rulesMatchingPattern: " + n, Log.LEVEL.DEBUG);
                }
            }
        }

        return rulePatternList;
    }

    /**
     * Applies a random matching rule from a given set of rules
     * @param rules
     * Rules to try to apply
     * @param graph
     */
    public void applyRandomMatchingRule(List<Rule> rules, Pattern graph) {
        ArrayList<Pair<Rule, Pattern>> rulePatternList = rulesMatchingPattern(rules, graph);
        if (rulePatternList.size() > 0) {
            Pair<Rule, Pattern> pair = rulePatternList.get(random.nextInt(rulePatternList.size()));
            Rule r = pair.getKey();
            Pattern p = pair.getValue();
            applyRule(graph, p, r);
        }
    }


    public void replace(Pattern p, Rule rule) {
        Pattern tr = rule.randomPossiblePattern();
        for (Node node : p.nodes) {
            Node n = findCorrespondingNode(node, tr, rule);
            if(n != null){
                Log.print("replace: found corresponding node; "+ n, Log.LEVEL.INFO);
                n.removeEdgesToNodesWithType("ANY");
                for (Edge e :
                        n.getEdges()) {
                    e.replaceNode(n, node);
                }
                node.setCenterX(n.getCenterX());
                node.setCenterY(n.getCenterY());
                node.addAllEdges(n.getEdges());
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
            if(!contains && !node.getType().equals("ANY")){
                Log.print("addAllNotIn: adding node; " + node, Log.LEVEL.DEBUG);

                p.nodes.add(node.clone());
            }
        }
    }


    private Node findCorrespondingNode(Node node, Pattern p, Rule rule) {
        Node ret = null;
        for (Node n :
                rule.matchingPattern.nodes) {
            if (node.getType().equals(n.getType())){
                for (Node n2 : p.nodes) {
                    if (n2.getNodeId() == n.getNodeId())
                        return n2;
                }
            }
        }
        return ret;
    }

    /**
     * Checks if node 'node' is a valid node in the given rules matchingpattern if that is the case its added to the
     * pattern 'buildPattern' will return false if any valid 'node' found in matchingpattern has more than 8 edges.
     * After checking a node, if its valid and has more nodes to check it will call itself with the next node and
     * add the checked node node to checkedNode list.
     * @param node
     * Node to check if valid in matchingPattern
     * @param checkedNodes
     * List of checked nodes so we dont check same node twice
     * @param buildPattern
     * Valid subpattern built from 'node'
     * @param rule
     * Rule with the pattern we are matching against
     * @return
     * returns true if we have found a valid subpattern
     */
    private boolean insertValidSubPatternFromRule(Node node, ArrayList<Node> checkedNodes, Pattern buildPattern, Rule rule){
        boolean returnBool = false;
        if(checkedNodes.contains(node)){
            return true;
        }
        for (Node n : rule.matchingPattern.nodes) {
            // find node in matching pattern with same type.
            if(node.getType().equals(n.getType()) || n.getType().equals("ANY")) {
                checkedNodes.add(node);
                Log.print("insertValidSubPatternFromRule: found matching type", Log.LEVEL.DEBUG);

                /*
                check so that node has less than 8 edges
                 */
                if(node.getEdges().size()>8){
                    Log.print("insertValidSubPatternFromRule: node has more than 8 edges; returning false", Log.LEVEL.DEBUG);
                    return false;
                }
                /*
                 * all edges in node n must be in node "node"
                 * also traverses the nodes to check
                 */
                returnBool = allEdgeAreContainedIn(n, node, checkedNodes, buildPattern, rule);
                if(returnBool){
                    Log.print("insertValidSubPatternFromRule: edges were correct, adding to pattern", Log.LEVEL.DEBUG);
                    buildPattern.nodes.add(node);
                }
            }
            /*
             * once we have found a node which has the correct edges we can check all the subnodes to that node.
             * issue here is that we need to keep track of which nodes we have check in order to not have a circular dep.
             */

        }
        return returnBool;
    }

    /**
     * Checks so all that all edges that node 'n' has, node 'node' will have too.
     * @param n
     * @param node
     * @param checkedNodes
     * @param p
     * @param rule
     * @return
     */
    private boolean allEdgeAreContainedIn(Node n, Node node, ArrayList<Node> checkedNodes, Pattern p, Rule rule){
        boolean returnBool = true;
        boolean nooneChecked = false;
        if(n.getEdges().size() > node.getEdges().size()){
            Log.print("allEdgeAreContainedIn: given node had less edges than other given node", Log.LEVEL.DEBUG);
            return false;
        }
        for (Edge e : n.getEdges()) { // for each edge in node from matching pattern
            for (Edge gE : node.getEdges()) { // for each edge in given node
                nooneChecked = true;
                if(e.getStartNode() == n){ // if n is start node then we check end node
                    if(gE.getStartNode() == node){ // if node is start node in its edge
                        nooneChecked = false;
                        if(!gE.getEndNode().getType().equals(e.getEndNode().getType()) && !e.getEndNode().getType().equals("ANY")){
                            /*
                             * if they are not the same we need to continue to look, but we set flag to false to keep track
                             */
                            Log.print("allEdgeAreContainedIn: found edge where end nodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }else{
                            /*
                             * if we find a matching node-edge pair we can set flag to true and check this subnode for subpattern.
                             * finally we break loop
                             */
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = insertValidSubPatternFromRule(gE.getEndNode(), checkedNodes, p, rule);
                            break;
                        }
                    }
                }else if(e.getEndNode() == n){
                    if(gE.getEndNode() == node){
                        nooneChecked = false;
                        if(!gE.getStartNode().getType().equals(e.getStartNode().getType()) && !e.getStartNode().getType().equals("ANY")){
                            Log.print("allEdgeAreContainedIn: found edge where start nodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }else{
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = insertValidSubPatternFromRule(gE.getStartNode(), checkedNodes, p, rule);
                            break;
                        }
                    }
                }
            }
            if(nooneChecked){
                Log.print("allEdgeAreContainedIn: returning false, nothing was checked", Log.LEVEL.DEBUG);
                return false;
            }
        }
        Log.print("allEdgeAreContainedIn: returning: " + returnBool, Log.LEVEL.DEBUG);
        return returnBool;
    }

    /**
     * Applies rule to subpattern in graph.
     *
     * @param graph
     * The graph to which we apply the given rule.
     * @param p
     * The subpattern inside the graph which we apply the rule to.
     * @param rule
     * The rule which we apply to the subpattern in the graph.
     */
    public void applyRule(Pattern graph, Pattern p,Rule rule) {
        graph.resetIds();
        replace(p, rule);
        addAllNotIn(graph, p);
    }

}
