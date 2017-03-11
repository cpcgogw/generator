package graph_generator.test;

import graph_generator.controller.GraphController;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import model.DrawableEdge;
import model.DrawableNode;
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
        Log.level = Log.LEVEL.INFO;

        rules = new ArrayList<>();

        Pattern matchingPattern = new Pattern();
        DrawableNode startDrawableNode = node("START");
        DrawableNode endDrawableNode = node("END");
        DrawableEdge e = new DrawableEdge(startDrawableNode, endDrawableNode);
        matchingPattern.drawableNodes.add(startDrawableNode);
        matchingPattern.drawableNodes.add(endDrawableNode);


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
            "starting" on different drawableNodes
         */

        Pattern weirdPattern = new Pattern();
        DrawableNode lockDrawableNode = node("LOCK");
        DrawableNode weirdDrawableNode = node("WEIRD");
        DrawableEdge e2 = new DrawableEdge(lockDrawableNode, weirdDrawableNode);
        weirdPattern.drawableNodes.add(lockDrawableNode);
        weirdPattern.drawableNodes.add(weirdDrawableNode);

        Pattern ourPattern = new Pattern();
        DrawableNode drawableNode1 = node("START");
        DrawableNode drawableNode2 = node("END");
        DrawableNode drawableNode3 = node("OTHER");
        DrawableEdge e3 = new DrawableEdge(drawableNode1, drawableNode2);
        DrawableEdge e4 = new DrawableEdge(drawableNode2, drawableNode3);
        ourPattern.drawableNodes.add(drawableNode1);
        ourPattern.drawableNodes.add(drawableNode2);
        ourPattern.drawableNodes.add(drawableNode3);

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
        DrawableNode startOverFull = node("START");
        DrawableNode endOverFull = node("END");
        DrawableEdge drawableEdgeOverfull = new DrawableEdge(startOverFull, endOverFull);
        overFullPattern.drawableNodes.add(startOverFull);
        overFullPattern.drawableNodes.add(endOverFull);
        for (int i = 0; i < 8; i++) {
            DrawableNode n = node("LOL" + i);
            DrawableEdge eTmp = new DrawableEdge(n, startOverFull);
            overFullPattern.drawableNodes.add(n);
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
            start -> any : should work
            start <- any : should not work
         */
        rules = new ArrayList<>();
        Pattern anyMatchingPattern = new Pattern();
        DrawableNode start = node("START");
        DrawableNode any = node("ANY");
        DrawableEdge matchDrawableEdge = new DrawableEdge(start, any);
        anyMatchingPattern.drawableNodes.add(start);
        anyMatchingPattern.drawableNodes.add(any);
        rules.add(new Rule(anyMatchingPattern));

        Pattern validPatten = new Pattern();
        DrawableNode start1 = node("START");
        DrawableNode any1 = node("ASDF");
        DrawableEdge validDrawableEdge = new DrawableEdge(start1, any1);
        validPatten.drawableNodes.add(start1);
        validPatten.drawableNodes.add(any1);

        Pattern invalidPattern = new Pattern();
        DrawableNode start2 = node("START");
        DrawableNode any2 = node("ASDF");
        DrawableEdge invalidDrawableEdge = new DrawableEdge(any2, start2);
        invalidPattern.drawableNodes.add(start2);
        invalidPattern.drawableNodes.add(any2);

        ArrayList<Pair<Rule, Pattern>> pairArrayList = graphController.rulesMatchingPattern(rules, validPatten);
        System.out.println(pairArrayList.size());

        assert pairArrayList.size() == 2;

        pairArrayList = graphController.rulesMatchingPattern(rules, invalidPattern);
        System.out.println(pairArrayList.size());

        assert pairArrayList.size() == 0;
    }

    @Test
    public void applyRule() throws Exception {
    /*
    create rule with simple matching pattern,
        a = b->c->a
    expect drawableNodes size to be 2n+1 after applyingRule n times
     */
        int nodeId = 1337;
        Pattern simplePattern =  new Pattern();
        DrawableNode drawableNodeA = node("A");
        drawableNodeA.setNodeId(nodeId);
        simplePattern.drawableNodes.add(drawableNodeA);

        Pattern trans =  new Pattern();
        DrawableNode drawableNodeA2 = node("A");
        drawableNodeA2.setNodeId(nodeId);
        DrawableNode drawableNodeB = node("B");
        DrawableNode drawableNodeC = node("C");
        DrawableEdge bToC = new DrawableEdge(drawableNodeB, drawableNodeC);
        DrawableEdge cToA = new DrawableEdge(drawableNodeC, drawableNodeA2);
        trans.drawableNodes.add(drawableNodeA2);
        trans.drawableNodes.add(drawableNodeB);
        trans.drawableNodes.add(drawableNodeC);

        Rule rule = new Rule(simplePattern);
        rule.possibleTranslations.add(trans);

        Pattern graph = new Pattern();
        DrawableNode drawableNodeA3 = node("A");
        graph.drawableNodes.add(drawableNodeA3);

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

        assert graph.drawableNodes.size() == 2*n+1;

    }

    private DrawableNode node(String type) {
        return new DrawableNode(0,0,40, Color.AQUA, type);
    }
}