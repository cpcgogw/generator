package translator.model;

import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 3/11/17.
 */
public class AbstractNode implements AreaNode, Tile {
    private int id;
    private static int idCounter = 0;
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<ObjectNode> objects = new ArrayList<>();
    private TILE_TYPE type = TILE_TYPE.TOWN;
    public AbstractNode(){
        id = idCounter;
        idCounter++;
    }

    @Override
    public AREA_TYPE getType() {
        return null;
    }

    @Override
    public List<ObjectNode> getObjects() {
        return objects;
    }

    @Override
    public void addObject(ObjectNode node) {
        objects.add(node);
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

    public void setType(TILE_TYPE type) {
        this.type = type;
    }
    @Override
    public TILE_TYPE getTILE_TYPE() {
        return type;
    }
}
