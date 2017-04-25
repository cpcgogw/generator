package model.interfaces;

import model.enums.EDGE_TYPE;

/**
 * Created by vilddjur on 3/11/17.
 */
public interface Edge {
    EDGE_TYPE getType();
    Node getFrom();
    Node getTo();
}
