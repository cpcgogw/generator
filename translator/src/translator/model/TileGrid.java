package translator.model;

import javafx.util.Pair;
import model.Tile;

import java.util.*;

/**
 * Created by time on 3/18/17.
 */
public class TileGrid {
    protected Tile[][] grid = null;
    protected HashMap<Tile, Pair<Integer, Integer>> placedPositions = new HashMap<>();
    protected List<Tile> placed = new ArrayList<>();

    public TileGrid(int size) {
        grid = new Tile[size][size];
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

    public boolean isValidPosition(int x, int y) {
        if (x >= 0 && x < grid.length &&
                y >= 0 && y < grid[0].length) {
            return true;
        }
        return false;
    }

    public boolean removeTile(int x, int y) {
        if (isValidPosition(x, y) && grid[x][y] != null) {
            placed.remove(grid[x][y]);
            grid[x][y] = null;
            return true;
        }
        return false;
    }

    @Override
    public TileGrid clone() {
        TileGrid tileGrid = new TileGrid(grid.length);
        copy(this, tileGrid);

        return tileGrid;
    }

    public boolean tryPlace(Tile tile, int x, int y) {
        if (placed.contains(tile))
            return false;

        // Check to see no already placed neighbours are out of reach.
        if (isValidPlacement(tile, x, y)) {
            this.addTile(tile, x, y);
            return true;
        }

        return false;
    }

    protected boolean isValidPlacement(Tile tile, int x, int y) {
        if (!isValidPosition(x, y))
            return false;

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
        if (grid[x][y] != null && grid[x][y].equals(node))
            return false;

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
        if (from == null || to == null)
            return false;

        if (from.size() != to.size())
            return false;

        // Equal sided grid.
        for (int x=0; x<from.size(); x++) {
            for (int y=0; y<from.size(); y++) {
                if (from.getTile(x, y) != null)
                    to.addTile(from.getTile(x, y), x, y);
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
    public boolean equals(Object o) {
        if (o instanceof TileGrid) {
            if (((TileGrid) o).size() != this.size())
                return false;

            Set<Tile> first = new HashSet<Tile>(((TileGrid) o).getTiles());
            Set<Tile> second = new HashSet<Tile>(this.getTiles());
            if (first.equals(second)) {
                Set<Pair<Integer, Integer>> firstPositions = new HashSet<>(((TileGrid) o).getPlacedPositions().values());
                Set<Pair<Integer, Integer>> secondPositions = new HashSet<>(this.getPlacedPositions().values());
                if (firstPositions.equals(secondPositions)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String s = "";

        for (int y=0; y<grid[0].length; y++) {
            for (int x=0; x<grid.length; x++) {
                if (grid[x][y] != null) {
                    if (x==0) {
                        s += getTile(x, y).getTILE_TYPE().toString();
                    } else {
                        s += " " + getTile(x, y).getTILE_TYPE().toString();
                    }
                } else {
                    if (x==0)
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
