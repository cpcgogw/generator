package translator.model;

import model.TILE_TYPE;
import model.Tile;

/**
 * Created by time on 3/18/17.
 */
public class Road implements Tile {

    @Override
    public TILE_TYPE getTILE_TYPE() {
        return TILE_TYPE.ROAD;
    }
}
