package model;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Created by time on 3/23/17.
 */
public class DrawableSubnode extends Circle implements Subnode, Tile {
    private static final int DEFAULT_RADIUS = 10;
    private OBJECT_TYPE type;
    private Color color = Color.YELLOW;
    public Text text;
    public Path path = new Path();
    private int ID;
    private static int IDcount = 1000;
    private ArrayList<DrawableEdge> drawableEdges = new ArrayList<>();

    public DrawableSubnode(double x, double y, OBJECT_TYPE type) {
        this(x, y, IDcount++, type);
        drawEdges();
    }

    public DrawableSubnode(double x, double y, int id, OBJECT_TYPE type) {
        super(x, y, DEFAULT_RADIUS);
        this.ID = id;
        this.type = type;
        text = new Text(x, y, ""+type.toString().charAt(0));
        text.setTextOrigin(VPos.CENTER);
        this.setColor();
    }

    public Shape drawEdges(){
        path.getElements().clear();
        for(DrawableEdge e : drawableEdges){
            path.getElements().add(new MoveTo(this.getCenterX(), this.getCenterY()));
            path.getElements().add(new LineTo(e.getMiddleX(), e.getMiddleY()));
        }
        return path;
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
        return ID;
    }

    @Override
    public void addEdge(Edge e) {
        addEdge((DrawableEdge) e);
    }

    @Override
    public ArrayList<? extends Edge> getEdges() {
        return drawableEdges;
    }

    public void addEdge(DrawableEdge e) {
        drawableEdges.add(e);
    }

    public ArrayList<DrawableEdge> getDrawableEdges() {
        return drawableEdges;
    }
}
