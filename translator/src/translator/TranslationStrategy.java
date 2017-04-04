package translator;

import model.Subnode;
import model.Tile;
import translator.model.PopulatedTileGrid;
import translator.model.TileGrid;

/**
 * Created by time on 3/22/17.
 */
public abstract class TranslationStrategy {
    public abstract PopulatedTileGrid apply(TileGrid grid);

    public static void copyFromPos(PopulatedTileGrid from, PopulatedTileGrid to, int xPos, int yPos) {
        Tile tile;
        Subnode obj;
        for (int y = 0; y < from.size(); y++) {
            for (int x = 0; x < from.size(); x++) {
                tile = from.getTile(x, y);
                obj = from.getObject(x,y);
                if (tile != null)
                    to.addTile(tile, xPos+x, yPos+y);
                if(obj != null)
                    to.addObject(obj, xPos+x, yPos+y);
            }
        }
    }
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
