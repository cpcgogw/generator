package translator;

import javafx.util.Pair;
import model.TILE_TYPE;
import model.Tile;
import translator.model.AbstractNode;
import translator.model.Road;
import translator.model.TileGrid;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by time on 3/22/17.
 */
public class SimpleTranslation extends TranslationStrategy {
    @Override
    public TileGrid apply(TileGrid grid) {
        TileGrid highRes = new TileGrid(grid.size()*10);
        Tile tile;

        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.size(); x++) {
                tile = grid.getTile(x, y);
                if (tile == null) {
                    copyFromPos(grass(10), highRes, x*10, y*10);
                } else if (tile.getTILE_TYPE() == TILE_TYPE.TOWN) {
                    copyFromPos(town(10), highRes, x*10, y*10);
                } else if (tile.getTILE_TYPE() == TILE_TYPE.ROAD) {
                    copyFromPos(road(grid, x, y), highRes, x*10, y*10);
                }
            }
        }

        return highRes;
    }

    private TileGrid grass(int size) {
        TileGrid grass = new TileGrid(size);

        for (int y = 0; y < grass.size(); y++) {
            for (int x = 0; x < grass.size(); x++) {
                grass.addTile(new Grass(), x, y);
            }
        }
        return grass;
    }

    private TileGrid road(TileGrid grid, int xPos, int yPos) {
        TileGrid road = new TileGrid(10);
        Tile tile;
        List<Pair<Integer, Integer>> positions = new ArrayList<>();

        for (int y = yPos-1; y <= yPos+1; y++) {
            for (int x = xPos-1; x <= xPos+1; x++) {
                if (!grid.isValidPosition(x, y))
                    continue;
                if (x == xPos && y == yPos)
                    continue;

                tile = grid.getTile(x, y);
                if (tile != null && tile.getTILE_TYPE() != TILE_TYPE.ROAD) {
                    positions.add(new Pair<>(x, y));
                }
            }
        }

        Log.print("SimpleTranslation: Checking position xPos: "+xPos+", yPos: "+yPos+".", Log.LEVEL.DEBUG);
        Log.print("SimpleTranslation: The positions where neighbours are not empty or road: "+positions, Log.LEVEL.DEBUG);

        if (positions.contains(new Pair<>(xPos-1, yPos)) && positions.contains(new Pair<>(xPos+1, yPos))) {
            for (int i = 0; i < 10; i++) {
                road.addTile(new Road(), i, 4);
            }
        }

        if (positions.contains(new Pair<>(xPos-1, yPos-1)) && positions.contains(new Pair<>(xPos+1, yPos+1))) {
            for (int i = 9; i >= 0; i--) {
                road.addTile(new Road(), i, i);
            }
        }

        if (positions.contains(new Pair<>(xPos-1, yPos+1)) && positions.contains(new Pair<>(xPos+1, yPos-1))) {
            for (int i = 9; i >= 0; i--) {
                road.addTile(new Road(), i, (9 - i));
            }
        }

        if (positions.contains(new Pair<>(xPos-1, yPos+1)) && positions.contains(new Pair<>(xPos+1, yPos-1))) {
            for (int i = 9; i >= 0; i--) {
                road.addTile(new Road(), i, (9 - i));
            }
        }

        return road;
    }

    private TileGrid town(int size) {
        TileGrid town = new TileGrid(size);
        for (int y = 0; y < town.size(); y++) {
            for (int x = 0; x < town.size(); x++) {
                town.addTile(new AbstractNode(), x, y);
            }
        }
        return town;
    }

    private TileGrid empty(int size) {
        TileGrid empty = new TileGrid(size);
        return empty;
    }
}
