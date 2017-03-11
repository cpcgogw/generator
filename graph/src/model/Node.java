package model;

import java.util.ArrayList;

/**
 * Created by vilddjur on 3/11/17.
 */
public interface Node {

    int getNodeId();
    void addEdge(Edge e);
    ArrayList<Edge> getEdges();
}
