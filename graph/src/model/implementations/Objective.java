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

    public void update() {
        //NOTE: Ugly fast fix for checking correct positions of objectives
        //TODO: Place objectives in a more intelligent manner (possibly like how its done for subnodes)
        Random random = new Random();
        this.setCenterX(((DrawableAreaNode)parent).getCenterX()-40-random.nextInt(30));
        this.setCenterY(((DrawableAreaNode)parent).getCenterY()+40+random.nextInt(30));
    }
}
