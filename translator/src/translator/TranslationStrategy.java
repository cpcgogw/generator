package translator;

import model.Tile;
import translator.model.TileGrid;

/**
 * Created by time on 3/22/17.
 */
public abstract class TranslationStrategy {
    public abstract TileGrid apply(TileGrid grid);

    public static void copyFromPos(TileGrid from, TileGrid to, int xPos, int yPos) {
        Tile tile;
        for (int y = 0; y < from.size(); y++) {
            for (int x = 0; x < from.size(); x++) {
                tile = from.getTile(x, y);
                if (tile != null)
                    to.addTile(tile, xPos+x, yPos+y);
            }
        }
    }
}
