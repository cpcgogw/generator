package translator;

import javafx.util.Pair;
import model.AreaNode;
import model.ObjectNode;
import model.TILE_TYPE;
import model.Tile;
import translator.model.AbstractNode;
import translator.model.PopulatedTileGrid;
import translator.model.Road;
import translator.model.TileGrid;
import utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simple translation strategy which expands the grid by 10 and performs a simple translation.
 *
 * Created by time on 3/22/17.
 */
public class SimpleTranslation extends TranslationStrategy {
    final int TILE_SIZE = 10;
    private final Random rand = new Random();
    @Override
    public PopulatedTileGrid apply(TileGrid grid) {
        PopulatedTileGrid popGrid = new PopulatedTileGrid(grid.size()*TILE_SIZE);
        Tile tile = null;

        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.size(); x++) {
                tile = grid.getTile(x, y);
                if (tile == null) {
                    continue;
                } else if (tile.getTILE_TYPE() == TILE_TYPE.TOWN) {
                    copyFromPos(town(10, tile), popGrid, x*TILE_SIZE, y*TILE_SIZE);
                } else if (tile.getTILE_TYPE() == TILE_TYPE.ROAD) {
                    copyFromPos(road(grid, x, y), popGrid, x*TILE_SIZE, y*TILE_SIZE);
                }
            }
        }
        grass(popGrid);

        return popGrid;
    }

    /**
     * Places grassland on all non-tiled tiles in grid.
     *
     */
    private void grass(TileGrid grid) {

        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.size(); x++) {
                if (grid.getTile(x, y) == null)
                    grid.addTile(new Grass(), x, y);
            }
        }
    }

    /**
     * Creates a road TileGrid.
     *
     * @param grid
     * Grid which road tile is placed on. Needed for determining paths to neighbours.
     * @param xPos
     * The x-coordinate where road is placed on grid.
     * @param yPos
     * The y-coordinate where road is placed on grid.
     * @return
     * The road TileGrid with size TILE_SIZE which has valid paths to neighbours in input grid.
     */
    private PopulatedTileGrid road(TileGrid grid, int xPos, int yPos) {
        PopulatedTileGrid road = new PopulatedTileGrid(TILE_SIZE);
        List<Pair<Integer, Integer>> neighbours = getNeighbours(grid, xPos, yPos);

        if (neighbours.isEmpty())
            return road;

        Log.print("SimpleTranslation: Checking position xPos: "+xPos+", yPos: "+yPos+".", Log.LEVEL.DEBUG);
        Log.print("SimpleTranslation: The positions where neighbours are not empty or road: "+neighbours, Log.LEVEL.DEBUG);

        if (placeRoads(road, xPos, yPos, neighbours)) {
            Log.print("SimpleTranslation: Road placed!", Log.LEVEL.DEBUG);
        } else {
            Log.print("SimpleTranslation: No road placed!", Log.LEVEL.DEBUG);
        }

        return road;
    }

    /**
     * Place road tiles on grid between positions if crossing.
     *  @param grid
     * Grid to place roads on.
     * @param xPos
     * The x-coordinate of tile to check for crossing.
     * @param yPos
 * The y-coordinate of tile to check for crossing.
     * @param positions
     */
    private boolean placeRoads(TileGrid grid, int xPos, int yPos, List<Pair<Integer, Integer>> positions) {
        boolean placed = false;
        if (positions.contains(new Pair<>(xPos-1, yPos)) && positions.contains(new Pair<>(xPos+1, yPos))) {
            for (int i = 0; i < 10; i++) {
                grid.addTile(new Road(), i, 4);
            }
            placed = true;
        }

        if (positions.contains(new Pair<>(xPos-1, yPos-1)) && positions.contains(new Pair<>(xPos+1, yPos+1))) {
            for (int i = 9; i >= 0; i--) {
                grid.addTile(new Road(), i, i);
            }
            placed = true;
        }

        if (positions.contains(new Pair<>(xPos-1, yPos+1)) && positions.contains(new Pair<>(xPos+1, yPos-1))) {
            for (int i = 9; i >= 0; i--) {
                grid.addTile(new Road(), i, (9 - i));
            }
            placed = true;
        }

        if (positions.contains(new Pair<>(xPos-1, yPos+1)) && positions.contains(new Pair<>(xPos+1, yPos-1))) {
            for (int i = TILE_SIZE-1; i >= 0; i--) {
                grid.addTile(new Road(), i, (TILE_SIZE-1 - i));
            }
            placed = true;
        }
        if (positions.contains(new Pair<>(xPos, yPos+1)) && positions.contains(new Pair<>(xPos, yPos-1))) {
            for (int i = TILE_SIZE-1; i >= 0; i--) {
                grid.addTile(new Road(), 4, i);
            }
            placed = true;
        }

        return placed;
    }

    private List<Pair<Integer, Integer>> getNeighbours(TileGrid grid, int xPos, int yPos) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        Tile tile;

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

        return positions;
    }

    private PopulatedTileGrid town(int size, Tile tile) {

        PopulatedTileGrid town = new PopulatedTileGrid(size);
        if(tile instanceof AreaNode){
            placeObjectsRandomly((AreaNode) tile, town);
        }
        for (int y = 0; y < town.size(); y++) {
            for (int x = 0; x < town.size(); x++) {
                town.addTile(new AbstractNode(), x, y);
            }
        }
        return town;
    }

    private void placeObjectsRandomly(AreaNode tile, PopulatedTileGrid grid) {
        for (ObjectNode node :
                tile.getObjects()) {
            boolean placed = false;
            while(!placed){
                placed = grid.addObject(node, rand.nextInt(grid.size()), rand.nextInt(grid.size()));
            }
        }
    }

    private TileGrid empty(int size) {
        TileGrid empty = new TileGrid(size);
        return empty;
    }
}
