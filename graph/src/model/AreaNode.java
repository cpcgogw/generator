package model;

import java.util.List;

/**
 * Created by vilddjur on 3/23/17.
 */
public interface AreaNode extends Node {
    AREA_TYPE getType();
    List<ObjectNode> getObjects();
    void addObject(ObjectNode node);
}
