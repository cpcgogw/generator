package graph_generator.controller;

import javafx.util.Pair;
import model.DrawableEdge;
import model.DrawableNode;
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
     * Calls applyRandomRule n number of times
     * @param rules
     * Which rules to try and apply
     * @param n
     * number of times to try insertAndReplace
     * @param graph
     * The full graph on which which to apply the rules too
     */
    public void applyRandomRuleNTimes(ArrayList<Rule> rules, int n, Pattern graph){
        for (int i = 0; i < n; i++) {
            applyRandomRule(rules, graph);
        }
    }

    /**
     * Returns all rules that can be applied to the given pattern
     * @param rules
     * A list of rules to match vs the given pattern
     * @param graph
     * A pattern to match vs the rules (full graph)
     * @return
     * A list of pairs in which the first element in the pair is the rule that matched and the second is the subpattern
     * the rule matched against
     */
    public ArrayList<Pair<Rule, Pattern>> rulesMatchingPattern(List<Rule> rules, Pattern graph) {
        ArrayList<Pair<Rule, Pattern>> rulePatternList = new ArrayList<>();
        for (Rule r : rules) {
            rulePatternList.addAll(ruleMatchingPattern(r, graph));
        }

        return rulePatternList;
    }

    public ArrayList<Pair<Rule, Pattern>> ruleMatchingPattern(Rule r, Pattern graph) {
        ArrayList<Pair<Rule, Pattern>> rulePatternList = new ArrayList<>();
        for (int i = 0; i < graph.drawableNodes.size(); i++) {
            Log.print("rulesMatchingPattern: checking subpattern:", Log.LEVEL.DEBUG);
            Log.print("rulesMatchingPattern: " + graph.drawableNodes.get(i).toString(), Log.LEVEL.DEBUG);
            Log.print("vs", Log.LEVEL.DEBUG);
            Log.print(r.matchingPattern.drawableNodes.toString(), Log.LEVEL.DEBUG);


            Pattern p = new Pattern();
            boolean result = insertValidSubPatternFromRule(graph.drawableNodes.get(i), new ArrayList<DrawableNode>(), p, r);

            if(result){
                rulePatternList.add(new Pair<>(r,p));
            }

            Log.print("rulesMatchingPattern: "+result, Log.LEVEL.DEBUG);
            Log.print("rulesMatchingPattern: found: ", Log.LEVEL.DEBUG);
            Log.print(p.drawableNodes.toString(), Log.LEVEL.DEBUG);
        }
        return rulePatternList;
    }

    /**
     * Applies a random matching rule from a given set of rules
     * @param rules
     * Rules to try to apply
     * @param graph
     */
    public void applyRandomRule(List<Rule> rules, Pattern graph) {
        ArrayList<Pair<Rule, Pattern>> rulePatternList = rulesMatchingPattern(rules, graph);
        if (rulePatternList.size() > 0) {
            Pair<Rule, Pattern> pair = rulePatternList.get(random.nextInt(rulePatternList.size()));
            Rule r = pair.getKey();
            Pattern p = pair.getValue();
            applyRule(graph, p, r);
        }
    }


    public void insertAndReplace(Pattern graph, Pattern p, Rule rule) {
        Pattern tr = rule.randomPossiblePattern();
        for (DrawableNode drawableNode : p.drawableNodes) {
            DrawableNode n = findCorrespondingNode(drawableNode, tr, rule);
            if(n != null){
                Log.print("insertAndReplace: found corresponding drawableNode; "+ n, Log.LEVEL.INFO);
                n.removeEdgesToNodesWithType("ANY");
                for (DrawableEdge e :
                        n.getDrawableEdges()) {
                    e.replaceNode(n, drawableNode);
                }
                drawableNode.setCenterX(n.getCenterX());
                drawableNode.setCenterY(n.getCenterY());
                drawableNode.addAllEdges(n.getDrawableEdges());
                drawableNode.setType(n.getType());
                drawableNode.setNodeId(n.getNodeId());
            }
        }
        addAllNotIn(p, tr);
        addAllNotIn(graph, p);
    }

    /**
     * adds all drawableNodes in tr that are not in p,
     * @param p
     * pattern to add into
     * @param tr
     * pattern to take drawableNodes from
     */
    public void addAllNotIn(Pattern p, Pattern tr) {
        for (DrawableNode drawableNode :
                tr.drawableNodes) {
            boolean contains = false;
            for (DrawableNode n : p.drawableNodes) {
                if(n.getNodeId() == drawableNode.getNodeId()){
                    contains = true;
                }
            }
            if(!contains && !drawableNode.getType().equals("ANY")){
                Log.print("addAllNotIn: adding drawableNode; " + drawableNode, Log.LEVEL.DEBUG);

                p.drawableNodes.add(drawableNode.clone());
            }
        }
    }


    private DrawableNode findCorrespondingNode(DrawableNode drawableNode, Pattern p, Rule rule) {
        DrawableNode ret = null;
        for (DrawableNode n :
                rule.matchingPattern.drawableNodes) {
            if (drawableNode.getType().equals(n.getType())){
                for (DrawableNode n2 : p.drawableNodes) {
                    if (n2.getNodeId() == n.getNodeId())
                        return n2;
                }
            }
        }
        return ret;
    }

    /**
     * Checks if drawableNode 'drawableNode' is a valid drawableNode in the given rules matchingpattern if that is the case its added to the
     * pattern 'buildPattern' will return false if any valid 'drawableNode' found in matchingpattern has more than 8 edges.
     * After checking a drawableNode, if its valid and has more drawableNodes to check it will call itself with the next drawableNode and
     * add the checked drawableNode drawableNode to checkedNode list.
     * @param drawableNode
     * DrawableNode to check if valid in matchingPattern
     * @param checkedDrawableNodes
     * List of checked drawableNodes so we dont check same drawableNode twice
     * @param buildPattern
     * Valid subpattern built from 'drawableNode'
     * @param rule
     * Rule with the pattern we are matching against
     * @return
     * returns true if we have found a valid subpattern
     */
    private boolean insertValidSubPatternFromRule(DrawableNode drawableNode, ArrayList<DrawableNode> checkedDrawableNodes, Pattern buildPattern, Rule rule){
        boolean returnBool = false;
        if(checkedDrawableNodes.contains(drawableNode)){
            return true;
        }
        for (DrawableNode n : rule.matchingPattern.drawableNodes) {
            // find drawableNode in matching pattern with same type.
            if(drawableNode.getType().equals(n.getType()) || n.getType().equals("ANY")) {
                checkedDrawableNodes.add(drawableNode);
                Log.print("insertValidSubPatternFromRule: found matching type", Log.LEVEL.DEBUG);

                /*
                check so that drawableNode has less than 8 edges
                 */
                if(drawableNode.getDrawableEdges().size()>8){
                    Log.print("insertValidSubPatternFromRule: drawableNode has more than 8 edges; returning false", Log.LEVEL.DEBUG);
                    return false;
                }
                /*
                 * all edges in drawableNode n must be in drawableNode "drawableNode"
                 * also traverses the drawableNodes to check
                 */
                returnBool = allEdgeAreContainedIn(n, drawableNode, checkedDrawableNodes, buildPattern, rule);
                if(returnBool){
                    Log.print("insertValidSubPatternFromRule: edges were correct, adding to pattern", Log.LEVEL.DEBUG);
                    buildPattern.drawableNodes.add(drawableNode);
                }
            }
            /*
             * once we have found a drawableNode which has the correct edges we can check all the subnodes to that drawableNode.
             * issue here is that we need to keep track of which drawableNodes we have check in order to not have a circular dep.
             */

        }
        return returnBool;
    }

    /**
     * Checks so all that all edges that drawableNode 'n' has, drawableNode 'drawableNode' will have too.
     * @param n
     * @param drawableNode
     * @param checkedDrawableNodes
     * @param p
     * @param rule
     * @return
     */
    private boolean allEdgeAreContainedIn(DrawableNode n, DrawableNode drawableNode, ArrayList<DrawableNode> checkedDrawableNodes, Pattern p, Rule rule){
        boolean returnBool = true;
        boolean nooneChecked = false;
        if(n.getDrawableEdges().size() > drawableNode.getDrawableEdges().size()){
            Log.print("allEdgeAreContainedIn: given drawableNode had less edges than other given drawableNode", Log.LEVEL.DEBUG);
            return false;
        }
        for (DrawableEdge e : n.getDrawableEdges()) { // for each edge in drawableNode from matching pattern
            for (DrawableEdge gE : drawableNode.getDrawableEdges()) { // for each edge in given drawableNode
                nooneChecked = true;
                if(e.getStartDrawableNode() == n){ // if n is start drawableNode then we check end drawableNode
                    if(gE.getStartDrawableNode() == drawableNode){ // if drawableNode is start drawableNode in its edge
                        nooneChecked = false;
                        if(!gE.getEndDrawableNode().getType().equals(e.getEndDrawableNode().getType()) && !e.getEndDrawableNode().getType().equals("ANY")){
                            /*
                             * if they are not the same we need to continue to look, but we set flag to false to keep track
                             */
                            Log.print("allEdgeAreContainedIn: found edge where end drawableNodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }else{
                            /*
                             * if we find a matching drawableNode-edge pair we can set flag to true and check this subnode for subpattern.
                             * finally we break loop
                             */
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = insertValidSubPatternFromRule(gE.getEndDrawableNode(), checkedDrawableNodes, p, rule);
                            break;
                        }
                    }
                }else if(e.getEndDrawableNode() == n){
                    if(gE.getEndDrawableNode() == drawableNode){
                        nooneChecked = false;
                        if(!gE.getStartDrawableNode().getType().equals(e.getStartDrawableNode().getType()) && !e.getStartDrawableNode().getType().equals("ANY")){
                            Log.print("allEdgeAreContainedIn: found edge where start drawableNodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }else{
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = insertValidSubPatternFromRule(gE.getStartDrawableNode(), checkedDrawableNodes, p, rule);
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
    public void applyRule(Pattern graph, Pattern p, Rule rule) {
        graph.resetIds();
        insertAndReplace(graph, p, rule);
    }

}
