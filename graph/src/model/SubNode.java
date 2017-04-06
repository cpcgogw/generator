package model;

import utils.Log;

import java.util.ArrayList;

/**
 * Created by vilddjur on 3/23/17.
 */
public class SubNode implements Subnode {
    private int id;
    private ArrayList<Edge> edges = new ArrayList<>();
    private OBJECT_TYPE type;

    public SubNode(int id, OBJECT_TYPE type){
        Log.print("SUBNODE IS USED!!!", Log.LEVEL.ERROR);
        this.id = id;
        this.type = type;
    }

    public SubNode(OBJECT_TYPE type){
        id = DrawableAreaNode.idCounter;
        DrawableAreaNode.idCounter++;
        this.type = type;
    }

    @Override
    public OBJECT_TYPE getType() {
        return this.type;
    }

    @Override
    public int getNodeId() {
        return id;
    }

    @Override
    public void addEdge(Edge e) {
        edges.add(e);
    }

    @Override
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    @Override
    public void setNodeId(int id) {
        this.id = id;
    }

    @Override
    public TILE_TYPE getTILE_TYPE() {
        return type.toTile();
    }
}
