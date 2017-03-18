package translator.model;

import model.Edge;
import model.Node;

import java.util.ArrayList;

/**
 * Created by vilddjur on 3/11/17.
 */
public class AbstractNode implements Node, Tile {
    private int id;
    private static int idCounter = 0;
    private ArrayList<Edge> edges = new ArrayList<>();
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

    @Override
    public String getRepresentation() {
        return ""+id;
    }
}
