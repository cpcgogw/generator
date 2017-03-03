package graph_generator.test;

import graph_generator.controller.GraphController;
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
    private ArrayList<Rule> rules;
    @Before
    public void init(){
        graphController = new GraphController();
        Log.level = Log.LEVEL.DEBUG;

        rules = new ArrayList<>();

        Pattern matchingPattern = new Pattern();
        Node startNode = node("START");
        Node endNode = node("END");
        Edge e = new Edge(startNode, endNode);
        matchingPattern.nodes.add(startNode);
        matchingPattern.nodes.add(endNode);


        Rule matchingRule = new Rule(matchingPattern);

        rules.add(matchingRule);
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

        Pattern weirdPattern = new Pattern();
        Node lockNode = node("LOCK");
        Node weirdNode = node("WEIRD");
        Edge e2 = new Edge(lockNode, weirdNode);
        weirdPattern.nodes.add(lockNode);
        weirdPattern.nodes.add(weirdNode);

        Pattern ourPattern = new Pattern();
        Node node1 = node("START");
        Node node2 = node("END");
        Node node3 = node("OTHER");
        Edge e3 = new Edge(node1, node2);
        Edge e4 = new Edge(node2, node3);
        ourPattern.nodes.add(node1);
        ourPattern.nodes.add(node2);
        ourPattern.nodes.add(node3);

        Rule weirdRule = new Rule(weirdPattern);

        rules.add(weirdRule);
        ArrayList<Pair<Rule, Pattern>> pairArrayList = graphController.rulesMatchingPattern(rules, ourPattern);

        assert pairArrayList.size() == 2;
    }

    @Test
    public void rulesMatchingPatternOverfull() throws Exception {
        /*
        create pattern containing node with more than 8 edges to/from it
        make sure we get no rules matching
         */
        Pattern overFullPattern = new Pattern();
        Node startOverFull = node("START");
        Node endOverFull = node("END");
        Edge edgeOverfull = new Edge(startOverFull, endOverFull);
        overFullPattern.nodes.add(startOverFull);
        overFullPattern.nodes.add(endOverFull);
        for (int i = 0; i < 8; i++) {
            Node n = node("LOL" + i);
            Edge eTmp = new Edge(n, startOverFull);
            overFullPattern.nodes.add(n);
        }
        Log.print("overfullPattern: " + overFullPattern, Log.LEVEL.DEBUG);

        ArrayList<Pair<Rule, Pattern>> pairArrayList = graphController.rulesMatchingPattern(rules, overFullPattern);
        System.out.println(pairArrayList.size());
        assert pairArrayList.size() == 0;
    }
    @Test
    public void rulesMatchingPatternAnyNode() throws Exception {
        /*
        create rule using 'ANY' node type.
        make sure we match correctly
         */
        
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
        Edge cToA = new Edge(nodeC, nodeA2);
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
                Log.print("applying rule", Log.LEVEL.DEBUG);
                Log.print("Before: \n " + graph, Log.LEVEL.DEBUG);
                Log.print("Subpattern: " + p, Log.LEVEL.DEBUG);
                graphController.applyRule(graph, p, r);
                Log.print("After: \n " + graph, Log.LEVEL.DEBUG);
            }
        }

        Log.print(graph.toString(), Log.LEVEL.DEBUG);

        assert graph.nodes.size() == 2*n+1;

    }

    private Node node(String type) {
        return new Node(0,0,40, Color.AQUA, type);
    }
}