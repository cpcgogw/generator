package model;

import com.sun.org.apache.bcel.internal.generic.ObjectType;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Created by time on 3/23/17.
 */
public class DrawableObjectNode extends Circle implements ObjectNode, Tile {
    private OBJECT_TYPE type;
    private Color color = Color.YELLOW;
    public Text text;
    private int ID;
    private static int IDcount = 0;

    public DrawableObjectNode(double x, double y, int radius, OBJECT_TYPE type) {
        super(x, y, radius);
        this.ID = this.IDcount;
        this.IDcount++;
        this.type = type;
        text = new Text(x, y, ""+type.toString().charAt(0));
        text.setTextOrigin(VPos.CENTER);
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
                color = Color.BURLYWOOD;
                break;
        }
        super.setFill(color);
    }

    public void setPosition(double x, double y) {
        super.setCenterX(x);
        super.setCenterY(y);
        text.setX(x-this.getRadius()/2);
        text.setY(y);
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
