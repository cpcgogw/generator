package model.interfaces;

import model.enums.OBJECT_TYPE;

/**
 * Created by vilddjur on 3/23/17.
 */
public interface Subnode extends Node, Tile {
    OBJECT_TYPE getType();
}
