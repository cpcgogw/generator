package translator.model;

import model.enums.AREA_TYPE;
import model.enums.TILE_TYPE;
import model.interfaces.AreaNode;
import model.interfaces.Edge;
import model.interfaces.Subnode;
import model.interfaces.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 3/11/17.
 */
public class AbstractNode implements AreaNode, Tile {
    private int id;
    private static int idCounter = 0;
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<Subnode> objects = new ArrayList<>();
    private TILE_TYPE type = TILE_TYPE.TOWN;
    public AbstractNode(){
        id = idCounter;
        idCounter++;
    }

    @Override
    public AREA_TYPE getType() {
        return null;
    }

    public List<Subnode> getSubnodes() {
        return objects;
    }

    @Override
    public void addObject(Subnode node) {
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

    @Override
    public void setNodeId(int id) {
        this.id = id;
    }

    public void setType(TILE_TYPE type) {
        this.type = type;
    }
    @Override
    public TILE_TYPE getTILE_TYPE() {
        return type;
    }
}
