import graph_generator.controller.GraphController;
import javafx.util.Pair;
import model.*;
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
        graphController = GraphController.getInstance();
        Log.level = Log.LEVEL.INFO;

        rules = new ArrayList<>();

        DrawablePattern matchingDrawablePattern = new DrawablePattern();
        DrawableAreaNode startDrawableAreaNode = node(AREA_TYPE.GRASSFIELD);
        DrawableAreaNode endDrawableAreaNode = node(AREA_TYPE.TOWN);
        DrawableEdge e = new DrawableEdge(startDrawableAreaNode, endDrawableAreaNode);
        matchingDrawablePattern.drawableAreaNodes.add(startDrawableAreaNode);
        matchingDrawablePattern.drawableAreaNodes.add(endDrawableAreaNode);


        Rule matchingRule = new Rule(matchingDrawablePattern);

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
            "starting" on different drawableAreaNodes
         */

        DrawablePattern weirdDrawablePattern = new DrawablePattern();
        DrawableAreaNode lockDrawableAreaNode = node(AREA_TYPE.GRASSFIELD);
        DrawableAreaNode weirdDrawableAreaNode = node(AREA_TYPE.DESERT);
        DrawableEdge e2 = new DrawableEdge(lockDrawableAreaNode, weirdDrawableAreaNode);
        weirdDrawablePattern.drawableAreaNodes.add(lockDrawableAreaNode);
        weirdDrawablePattern.drawableAreaNodes.add(weirdDrawableAreaNode);

        DrawablePattern ourDrawablePattern = new DrawablePattern();
        DrawableAreaNode drawableAreaNode1 = node(AREA_TYPE.GRASSFIELD);
        DrawableAreaNode drawableAreaNode2 = node(AREA_TYPE.TOWN);
        DrawableAreaNode drawableAreaNode3 = node(AREA_TYPE.DESERT);
        DrawableEdge e3 = new DrawableEdge(drawableAreaNode1, drawableAreaNode2);
        DrawableEdge e4 = new DrawableEdge(drawableAreaNode2, drawableAreaNode3);
        ourDrawablePattern.drawableAreaNodes.add(drawableAreaNode1);
        ourDrawablePattern.drawableAreaNodes.add(drawableAreaNode2);
        ourDrawablePattern.drawableAreaNodes.add(drawableAreaNode3);

        Rule weirdRule = new Rule(weirdDrawablePattern);

        rules.add(weirdRule);
        ArrayList<Pair<Rule, Pair<DrawablePattern, DrawablePattern>>> pairArrayList = graphController.rulesMatchingPattern(rules, ourDrawablePattern);

        assert pairArrayList.size() == 2;
    }

    @Test
    public void rulesMatchingPatternOverfull() throws Exception {
        /*
        create pattern containing node with more than 8 edges to/from it
        make sure we get no rules matching
         */
        DrawablePattern overFullDrawablePattern = new DrawablePattern();
        DrawableAreaNode startOverFull = node(AREA_TYPE.GRASSFIELD);
        DrawableAreaNode endOverFull = node(AREA_TYPE.TOWN);
        DrawableEdge drawableEdgeOverfull = new DrawableEdge(startOverFull, endOverFull);
        overFullDrawablePattern.drawableAreaNodes.add(startOverFull);
        overFullDrawablePattern.drawableAreaNodes.add(endOverFull);
        for (int i = 0; i < 8; i++) {
            DrawableAreaNode n = node(AREA_TYPE.DESERT);
            DrawableEdge eTmp = new DrawableEdge(n, startOverFull);
            overFullDrawablePattern.drawableAreaNodes.add(n);
        }
        Log.print("overfullPattern: " + overFullDrawablePattern, Log.LEVEL.DEBUG);

        /*ArrayList<Pair<Rule, DrawablePattern>> pairArrayList = graphController.rulesMatchingPattern(rules, overFullDrawablePattern);
        System.out.println(pairArrayList.size());
        assert pairArrayList.size() == 0;*/
    }

    @Test
    public void applyRule() throws Exception {
        /*
    create rule with simple matching pattern,
        a = b->c->a
    expect drawableAreaNodes size to be 2n+1 after applyingRule n times
    */
        int nodeId = 1337;
        DrawablePattern simpleDrawablePattern =  new DrawablePattern();
        DrawableAreaNode drawableAreaNodeA = node(AREA_TYPE.GRASSFIELD);
        drawableAreaNodeA.setNodeId(nodeId);
        simpleDrawablePattern.drawableAreaNodes.add(drawableAreaNodeA);

        DrawablePattern trans =  new DrawablePattern();
        DrawableAreaNode drawableAreaNodeA2 = node(AREA_TYPE.GRASSFIELD);
        drawableAreaNodeA2.setNodeId(nodeId);
        DrawableAreaNode drawableAreaNodeB = node(AREA_TYPE.DESERT);
        DrawableAreaNode drawableAreaNodeC = node(AREA_TYPE.TOWN);
        DrawableEdge bToC = new DrawableEdge(drawableAreaNodeB, drawableAreaNodeC);
        DrawableEdge cToA = new DrawableEdge(drawableAreaNodeC, drawableAreaNodeA2);
        trans.drawableAreaNodes.add(drawableAreaNodeA2);
        trans.drawableAreaNodes.add(drawableAreaNodeB);
        trans.drawableAreaNodes.add(drawableAreaNodeC);

        Rule rule = new Rule(simpleDrawablePattern);
        rule.possibleTranslations.add(trans);

        DrawablePattern graph = new DrawablePattern();
        DrawableAreaNode drawableAreaNodeA3 = node(AREA_TYPE.GRASSFIELD);
        graph.drawableAreaNodes.add(drawableAreaNodeA3);

        ArrayList<Rule> rules = new ArrayList<Rule>();
        rules.add(rule);
        int n = 20;
        /*for (int i = 0; i < n; i++) {
            ArrayList<Pair<Rule, DrawablePattern>> rulePatternList = graphController.rulesMatchingPattern(rules, graph);
            if (rulePatternList.size() > 0) {
                Pair<Rule, DrawablePattern> pair = rulePatternList.get(0);
                Rule r = pair.getKey();
                DrawablePattern p = pair.getValue();
                Log.print("applying rule", Log.LEVEL.DEBUG);
                Log.print("Before: \n " + graph, Log.LEVEL.DEBUG);
                Log.print("Subpattern: " + p, Log.LEVEL.DEBUG);
                graphController.applyRule(graph, p, r);
                Log.print("After: \n " + graph, Log.LEVEL.DEBUG);
            }
        }*/

        Log.print(graph.toString(), Log.LEVEL.DEBUG);

        assert graph.drawableAreaNodes.size() == 2*n+1;

    }

    private DrawableAreaNode node(AREA_TYPE type) {
        return new DrawableAreaNode(0, 0, type);
    }
}