package model;

/**
 * Created by vilddjur on 3/11/17.
 */
public interface Edge {
    EDGE_TYPE getType();
    Node getFrom();
    Node getTo();
}
