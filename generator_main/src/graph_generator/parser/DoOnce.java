package graph_generator.parser;

import graph_generator.controller.GraphController;
import javafx.util.Pair;
import model.Edge;
import model.Node;
import model.Pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.Rule;
import rule_editor.FileHandler;
import rule_editor.controller.Controller;
import sun.rmi.server.LoaderHandler;
import utils.Log;

/**
 * Created by time on 3/4/17.
 */
public class DoOnce extends Command {
    String rule = null;
    final int numOfParams = 1;
    //TODO: should be a singleton so graph cant get into a weird state.
    GraphController graphController = new GraphController();

    //TODO: all the parsing for the rule should be done before usage,
    // preferably in setParams or class calling this function.
    @Override
    public boolean execute(Pattern graph) {
        Log.print("DoOnce: Executing command on graph...", Log.LEVEL.INFO);
        File file = new File("saves/rules/"+rule);
        if (file == null) {
            Log.print("DoOnce: Failed to load file: "+file+".", Log.LEVEL.ERROR);
            return false;
        }

        Pair<ArrayList<Node>,ArrayList<Edge>> ruleR = FileHandler.LoadNodes(file);
        Rule r = new Rule(new Pattern(FileHandler.LoadMatchingPattern(file)), FileHandler.LoadTranslations(file));
        ArrayList<Rule> rl = new ArrayList<>();
        rl.add(r);

        Log.print("Matching pattern to graph...", Log.LEVEL.INFO);
        Log.print("Graph: "+graph,Log.LEVEL.DEBUG);
        ArrayList<Pair<Rule, Pattern>> rulePatternList = graphController.rulesMatchingPattern(rl, graph);

        if (rulePatternList.size() == 0) {
            Log.print("DoOnce: No rules matching pattern.", Log.LEVEL.WARNING);
            return false;
        } else {
            graphController.applyRule(graph, rulePatternList.get(0).getValue(), r);
            Log.print("Updated graph: "+graph,Log.LEVEL.DEBUG);
            return true;
        }
    }

    @Override
    public boolean setParameters(List<String> params) {
        if (params.size() != numOfParams)
            return false;

        rule = params.get(0);
        return true;
    }
}
