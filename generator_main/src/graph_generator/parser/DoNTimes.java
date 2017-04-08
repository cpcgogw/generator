package graph_generator.parser;

import model.DrawablePattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by time on 3/4/17.
 */
public class DoNTimes extends Command {
    final int PARAM_SIZE = 2;
    int n;
    String rule;
    Command doOnce;

    public DoNTimes() {
        clearParams();
    }

    @Override
    boolean execute(DrawablePattern graph) {
        if (rule == null || n<=0)
            return false;

        for (int i=0; i < n; i++) {
            doOnce.execute(graph);
        }

        clearParams();
        return true;
    }

    private void clearParams() {
        rule = null;
        n = -1;
    }

    @Override
    boolean setParameters(List<String> params) {
        if (params.size() != PARAM_SIZE)
            return false;

        n = Integer.parseInt(params.get(1));
        if (n<=0)
            return false;

        rule = params.get(0);
        doOnce = new DoOnce();
        List<String> derp = new ArrayList<>();
        derp.add(rule);
        doOnce.setParameters(derp);

        return true;
    }
}
