package graph_generator.test;

import graph_generator.controller.GraphController;
import graph_generator.utils.GraphLogger;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import model.Edge;
import model.Node;
import model.Pattern;
import model.Rule;
import org.junit.Before;
import org.junit.Test;
import utils.Log;

import java.util.ArrayList;

/**
 * Created by vilddjur on 3/1/17.
 */
public class GraphControllerTest {
    private GraphController graphController;
    @Before
    public void init(){
        graphController = new GraphController();
        //Log.level = Log.LEVEL.DEBUG;
    }
    @Test
    public void rulesMatchingPattern() throws Exception {
        /*
        create n rules
            create 2 patterns, one we can match vs, one we dont match vs.
            create n (random?) translations, maybe not needed?
        create pattern matching one of these rules
        expect output pair array have n/2 different rules, in reality it can have multiple matches on same subpattern
            "starting" on different nodes
         */

        Pattern matchingPattern = new Pattern();
        Node startNode = node("START");
        Node endNode = node("END");
        Edge e = new Edge(startNode, endNode);
        startNode.addEdge(e);
        endNode.addEdge(e);
        matchingPattern.nodes.add(startNode);
        matchingPattern.nodes.add(endNode);

        Pattern weirdPattern = new Pattern();
        Node lockNode = node("LOCK");
        Node weirdNode = node("WEIRD");
        Edge e2 = new Edge(lockNode, weirdNode);
        lockNode.addEdge(e2);
        weirdNode.addEdge(e2);
        weirdPattern.nodes.add(lockNode);
        weirdPattern.nodes.add(weirdNode);

        Pattern ourPattern = new Pattern();
        Node node1 = node("START");
        Node node2 = node("END");
        Node node3 = node("OTHER");
        Edge e3 = new Edge(node1, node2);
        Edge e4 = new Edge(node2, node3);
        node1.addEdge(e3);
        node2.addEdge(e3);
        node2.addEdge(e4);
        node3.addEdge(e4);
        ourPattern.nodes.add(node1);
        ourPattern.nodes.add(node2);
        ourPattern.nodes.add(node3);

        Rule matchingRule = new Rule(matchingPattern);
        Rule weirdRule = new Rule(weirdPattern);
        ArrayList<Rule> rules = new ArrayList<>();
        rules.add(matchingRule);
        rules.add(weirdRule);
        ArrayList<Pair<Rule, Pattern>> pairArrayList = graphController.rulesMatchingPattern(rules, ourPattern);

        assert pairArrayList.size() == 2;
    }

    @Test
    public void applyRule() throws Exception {
    /*
    create rule with simple matching pattern,
        a = b->c->a
    expect nodes size to be 2n+1 after applyingRule n times
     */
        int nodeId = 1337;
        Pattern simplePattern =  new Pattern();
        Node nodeA = node("A");
        nodeA.setNodeId(nodeId);
        simplePattern.nodes.add(nodeA);

        Pattern trans =  new Pattern();
        Node nodeA2 = node("A");
        nodeA2.setNodeId(nodeId);
        Node nodeB = node("B");
        Node nodeC = node("C");
        Edge bToC = new Edge(nodeB, nodeC);
        nodeB.addEdge(bToC);
        nodeC.addEdge(bToC);
        Edge cToA = new Edge(nodeC, nodeA2);
        nodeA2.addEdge(cToA);
        nodeC.addEdge(cToA);
        trans.nodes.add(nodeA2);
        trans.nodes.add(nodeB);
        trans.nodes.add(nodeC);

        Rule rule = new Rule(simplePattern);
        rule.possibleTranslations.add(trans);

        Pattern graph = new Pattern();
        Node nodeA3 = node("A");
        graph.nodes.add(nodeA3);

        ArrayList<Rule> rules = new ArrayList<Rule>();
        rules.add(rule);
        int n = 20;
        for (int i = 0; i < n; i++) {
            ArrayList<Pair<Rule, Pattern>> rulePatternList = graphController.rulesMatchingPattern(rules, graph);
            if (rulePatternList.size() > 0) {
                Pair<Rule, Pattern> pair = rulePatternList.get(0);
                Rule r = pair.getKey();
                Pattern p = pair.getValue();
                graph.resetIds();
                Log.print("applying rule", Log.LEVEL.DEBUG);
                Log.print("Before: \n " + GraphLogger.patternToString(graph), Log.LEVEL.DEBUG);
                Log.print("Subpattern: " + GraphLogger.patternToString(p), Log.LEVEL.DEBUG);
                graphController.applyRule(graph, p, r);
                Log.print("After: \n " + GraphLogger.patternToString(graph), Log.LEVEL.DEBUG);
            }
        }

        Log.print(GraphLogger.patternToString(graph), Log.LEVEL.DEBUG);

        assert graph.nodes.size() == 2*n+1;
    }

    private Node node(String type) {
        return new Node(0,0,40, Color.AQUA, type);
    }
}