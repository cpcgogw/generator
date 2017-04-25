package translator.model;

import model.interfaces.Subnode;

/**
 * Created by vilddjur on 3/23/17.
 */
public class PopulatedTileGrid extends TileGrid {
    protected Subnode[][] objects;
    public PopulatedTileGrid(int size) {
        super(size);
        objects = new Subnode[size][size];
    }

    public boolean addObject(Subnode node, int x , int y){
        if (isValidPosition(x, y) && objects[x][y] == null) {
            objects[x][y] = node;
            return true;
        }
        return false;
    }
    public Subnode getObject(int x, int y){
        return objects[x][y];
    }
}
