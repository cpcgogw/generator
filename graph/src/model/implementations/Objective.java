package model.implementations;

import javafx.scene.shape.Circle;
import model.enums.OBJECTIVE_TYPE;
import model.interfaces.AreaNode;

/**
 * Created by vilddjur on 4/25/17.
 */
public class Objective extends Circle {
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

    @Override
    public String toString() {
        return "(Parent: "+parent.getNodeId()+" Type: "+getType()+")";
    }
}
