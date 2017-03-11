package graph_generator.parser;

import model.DrawablePattern;

import java.util.List;

/**
 * Abstract command
 * Created by time on 3/4/17.
 */
public abstract class Command {
    abstract boolean execute(DrawablePattern graph);
    abstract boolean setParameters(List<String> params);

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
