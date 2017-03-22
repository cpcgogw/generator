package translator.model;

import model.Edge;
import model.Node;
import model.TILE_TYPE;
import model.Tile;

import java.util.ArrayList;

/**
 * Created by vilddjur on 3/11/17.
 */
public class AbstractNode implements Node, Tile {
    private int id;
    private static int idCounter = 0;
    private ArrayList<Edge> edges = new ArrayList<>();
    private TILE_TYPE type = TILE_TYPE.TOWN;
    public AbstractNode(){
        id = idCounter;
        idCounter++;
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
