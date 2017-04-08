package graph_generator.controller;

import javafx.util.Pair;
import model.*;
import utils.Log;

import java.util.*;

/**
 * Created by vilddjur on 2/28/17.
 */
public class GraphController {

    private Random random;
    private static GraphController instance = null;

    private GraphController(){
        random = new Random();
    }

    public static GraphController getInstance() {
        if (instance == null) {
            instance = new GraphController();
        }
        return instance;
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
            Log.print("rulesMatchingPattern: " + graph.drawableAreaNodes.get(i), Log.LEVEL.DEBUG);
            Log.print("vs", Log.LEVEL.DEBUG);
            Log.print("GraphController: " + r.matchingDrawablePattern.drawableAreaNodes.toString(), Log.LEVEL.DEBUG);

            DrawablePattern p = new DrawablePattern();
            boolean result = insertValidSubPatternFromRule(graph.drawableAreaNodes.get(i), new ArrayList<DrawableAreaNode>(), p, r);

            if (result) {
                //If the current node can get too many edges its not a possible rule.
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
            if (n != null) {
                Log.print("insertAndReplace: found corresponding drawableAreaNode; "+ n, Log.LEVEL.INFO);
                n.removeEdgesToNodesWithType("ANY");

                for (DrawableEdge e : n.getDrawableEdges()) {
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
        for (DrawableAreaNode n : rule.matchingDrawablePattern.drawableAreaNodes) {
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
     * Checks if target 'target' is a valid target in the given rules matchingpattern if that is the case its added to the
     * pattern 'currentMatch' will return false if any valid 'target' found in matchingpattern has more than 8 edges.
     * After checking a target, if its valid and has more drawableAreaNodes to check it will call itself with the next target and
     * add the checked target target to checkedNode list.
     *
     * @param target
     * DrawableAreaNode to check if valid in matchingDrawablePattern
     * @param checkedNodes
     * List of checked drawableAreaNodes so we dont check same target twice
     * @param currentMatch
     * Valid subpattern built from 'target'
     * @param rule
     * Rule with the pattern we are matching against
     * @return
     * True if we have found a valid subpattern, otherwise false.
     */
    private boolean insertValidSubPatternFromRule(DrawableAreaNode target, ArrayList<DrawableAreaNode> checkedNodes, DrawablePattern currentMatch, Rule rule) {
        boolean inserted = false;

        if (checkedNodes.contains(target)) {
            return true;
        }

        // check so that target has less than 8 edges
        if (target.getDrawableEdges().size() > 8) {
            Log.print("insertValidSubPatternFromRule: target has more than 8 edges; returning false", Log.LEVEL.DEBUG);
            return false;
        }

        for (DrawableAreaNode match : rule.matchingDrawablePattern.drawableAreaNodes) {
            // find target in matching pattern with same type.
            if (target.getType().equals(match.getType())) {
                checkedNodes.add(target);
                Log.print("insertValidSubPatternFromRule: found matching type", Log.LEVEL.DEBUG);

                /*
                 * all edges in target match must be in target "target"
                 * also traverses the drawableAreaNodes to check
                 */
                inserted = allSubnodesAreContainedIn(match, target, checkedNodes, currentMatch);
                inserted = inserted && allEdgeAreContainedIn(match, target, checkedNodes, currentMatch, rule);

                // This specific node is correct
                if (inserted) {
                    // For each translation check that there wont be too many edges (>8)
                    for (DrawablePattern translation : rule.possibleTranslations) {
                        for (DrawableAreaNode node : translation.drawableAreaNodes) {
                            if (node.getNodeId() == match.getNodeId()) {
                                // NodeEdges - MatchEdges + TranslationEdges = final number of edges
                                if (target.getDrawableEdges().size()
                                        - match.getDrawableEdges().size()
                                        + node.getDrawableEdges().size() > 8) {
                                    return false;
                                }
                            }
                        }
                    }
                    Log.print("insertValidSubPatternFromRule: edges were correct, adding to pattern", Log.LEVEL.DEBUG);
                    currentMatch.drawableAreaNodes.add(target);
                }
            }
        }

        return inserted;
    }

    private boolean allSubnodesAreContainedIn(DrawableAreaNode match, DrawableAreaNode target, ArrayList<DrawableAreaNode> checkedNodes, DrawablePattern currentMatch) {
        if (target.getDrawableSubnodes().containsAll(match.getDrawableSubnodes())) {
            return true;
        }

        return false;
    }

    /**
     * Checks so all edges of the target node contains all edges of the matching node.
     *
     * @param match
     * @param target
     * @param checkedNodes
     * @param currentMatch
     * @param rule
     * @return
     */
    private boolean allEdgeAreContainedIn(DrawableAreaNode match, DrawableAreaNode target, ArrayList<DrawableAreaNode> checkedNodes, DrawablePattern currentMatch, Rule rule){
        boolean returnBool = true;
        boolean nooneChecked = false;

        if (match.getDrawableEdges().size() > target.getDrawableEdges().size()) {
            Log.print("allEdgeAreContainedIn: given target had less edges than other given target", Log.LEVEL.DEBUG);
            return false;
        }

        // For each matchingEdge in target from matching pattern.
        for (DrawableEdge matchingEdge : match.getDrawableEdges()) {
            // For each matchingEdge in given target.
            for (DrawableEdge targetEdge : target.getDrawableEdges()) {
                nooneChecked = true;

                // If match is start target then we check end target.
                if (matchingEdge.getStartDrawableAreaNode() == match) {
                    // If target is start target in its matchingEdge.
                    if (targetEdge.getStartDrawableAreaNode() == target) {
                        nooneChecked = false;

                        if (targetEdge.getEndDrawableAreaNode().getType().equals(matchingEdge.getEndDrawableAreaNode().getType())
                                || matchingEdge.getEndDrawableAreaNode().getType().equals("ANY")) {

                            // Matching target-matchingEdge pair we check this subnode for subpattern.

                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = insertValidSubPatternFromRule(targetEdge.getEndDrawableAreaNode(), checkedNodes, currentMatch, rule);
                            break;
                        } else {
                            // If they are not the same we need to continue to look, but we set flag to false to keep track.
                            Log.print("allEdgeAreContainedIn: found matchingEdge where end drawableAreaNodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }
                    }
                } else if (matchingEdge.getEndDrawableAreaNode() == match) {
                    if (targetEdge.getEndDrawableAreaNode() == target) {
                        nooneChecked = false;

                        if (targetEdge.getStartDrawableAreaNode().getType().equals(matchingEdge.getStartDrawableAreaNode().getType()) || matchingEdge.getStartDrawableAreaNode().getType().equals("ANY")) {
                            Log.print("allEdgeAreContainedIn: found true case, checking subNode", Log.LEVEL.INFO);
                            returnBool = insertValidSubPatternFromRule(targetEdge.getStartDrawableAreaNode(), checkedNodes, currentMatch, rule);
                            break;
                        } else {
                            Log.print("allEdgeAreContainedIn: found matchingEdge where start drawableAreaNodes were not the same", Log.LEVEL.INFO);
                            returnBool = false;
                        }
                    }
                }
            }
            if (nooneChecked) {
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
