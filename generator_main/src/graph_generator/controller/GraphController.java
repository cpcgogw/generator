package graph_generator.controller;

import javafx.util.Pair;
import model.DrawableAreaNode;
import model.DrawableEdge;
import model.DrawablePattern;
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
    public void applyRandomRuleNTimes(ArrayList<Rule> rules, int n, DrawablePattern graph){
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
    public ArrayList<Pair<Rule, DrawablePattern>> rulesMatchingPattern(List<Rule> rules, DrawablePattern graph) {
        ArrayList<Pair<Rule, DrawablePattern>> rulePatternList = new ArrayList<>();
        for (Rule r : rules) {
            rulePatternList.addAll(ruleMatchingPattern(r, graph));
        }

        return rulePatternList;
    }

    public ArrayList<Pair<Rule, DrawablePattern>> ruleMatchingPattern(Rule r, DrawablePattern graph) {
        ArrayList<Pair<Rule, DrawablePattern>> rulePatternList = new ArrayList<>();
        for (int i = 0; i < graph.drawableAreaNodes.size(); i++) {
            Log.print("rulesMatchingPattern: checking subpattern:", Log.LEVEL.DEBUG);
            Log.print("rulesMatchingPattern: " + graph.drawableAreaNodes.get(i).toString(), Log.LEVEL.DEBUG);
            Log.print("vs", Log.LEVEL.DEBUG);
            Log.print(r.matchingDrawablePattern.drawableAreaNodes.toString(), Log.LEVEL.DEBUG);


            DrawablePattern p = new DrawablePattern();
            boolean result = insertValidSubPatternFromRule(graph.drawableAreaNodes.get(i), new ArrayList<DrawableAreaNode>(), p, r);

            if(result){
                rulePatternList.add(new Pair<>(r,p));
            }

            Log.print("rulesMatchingPattern: "+result, Log.LEVEL.DEBUG);
            Log.print("rulesMatchingPattern: found: ", Log.LEVEL.DEBUG);
            Log.print(p.drawableAreaNodes.toString(), Log.LEVEL.DEBUG);
        }
        return rulePatternList;
    }

    /**
     * Applies a random matching rule from a given set of rules
     * @param rules
     * Rules to try to apply
     * @param graph
     */
    public void applyRandomRule(List<Rule> rules, DrawablePattern graph) {
        ArrayList<Pair<Rule, DrawablePattern>> rulePatternList = rulesMatchingPattern(rules, graph);
        if (rulePatternList.size() > 0) {
            Pair<Rule, DrawablePattern> pair = rulePatternList.get(random.nextInt(rulePatternList.size()));
            Rule r = pair.getKey();
            DrawablePattern p = pair.getValue();
            applyRule(graph, p, r);
        }
    }


    public void insertAndReplace(DrawablePattern graph, DrawablePattern p, Rule rule) {
        DrawablePattern tr = rule.randomPossiblePattern();
        for (DrawableAreaNode drawableAreaNode : p.drawableAreaNodes) {
            DrawableAreaNode n = findCorrespondingNode(drawableAreaNode, tr, rule);
            if(n != null){
                Log.print("insertAndReplace: found corresponding drawableAreaNode; "+ n, Log.LEVEL.INFO);
                n.removeEdgesToNodesWithType("ANY");
                for (DrawableEdge e :
                        n.getDrawableEdges()) {
                    e.replaceNode(n, drawableAreaNode);
                }
                drawableAreaNode.setCenterX(n.getCenterX());
                drawableAreaNode.setCenterY(n.getCenterY());
                drawableAreaNode.addAllEdges(n.getDrawableEdges());
                drawableAreaNode.setType(n.getType());
                drawableAreaNode.setNodeId(n.getNodeId());
            }
        }
        addAllNotIn(p, tr);
        addAllNotIn(graph, p);
    }

    /**
     * adds all drawableAreaNodes in tr that are not in p,
     * @param p
     * pattern to add into
     * @param tr
     * pattern to take drawableAreaNodes from
     */
    public void addAllNotIn(DrawablePattern p, DrawablePattern tr) {
        for (DrawableAreaNode drawableAreaNode :
                tr.drawableAreaNodes) {
            boolean contains = false;
            for (DrawableAreaNode n : p.drawableAreaNodes) {
                if(n.getNodeId() == drawableAreaNode.getNodeId()){
                    contains = true;
                }
            }
            if(!contains && !drawableAreaNode.getType().equals("ANY")){
                Log.print("addAllNotIn: adding drawableAreaNode; " + drawableAreaNode, Log.LEVEL.DEBUG);

                p.drawableAreaNodes.add(drawableAreaNode.clone());
            }
        }
    }


    private DrawableAreaNode findCorrespondingNode(DrawableAreaNode drawableAreaNode, DrawablePattern p, Rule rule) {
        DrawableAreaNode ret = null;
        for (DrawableAreaNode n :
                rule.matchingDrawablePattern.drawableAreaNodes) {
            if (drawableAreaNode.getType().equals(n.getType())){
                for (DrawableAreaNode n2 : p.drawableAreaNodes) {
                    if (n2.getNodeId() == n.getNodeId())
                        return n2;
                }
            }
        }
        return ret;
    }

    /**
     * Checks if drawableAreaNode 'drawableAreaNode' is a valid drawableAreaNode in the given rules matchingpattern if that is the case its added to the
     * pattern 'buildDrawablePattern' will return false if any valid 'drawableAreaNode' found in matchingpattern has more than 8 edges.
     * After checking a drawableAreaNode, if its valid and has more drawableAreaNodes to check it will call itself with the next drawableAreaNode and
     * add the checked drawableAreaNode drawableAreaNode to checkedNode list.
     * @param drawableAreaNode
     * DrawableAreaNode to check if valid in matchingDrawablePattern
     * @param checkedDrawableAreaNodes
     * List of checked drawableAreaNodes so we dont check same drawableAreaNode twice
     * @param buildDrawablePattern
     * Valid subpattern built from 'drawableAreaNode'
     * @param rule
     * Rule with the pattern we are matching against
     * @return
     * returns true if we have found a valid subpattern
     */
    private boolean insertValidSubPatternFromRule(DrawableAreaNode drawableAreaNode, ArrayList<DrawableAreaNode> checkedDrawableAreaNodes, DrawablePattern buildDrawablePattern, Rule rule){
        boolean returnBool = false;
        if(checkedDrawableAreaNodes.contains(drawableAreaNode)){
            return true;
        }
        for (DrawableAreaNode n : rule.matchingDrawablePattern.drawableAreaNodes) {
            // find drawableAreaNode in matching pattern with same type.
            if(drawableAreaNode.getType().equals(n.getType()) || n.getType().equals("ANY")) {
                checkedDrawableAreaNodes.add(drawableAreaNode);
                Log.print("insertValidSubPatternFromRule: found matching type", Log.LEVEL.DEBUG);

                /*
                check so that drawableAreaNode has less than 8 edges
                 */
                if(drawableAreaNode.getDrawableEdges().size()>8){
                    Log.print("insertValidSubPatternFromRule: drawableAreaNode has more than 8 edges; returning false", Log.LEVEL.DEBUG);
                    return false;
                }
                /*
                 * all edges in drawableAreaNode n must be in drawableAreaNode "drawableAreaNode"
                 * also traverses the drawableAreaNodes to check
                 */
                returnBool = allEdgeAreContainedIn(n, drawableAreaNode, checkedDrawableAreaNodes, buildDrawablePattern, rule);
                if(returnBool){
                    Log.print("insertValidSubPatternFromRule: edges were correct, adding to pattern", Log.LEVEL.DEBUG);
                    buildDrawablePattern.drawableAreaNodes.add(drawableAreaNode);
                }
            }
            /*
             * once we have found a drawableAreaNode which has the correct edges we can check all the subnodes to that drawableAreaNode.
             * issue here is that we need to keep track of which drawableAreaNodes we have check in order to not have a circular dep.
             */

        }
        return returnBool;
    }

    /**
     * Checks so all that all edges that drawableAreaNode 'n' has, drawableAreaNode 'drawableAreaNode' will have too.
     * @param n
     * @param drawableAreaNode
     * @param checkedDrawableAreaNodes
     * @param p
     * @param rule
     * @return
     */
    private boolean allEdgeAreContainedIn(DrawableAreaNode n, DrawableAreaNode drawableAreaNode, ArrayList<DrawableAreaNode> checkedDrawableAreaNodes, DrawablePattern p, Rule rule){
        boolean returnBool = true;
        boolean nooneChecked = false;
        if(n.getDrawableEdges().size() > drawableAreaNode.getDrawableEdges().size()){
            Log.print("allEdgeAreContainedIn: given drawableAreaNode had less edges than other given drawableAreaNode", Log.LEVEL.DEBUG);
            return false;
        }
        for (DrawableEdge e : n.getDrawableEdges()) { // for each edge in drawableAreaNode from matching pattern
            for (DrawableEdge gE : drawableAreaNode.getDrawableEdges()) { // for each edge in given drawableAreaNode
                nooneChecked = true;
                if(e.getStartDrawableAreaNode() == n){ // if n is start drawableAreaNode then we check end drawableAreaNode
                    if(gE.getStartDrawableAreaNode() == drawableAreaNode){ // if drawableAreaNode is start drawableAreaNode in its edge
                        nooneChecked = false;
                        if(!gE.getEndDrawableAreaNode().getType().equals(e.getEndDrawableAreaNode().getType()) && !e.getEndDrawableAreaNode().getType().equals("ANY")){
                            /*
                             * if they are not the same we need to continue to look, but we set flag to false to keep track
                             */
                            Log.print("allEdgeAreContainedIn: found edge where end drawableAreaNodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }else{
                            /*
                             * if we find a matching drawableAreaNode-edge pair we can set flag to true and check this subnode for subpattern.
                             * finally we break loop
                             */
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = insertValidSubPatternFromRule(gE.getEndDrawableAreaNode(), checkedDrawableAreaNodes, p, rule);
                            break;
                        }
                    }
                }else if(e.getEndDrawableAreaNode() == n){
                    if(gE.getEndDrawableAreaNode() == drawableAreaNode){
                        nooneChecked = false;
                        if(!gE.getStartDrawableAreaNode().getType().equals(e.getStartDrawableAreaNode().getType()) && !e.getStartDrawableAreaNode().getType().equals("ANY")){
                            Log.print("allEdgeAreContainedIn: found edge where start drawableAreaNodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }else{
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = insertValidSubPatternFromRule(gE.getStartDrawableAreaNode(), checkedDrawableAreaNodes, p, rule);
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
    public void applyRule(DrawablePattern graph, DrawablePattern p, Rule rule) {
        graph.resetIds();
        insertAndReplace(graph, p, rule);
    }

}
