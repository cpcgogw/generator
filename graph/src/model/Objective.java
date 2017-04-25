package model;

import javafx.scene.shape.Circle;

/**
 * Created by vilddjur on 4/25/17.
 */
public class Objective extends Circle{
    private final AreaNode parent;
    private final OBJECTIVE_TYPE type;
    public Objective(AreaNode parent, OBJECTIVE_TYPE type) {
        this.parent = parent;
        this.type = type;
    }

    public AreaNode getParentNode() {
        return parent;
    }

    public OBJECTIVE_TYPE getType() {
        return type;
    }
}
