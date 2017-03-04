package graph_generator.parser;

import graph_generator.controller.GraphController;
import model.Rule;

import java.util.List;

/**
 * Created by time on 3/4/17.
 */
public class DoOnce extends Command {
    String rule = null;
    final int numOfParams = 1;
    GraphController graphController = new GraphController();

    @Override
    public boolean execute() {
        //List<Pair<Rule, Pattern>> matches = graphController.getRuleMatching(rule)
        //if match.size() > 0
        //>choose random match and apply rule
        //else
        //return false;

        //graphController.applyRule(r, graph, pattern);
        if (rule != null)
            return true;
        return false;
    }

    @Override
    public boolean setParameters(List<String> params) {
        if (params.size() != numOfParams)
            return false;

        rule = params.get(0);
        return true;
    }
}
