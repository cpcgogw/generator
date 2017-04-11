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
    public ArrayList<Pair<Rule, Pair<DrawablePattern, DrawablePattern>>> rulesMatchingPattern(List<Rule> rules, DrawablePattern graph) {
        ArrayList<Pair<Rule, Pair<DrawablePattern, DrawablePattern>>> rulePatternList = new ArrayList<>();

        for (Rule r : rules) {
            rulePatternList.addAll(ruleMatchingPattern(r, graph));
        }

        return rulePatternList;
    }

    public ArrayList<Pair<Rule, Pair<DrawablePattern, DrawablePattern>>> ruleMatchingPattern(Rule r, DrawablePattern graph) {
        ArrayList<Pair<Rule, Pair<DrawablePattern, DrawablePattern>>> rulePatternList = new ArrayList<>();

        for (DrawableAreaNode node : graph.drawableAreaNodes) {
            Log.print("rulesMatchingPattern: checking subpattern:", Log.LEVEL.DEBUG);
            Log.print("rulesMatchingPattern: " + node, Log.LEVEL.DEBUG);
            Log.print("vs", Log.LEVEL.DEBUG);
            Log.print("GraphController: " + r.matchingDrawablePattern.drawableAreaNodes, Log.LEVEL.DEBUG);

            DrawablePattern p = new DrawablePattern();
            DrawablePattern p2 = new DrawablePattern();
            Pair<DrawablePattern, DrawablePattern> pair = new Pair<>(p, p2);
            boolean result = insertValidSubPatternFromRule(node, new ArrayList<>(), pair, r);

            if (result) {
                Log.print("A pattern was found which returns a match!", Log.LEVEL.INFO);
                Log.print(p, Log.LEVEL.DEBUG);
                Log.print("##############################################", Log.LEVEL.DEBUG);
                rulePatternList.add(new Pair<>(r,pair));
            }

            Log.print("rulesMatchingPattern: "+result, Log.LEVEL.DEBUG);
            Log.print("rulesMatchingPattern: found: ", Log.LEVEL.DEBUG);
            Log.print(p.drawableAreaNodes, Log.LEVEL.DEBUG);
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
        ArrayList<Pair<Rule, Pair<DrawablePattern, DrawablePattern>>> rulePatternList = rulesMatchingPattern(rules, graph);
        if (rulePatternList.size() > 0) {
            Pair<Rule, Pair<DrawablePattern, DrawablePattern>> pair = rulePatternList.get(random.nextInt(rulePatternList.size()));
            Rule r = pair.getKey();
            Pair<DrawablePattern, DrawablePattern> p = pair.getValue();
            applyRule(graph, p, r);
        }
    }


    /**
     * Inserts a random translation into the graph through the match.
     * match    = graph nodes which match the matching pattern for the rule.
     * matching = matching pattern of rule.
     *
     *  @param graph
     * The graph to insert the random translation into.
     * @param match
     * Pair of (match, matching).
     * @param rule
     * Rule to apply some translation from.
     */
    public void insertAndReplace(DrawablePattern graph, Pair<DrawablePattern, DrawablePattern> match, Rule rule) {
        Log.print("Before...", Log.LEVEL.DEBUG);
        Log.print("Graph: "+graph, Log.LEVEL.DEBUG);
        Log.print("Matched: "+match.getKey(), Log.LEVEL.DEBUG);
        Log.print("Matching: "+match.getValue(), Log.LEVEL.DEBUG);
        ArrayList<DrawableAreaNode> updatedNodes = new ArrayList<>();
        ArrayList<DrawableSubnode> updatedSubnodes = new ArrayList<>();
        // Replace ID's of translation and matching nodes to be same as the ID of the matched node.
        // Adds updated nodes to placedNodes and updated subnodes to placedSubnodes. This way
        // we know which nodes we need to simply update to the translated version and which nodes
        // will be newly added to the graph.
        DrawablePattern translation = updateNodeIDs(
                graph,
                match.getKey().drawableAreaNodes,
                match.getValue().drawableAreaNodes,
                rule,
                updatedNodes,
                updatedSubnodes
        );

        // Remove matched edges, as we will be adding all translation edges
        removeMatchingEdges(match.getKey().drawableAreaNodes, match.getValue().drawableAreaNodes);

        Log.print("Middle...", Log.LEVEL.DEBUG);
        Log.print("Graph after ID update and edge removal: "+graph, Log.LEVEL.DEBUG);
        Log.print("Match after ID update and edge removal: "+match.getKey(), Log.LEVEL.DEBUG);
        Log.print("Translation after ID update and edge removal: "+translation, Log.LEVEL.DEBUG);

        Log.print("Updated nodes: ", Log.LEVEL.DEBUG);
        for (DrawableAreaNode node : updatedNodes) {
            Log.print(node, Log.LEVEL.DEBUG);
        }

        Log.print("Updated subnodes: ", Log.LEVEL.DEBUG);
        for (DrawableSubnode subnode : updatedSubnodes) {
            Log.print(subnode, Log.LEVEL.DEBUG);
        }

        // When we somehow get the same placedNode twice, the graph will fuck up.

        // Go through all nodes in translation and either update its Type, position, subnodes, and edges
        // or set its ID to some unique ID and add to the graph.
        mergeTranslationAndGraph(translation.drawableAreaNodes, match.getKey().drawableAreaNodes, graph, updatedNodes, updatedSubnodes);

        Log.print("After...", Log.LEVEL.DEBUG);
        Log.print("Graph: "+graph, Log.LEVEL.DEBUG);
        Log.print("Matched: "+match.getKey(), Log.LEVEL.DEBUG);
    }

    /**
     * Merges a translation of a rule with the corresponding graph.
     *
     * @param translation
     * The nodes in the translation part of some rule.
     * @param match
     * The nodes which match the matching pattern of some rule, should be same rule as for the translation.
     * @param graph
     * The graph with to merge to.
     * @param updatedNodes
     * Nodes which has had their ID's updated.
     * @param updatedSubnodes
     * Subnodes which has had their ID's updated.
     */
    private void mergeTranslationAndGraph(List<DrawableAreaNode> translation,
                                          List<DrawableAreaNode> match,
                                          DrawablePattern graph,
                                          List<DrawableAreaNode> updatedNodes,
                                          List<DrawableSubnode> updatedSubnodes) {
        // We will need to use unique ID's for non-updated nodes and subnodes.
        int maxCount = getMaxID(graph);
        int maxSubCount = getMaxSubID(graph);

        // For each node in the translation either update it or set a unique ID and add it to the graph.
        for (DrawableAreaNode translationNode : translation) {
            // Nodes with updated ID should have same ID.
            if (updatedNodes.contains(translationNode)) {
                for (DrawableAreaNode matchedNode : match) {
                    // Since the translation node has been added to the list of updated nodes
                    // there must be a matched node with the same ID.
                    if (matchedNode.getNodeId() == translationNode.getNodeId()) {
                        //Update matched node
                        matchedNode.setPos(translationNode.getCenterX(), translationNode.getCenterY());
                        matchedNode.setType(translationNode.getType());

                        // Make all edges refer to the matched node instead.
                        for (DrawableEdge edge : translationNode.getDrawableEdges()) {
                            edge.replaceNode(translationNode, matchedNode);
                        }

                        // Add all edges from the translation node to the matched node.
                        // This only works since we removed the matching edges before.
                        // Else we would get duplicate edges.
                        matchedNode.addAllEdges(translationNode.getDrawableEdges());

                        // Either the subnode has been update (should keep same ID) or its a
                        // new subnode and should be assigned a unique ID.
                        for (DrawableSubnode subnode : translationNode.getDrawableSubnodes()) {
                            if (updatedSubnodes.contains(subnode)) {
                                matchedNode.addObject(subnode);
                            } else {
                                maxSubCount++;
                                subnode.setNodeId(maxSubCount);
                                matchedNode.addObject(subnode);
                            }
                        }
                    }
                }
            } else {
                // The current translation node has not been updated and is thus a entirely new node.
                // Assign a unique ID.
                maxCount++;
                translationNode.setNodeId(maxCount);

                // Either the subnode has been updated and nothing needs to be done,
                // or it is a new subnode and needs a unique ID.
                for (DrawableSubnode subnode : translationNode.getDrawableSubnodes()) {
                    if (!updatedSubnodes.contains(subnode)) {
                        maxSubCount++;
                        subnode.setNodeId(maxSubCount);
                    }
                }

                graph.addNode(translationNode);
            }
        }
    }

    /**
     * Removes all matching edges from all nodes in matched and matching.
     *
     * @param matched
     * List of nodes which to match edges with the list of matching nodes.
     * @param matching
     * List of nodes which to match edges with the list of matched nodes.
     */
    private void removeMatchingEdges(List<DrawableAreaNode> matched, List<DrawableAreaNode> matching) {
        // Find matching edges in matching and matched
        for (DrawableAreaNode matchingNode : matching) {
            for (DrawableAreaNode matchedNode : matched) {
                removeMatchingEdge(matchedNode, matchingNode);
            }
        }
    }

    /**
     * Removes all edges matching in the matched and matching node.
     *
     * @param matched
     * Node which to match and remove edges matching edges in matching.
     * @param matching
     * Node which to match and remove edges matching edges in matched.
     */
    private void removeMatchingEdge(DrawableAreaNode matched, DrawableAreaNode matching) {
        List<DrawableEdge> removableEdges = new ArrayList<>();
        for (DrawableEdge matchingEdge : matching.getDrawableEdges()) {
            for (DrawableEdge matchedEdge : matched.getDrawableEdges()) {
                if (matchedEdge.getTo().getNodeId() == matchingEdge.getTo().getNodeId()
                        && matchedEdge.getFrom().getNodeId() == matchedEdge.getFrom().getNodeId()) {
                    // Since we cant remove edges while iterating through them we
                    // instead add them to a list and remove them after.
                    removableEdges.add(matchedEdge);
                }
            }
            for (DrawableEdge edge : removableEdges) {
                matched.removeEdge(edge);
            }
        }
    }

    private int getMaxSubID(DrawablePattern graph) {
        int max = -1;
        for (DrawableAreaNode node : graph.drawableAreaNodes) {
            for (DrawableSubnode subnode : node.getDrawableSubnodes()) {
                if (subnode.getNodeId() > max)
                    max = subnode.getNodeId();
            }
        }
        return max;
    }

    private int getMaxID(DrawablePattern graph) {
        int maxCount = -1;
        for (DrawableAreaNode node : graph.drawableAreaNodes) {
            if (node.getNodeId() > maxCount)
                maxCount = node.getNodeId();
        }
        return maxCount;
    }

    /**
     * Updates the ID's of the matching and translation nodes to be the same as the matched node ID's.
     * matched = The matched nodes in the graph.
     * matching = The matching pattern of the rule.
     *
     * @param graph
     * Graph to update node ID's on.
     * @param matched
     * The nodes which ID's to use for updating.
     * @param matching
     * The nodes which ID's to update.
     * Pair of (matched, matching) nodes.
     * @param rule
     * Rule which will be used for replacing matching and translation node ID's with the matched node ID's.
     * @param updatedNodes
     * Nodes which have had its ID updated, needed for other function.
     * @param updatedSubnodes
     * Subnodes which have had its ID updated, needed for other function.
     * @return
     * The translation which were randomly selected and had its ID's updated.
     */
    //TODO: Refactor so the used translation is sent in as a variable instead of the rule.
    private DrawablePattern updateNodeIDs(DrawablePattern graph,
                                          List<DrawableAreaNode> matched,
                                          List<DrawableAreaNode> matching,
                                          Rule rule,
                                          List<DrawableAreaNode> updatedNodes,
                                          List<DrawableSubnode> updatedSubnodes) {

        // Don't make changes to the entered rule, make a copy and update that instead.
        // Note: pretty sure the clone method is faulty and thus will result in us updating the
        // rule anyway.
        DrawablePattern translation = copyRule(rule);
        List<DrawableAreaNode> removeFromGraph = new ArrayList<>();
        List<DrawableAreaNode> removeFromMatched = new ArrayList<>();

        // Go through all the nodes that were matched
        for (int i = 0; i < matched.size(); i++) {
            DrawableAreaNode translationNode = null;
            DrawableAreaNode matchedNode = matched.get(i);
            DrawableAreaNode matchingNode = matching.get(i);

            // Try to find the corresponding translation node.
            for (DrawableAreaNode node : translation.drawableAreaNodes) {
                if (matchingNode.getNodeId() == node.getNodeId()) {
                    translationNode = node;
                }
            }

            // There is a corresponding matched, matching, and translation node.
            if (translationNode != null) {
                Log.print("Matched, matching, translation ("
                        +matchedNode.getNodeId()
                        +", "
                        +matchingNode.getNodeId()
                        +", "
                        +translationNode.getNodeId()
                        +")",
                        Log.LEVEL.DEBUG
                );

                // If the matched ID is used elsewhere in translation and matching, change those ID's
                // This is done so translation node isn't matched again with a different ID.
                updateConflictingID(matching, translation.drawableAreaNodes, graph, matchedNode, matchingNode);

                // Use the same ID as the matched node.
                translationNode.setNodeId(matchedNode.getNodeId());
                matchingNode.setNodeId(matchedNode.getNodeId());

                // The translationNode and matchingNode has had their ID's updated.
                updatedNodes.add(translationNode);

                // Update subnode ID's in translationNode and matchingNode to be same as in matched.
                updateSubnodeIDs(matchedNode, matchingNode, translation.drawableAreaNodes, updatedSubnodes);
            } else {
                // There is a matching and matched node but no translation node
                // ie, the node is supposed to be deleted.
                Log.print("Node "+matchedNode.getNodeId()+" has a matching but not translation node.", Log.LEVEL.DEBUG);

                // Mark for deletion, cant delete right away since all nodes are needed for updateSubnodeIDs
                // and cant modify matched while iterating through it.
                // While both graph and matched refer to the same nodes
                // they are different lists and therefore both need to remove the same node.
                for (DrawableAreaNode node : graph.drawableAreaNodes) {
                    if (node == matchedNode) {
                        Log.print("Node "+node.getNodeId()+" is marked for deletion.", Log.LEVEL.DEBUG);
                        removeFromGraph.add(node);
                    }
                }

                removeFromMatched.add(matchedNode);
            }
        }

        // Remove nodes marked for deletion from the graph
        for (DrawableAreaNode node : removeFromGraph) {
            node.removeAllEdges();
            graph.drawableAreaNodes.remove(node);
        }

        // Remove nodes marked for deletion from the matched.
        for (DrawableAreaNode node : removeFromMatched) {
            node.removeAllEdges();
            matched.remove(node);
        }

        Log.print("Graph after: \n"+graph, Log.LEVEL.DEBUG);
        Log.print("Matched after: "+matched, Log.LEVEL.DEBUG);

        return translation;
    }

    private void updateConflictingID(List<DrawableAreaNode> matching,
                                     List<DrawableAreaNode> translation,
                                     DrawablePattern graph,
                                     DrawableAreaNode matchedNode,
                                     DrawableAreaNode conflicting) {
        for (DrawableAreaNode matchingNode : matching) {
            // If there is a matching node with the matched id which isn't the current matching node
            if (matchingNode.getNodeId() == matchedNode.getNodeId() &&
                    matchingNode != conflicting) {
                // Used to get a unique ID.
                int max = getMaxID(graph);
                max++;

                // Change the ID of the corresponding translation node if there is one.
                for (DrawableAreaNode translationNode : translation) {
                    if (translationNode.getNodeId() == matchingNode.getNodeId()) {
                        translationNode.setNodeId(max);
                    }
                }
                matchingNode.setNodeId(max);
            }
        }
    }

    /**
     * Updates the ID's of translation and matching to be the same as the matched subnode.
     * NOTE: A side effect of this method is that it will remove all matching subnodes from
     * the matched node.
     * matched = The nodes in the graph which has matched the matching pattern of the rule.
     * matching = The matching part of the rule.
     *
     * @param matched
     * The matched node.
     * @param matching
     * The matching node.
     * @param translation
     * The translation nodes for the used rule.
     * @param updatedSubnodes
     * A list of all subnodes which have been updated.
     */
    //TODO: Refactor so updatedSubnodes is not used.
    private void updateSubnodeIDs(DrawableAreaNode matched,
                                  DrawableAreaNode matching,
                                  List<DrawableAreaNode> translation,
                                  List<DrawableSubnode> updatedSubnodes) {

        List<DrawableSubnode> removeMatching = new ArrayList<>();
        List<DrawableSubnode> removeMatched = new ArrayList<>();
        // Find a corresponding matched and matching subnode
        for (DrawableSubnode matchingSubnode : matching.getDrawableSubnodes()) {
            for (DrawableSubnode matchedSubnode : matched.getDrawableSubnodes()) {
                // If they have the same type they are, in the case of a match, equal.
                if (matchedSubnode.getType().equals(matchingSubnode.getType())) {
                    // Go through all subnodes in the translation and find the
                    // translation subnode with the same ID as the matching subnode.
                    for (DrawableAreaNode node : translation) {
                        for (DrawableSubnode subnode : node.getDrawableSubnodes()) {
                            // Matching translation subnode ID and matching subnode ID, update
                            if (subnode.getNodeId() == matchingSubnode.getNodeId()) {
                                subnode.setNodeId(matchedSubnode.getNodeId());
                                // Add to the list of updated subnodes.
                                updatedSubnodes.add(subnode);
                            }
                        }
                    }

                    // Purge the matched subnodes.
                    removeMatching.add(matchingSubnode);
                    removeMatched.add(matchedSubnode);
                }
            }
        }

        for (DrawableSubnode subnode : removeMatched)
            matched.removeSubnode(subnode);
        for (DrawableSubnode subnode : removeMatching)
            matching.removeSubnode(subnode);
    }

    private DrawablePattern copyRule(Rule rule) {
        DrawablePattern translation = rule.randomPossiblePattern();
        DrawablePattern p = new DrawablePattern();
        for (DrawableAreaNode node : translation.drawableAreaNodes) {
            p.addNode(node.clone());
        }
        return p;
    }

    /**
     * adds all drawableAreaNodes in translation that are not in match,
     * @param match
     * pattern to add into
     * @param translation
     * pattern to take drawableAreaNodes from
     */
    public void addAllNotIn(DrawablePattern match, DrawablePattern translation) {
        for (DrawableAreaNode translationNode : translation.drawableAreaNodes) {
            boolean contains = false;
            for (DrawableAreaNode matchingNode : match.drawableAreaNodes) {
                if (matchingNode.getNodeId() == translationNode.getNodeId()) {
                    contains = true;
                }
            }
            if (!contains && !translationNode.getType().equals("ANY")) {
                Log.print("addAllNotIn: adding translationNode; " + translationNode, Log.LEVEL.DEBUG);
                match.drawableAreaNodes.add(translationNode.clone());
            }
        }
    }


    //TODO: Currently retrieves the wrong node
    private DrawableAreaNode findCorrespondingNode(DrawableAreaNode node, DrawablePattern p, Rule rule) {
        DrawableAreaNode ret = null;
        for (DrawableAreaNode matchingNode : rule.matchingDrawablePattern.drawableAreaNodes) {
            if (node.getType().equals(matchingNode.getType())){
                for (DrawableAreaNode target : p.drawableAreaNodes) {
                    if (target.getNodeId() == matchingNode.getNodeId())
                        return target;
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
     * Valid subpattern built from 'target' paired with the corresponding match pattern.
     * @param rule
     * Rule with the pattern we are matching against
     * @return
     * True if we have found a valid subpattern, otherwise false.
     */
    private boolean insertValidSubPatternFromRule(DrawableAreaNode target, ArrayList<DrawableAreaNode> checkedNodes, Pair<DrawablePattern, DrawablePattern> currentMatch, Rule rule) {
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
                Log.print("Checking if node "+target.getNodeId()+" has valid subnodes...", Log.LEVEL.INFO);
                Log.print("Target: "+target, Log.LEVEL.DEBUG);
                Log.print("Match:  "+match, Log.LEVEL.DEBUG);
                inserted = allSubnodesAreContainedIn(match, target);
                Log.print("Valid subnodes: "+inserted, Log.LEVEL.DEBUG);
                Log.print("Checking if valid edges...", Log.LEVEL.INFO);
                if (inserted) {
                    inserted = allEdgeAreContainedIn(match, target, checkedNodes, currentMatch, rule);
                }
                Log.print("Valid subnodes & edges: "+inserted, Log.LEVEL.INFO);

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
                    Log.print("insertValidSubPatternFromRule: edges and subnodes were correct, adding to pattern", Log.LEVEL.DEBUG);
                    currentMatch.getKey().drawableAreaNodes.add(target);
                    currentMatch.getValue().drawableAreaNodes.add(match);
                    return true;
                }
            }
        }

        return inserted;
    }

    private boolean allSubnodesAreContainedIn(DrawableAreaNode match, DrawableAreaNode target) {
        Log.print("Subnodes in target: "+target.getDrawableSubnodes(), Log.LEVEL.DEBUG);
        Log.print("Looking for subnodes: "+match.getDrawableSubnodes(), Log.LEVEL.DEBUG);
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
    private boolean allEdgeAreContainedIn(DrawableAreaNode match, DrawableAreaNode target, ArrayList<DrawableAreaNode> checkedNodes, Pair<DrawablePattern, DrawablePattern> currentMatch, Rule rule){
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
    public void applyRule(DrawablePattern graph, Pair<DrawablePattern, DrawablePattern> p, Rule rule) {
        //graph.resetIds();
        insertAndReplace(graph, p, rule);
    }
}
