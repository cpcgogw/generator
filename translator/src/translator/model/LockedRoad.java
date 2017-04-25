package translator.model;

import model.enums.TILE_TYPE;
import model.interfaces.Tile;

/**
 * Created by vilddjur on 4/5/17.
 */
public class LockedRoad implements Tile {
    @Override
    public TILE_TYPE getTILE_TYPE() {
        return TILE_TYPE.LOCKED_ROAD;
    }
}
