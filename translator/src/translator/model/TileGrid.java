package translator.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by time on 3/18/17.
 */
public class TileGrid {
    protected Tile[][] grid = null;
    protected HashMap<Tile, Pair<Integer, Integer>> placedPositions = new HashMap<>();
    protected List<Tile> placed = new ArrayList<>();

    public TileGrid(int size) {
        grid = new Tile[size][size];
        //initGrid();
    }

    private void initGrid() {
        for (int x=0; x<grid.length; x++) {
            for (int y=0; y<grid[0].length; y++) {
                grid[x][y] = null;
            }
        }
    }

    public int size() {
        return grid.length;
    }

    public boolean addTile(Tile tile, int x, int y) {
        if (isValidPosition(x, y) && grid[x][y] == null) {
            placed.add(tile);
            placedPositions.put(tile, new Pair<>(x,y));
            grid[x][y] = tile;
            return true;
        }
        return false;
    }

    public HashMap<Tile, Pair<Integer, Integer>> getPlacedPositions() {
        return placedPositions;
    }

    private boolean isValidPosition(int x, int y) {
        if (grid == null)
            return false;

        if (x >= 0 && x < grid.length &&
                y >= 0 && y < grid[0].length) {
            return true;
        }
        return false;
    }

    public boolean removeTile(int x, int y) {
        if (isValidPosition(x, y) && grid[x][y] != null) {
            grid[x][y] = null;
            placed.remove(new Pair<>(x, y));
            return false;
        }
        return true;
    }

    @Override
    public TileGrid clone() {
        if (grid == null)
            return null;

        TileGrid tileGrid = new TileGrid(grid.length);
        if (copy(this, tileGrid))
            return tileGrid;

        return null;
    }

    public boolean tryPlace(Tile tile, int x, int y) {
        // Check to see no already placed neighbours are out of reach.
        if (isValidPlacement(tile, x, y)) {
            this.addTile(tile, x, y);
            return true;
        }

        return false;
    }

    protected boolean isValidPlacement(Tile tile, int x, int y) {
        // If space is not free.
        if (getTile(x, y) != null)
            return false;

        return true;
    }

    /**
     * Checks to see whether the given node is a neighbour to position (x,y) on grid.
     *
     * @param x
     * X coordinate of origin.
     * @param y
     * Y coordinate of origin.
     * @param node
     * DrawableNode to be searched for.
     * @return
     * True if node is neighbour to position (x,y), otherwise False.
     */
    protected boolean isNeighbour(int x, int y, Tile node) {
        // Iterate through all neighbours
        for (int i=y-1; i<=y+1; i++) {
            for (int j = x-1; j<=x+1; j++) {
                // Only check inside the grid
                if (isValidPosition(i, j)) {
                    if (grid[j][i] == node) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean copy(TileGrid from, TileGrid to) {
        if (from == null)
            return false;

        // Assuming equal sided grid.
        for (int x=0; x<from.size(); x++) {
            for (int y=0; y<from.size(); y++) {
                to.addTile(from.getTile(x,y), x, y);
            }
        }

        return true;
    }

    public Tile getTile(int x, int y) {
        if (isValidPosition(x, y))
            return grid[x][y];

        return null;
    }

    public Pair<Integer, Integer> getNodePosition(Tile tile) {
        return placedPositions.get(tile);
    }
    public List<Tile> getTiles() {
        return placed;
    }

    @Override
    public String toString() {
        String s = "";

        for (int x=0; x<grid.length; x++) {
            for (int y=0; y<grid[0].length; y++) {
                if (grid[x][y] != null) {
                    if (y==0) {
                        s += getTile(x, y).getRepresentation();
                    } else {
                        s += " " + getTile(x, y).getRepresentation();
                    }
                } else {
                    if (y==0)
                        s += "*";
                    else
                        s += " *";
                }
            }
            s += "\n";
        }
        return s;
    }
}
