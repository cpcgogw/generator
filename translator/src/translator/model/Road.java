package translator.model;

import model.Tile;

/**
 * Created by time on 3/18/17.
 */
public class Road implements Tile {

    @Override
    public String getRepresentation() {
        return "R";
    }
}
