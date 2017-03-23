package model;

import com.sun.org.apache.bcel.internal.generic.ObjectType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

/**
 * Created by time on 3/23/17.
 */
public class DrawableObjectNode extends Circle implements ObjectNode, Tile {
    private OBJECT_TYPE type;
    private Color color = Color.YELLOW;

    public DrawableObjectNode(double x, double y, int radius, OBJECT_TYPE type) {
        super(x, y, radius);
        this.type = type;
        this.setColor();
    }

    private void setColor() {
        switch (type) {
            case KEY:
                color = Color.AQUAMARINE;
                break;
            case LOCK:
                color = Color.BISQUE;
                break;
            case START:
                color = Color.VIOLET;
                break;
            case MONSTER:
                color = Color.CORNFLOWERBLUE;
                break;
            case END:
                color = Color.AZURE;
                break;
        }
        super.setFill(color);
    }

    public void setPosition(double x, double y) {
        super.setCenterX(x);
        super.setCenterY(y);
    }

    @Override
    public TILE_TYPE getTILE_TYPE() {
        return null;
    }

    @Override
    public OBJECT_TYPE getType() {
        return type;
    }

    @Override
    public int getNodeId() {
        return 0;
    }

    @Override
    public void addEdge(Edge e) {

    }

    @Override
    public ArrayList<Edge> getEdges() {
        return null;
    }

}
