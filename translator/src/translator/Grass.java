package translator;

import model.enums.TILE_TYPE;
import model.interfaces.Tile;

/**
 * Created by time on 3/22/17.
 */
public class Grass implements Tile {
    @Override
    public TILE_TYPE getTILE_TYPE() {
        return TILE_TYPE.GRASS;
    }
}
