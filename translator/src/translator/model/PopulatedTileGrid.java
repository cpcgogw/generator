package translator.model;

import model.ObjectNode;

/**
 * Created by vilddjur on 3/23/17.
 */
public class PopulatedTileGrid extends TileGrid {
    protected ObjectNode[][] objects;
    public PopulatedTileGrid(int size) {
        super(size);
        objects = new ObjectNode[size][size];
    }

    public boolean addObject(ObjectNode node, int x , int y){
        if (isValidPosition(x, y) && objects[x][y] == null) {
            objects[x][y] = node;
            return true;
        }
        return false;
    }
    public ObjectNode getObject(int x, int y){
        return objects[x][y];
    }
}
