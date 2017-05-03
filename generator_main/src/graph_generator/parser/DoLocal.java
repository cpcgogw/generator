package graph_generator.parser;

import graph_generator.controller.GraphController;
import javafx.util.Pair;
import model.implementations.DrawableAreaNode;
import model.implementations.DrawablePattern;
import model.implementations.Rule;
import utils.FileHandler;
import utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 5/3/17.
 */
public class DoLocal extends Command {
    //TODO: Make actual Rule instead of String
    String rule = null;
    List<Rule> localRules;
    final int numOfParams = 1;
    GraphController graphController = GraphController.getInstance();

    //TODO: all the parsing for the rule should be done before usage,
    // preferably in setParams or class calling this function.
    @Override
    public boolean execute(DrawablePattern graph) {
        Log.print("DoLocal: Executing command on graph...", Log.LEVEL.INFO);
        File file = new File("saves/rules/"+rule);
        if (file == null) {
            Log.print("DoLocal: Failed to load file: "+file+".", Log.LEVEL.ERROR);
            return false;
        }

        List<DrawableAreaNode> match = FileHandler.loadMatch(file);
        List<List<DrawableAreaNode>> translationsTMP = FileHandler.loadTranslations(file);
        List<DrawablePattern> translations = new ArrayList<>();

        //Need to do conversion...
        for (List<DrawableAreaNode> nodes : translationsTMP) {
            translations.add(new DrawablePattern((ArrayList<DrawableAreaNode>) nodes));
        }

        Rule r = new Rule(new DrawablePattern((ArrayList<DrawableAreaNode>) match), translations);
        List<Rule> rules = new ArrayList<>();
        rules.add(r);


        Log.print("Matching pattern to graph...", Log.LEVEL.INFO);
        Log.print("Graph: "+graph,Log.LEVEL.DEBUG);
        ArrayList<Pair<Rule, Pair<DrawablePattern, DrawablePattern>>> matches = graphController.rulesMatchingPattern(rules, graph);

        if (matches.size() == 0) {
            Log.print("DoLocal: No rules matching pattern.", Log.LEVEL.WARNING);
            return false;
        } else {
            graphController.applyRulesLocally(localRules, r, graph);
            Log.print("Updated graph: "+graph,Log.LEVEL.DEBUG);
            return true;
        }
    }
    void setRules(List<Rule> rules){
        this.localRules = rules;
    }
    @Override
    boolean setParameters(List<String> params) {
        rule = params.get(0);
        return true;
    }
}
