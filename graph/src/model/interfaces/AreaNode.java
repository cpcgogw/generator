package model.interfaces;

import model.enums.AREA_TYPE;

import java.util.List;

/**
 * Created by vilddjur on 3/23/17.
 */
public interface AreaNode extends Node {
    AREA_TYPE getType();
    List<? extends Subnode> getSubnodes();
    void addObject(Subnode node);
}
