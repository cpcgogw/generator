package model.implementations;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.enums.OBJECTIVE_TYPE;
import model.interfaces.AreaNode;

import java.util.Random;

/**
 * Created by vilddjur on 4/25/17.
 */
public class Objective extends Circle {
    private final AreaNode parent;
    private final OBJECTIVE_TYPE type;

    public Objective(AreaNode parent, OBJECTIVE_TYPE type) {
        this.parent = parent;
        this.type = type;

        this.setRadius(5);
        this.setFill(Color.DARKGOLDENROD);
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

    public void update(Integer placed) {
        this.setCenterX(((DrawableAreaNode)parent).getCenterX()+Math.cos(Math.toRadians(30*placed))*30);
        this.setCenterY(((DrawableAreaNode)parent).getCenterY()+Math.sin(Math.toRadians(30*placed))*30);
    }
}
