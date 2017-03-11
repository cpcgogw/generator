package translator.model;

import javafx.util.Pair;
import model.Edge;
import model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to represent node-grid.
 *
 * Created by time on 3/9/17.
 */
public class NodeGrid {
    private Node[][] grid = null;
    //private List<Pair<Integer, Integer>> placed = new ArrayList<>();
    private List<Node> placed = new ArrayList<>();

    public NodeGrid(int size) {
        grid = new Node[size][size];
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

    public boolean addNode(Node node, int x, int y) {
        if (isValidPosition(x, y) && grid[x][y] == null) {
            placed.add(node);
            grid[x][y] = node;
            return true;
        }
        return false;
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

    public boolean removeNode(int x, int y) {
        if (isValidPosition(x, y) && grid[x][y] != null) {
            grid[x][y] = null;
            placed.remove(new Pair<>(x, y));
            return false;
        }
        return true;
    }

    @Override
    public NodeGrid clone() {
        if (grid == null)
            return null;

        NodeGrid nodeGrid = new NodeGrid(grid.length);
        if (copy(this, nodeGrid))
            return nodeGrid;

        return null;
    }

    public boolean tryPlace(Node node, int x, int y) {
        // Check to see no already placed neighbours are out of reach.
        if (isValidPlacement(node, x, y)) {
            this.addNode(node, x, y);
            return true;
        }

        return false;
    }

    private boolean isValidPlacement(Node node, int x, int y) {
        // If space is not free.
        if (getNode(x, y) != null)
            return false;

        // Check neighbours
        for (Edge e : node.getEdges()) {

            // Is already placed so we must be next to it.
            if (placed.contains(e.getTo())) {
                if (!isNeighbour(x, y, e.getTo())) {
                    return false;
                }
            }
        }
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
    private boolean isNeighbour(int x, int y, Node node) {
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

    public static boolean copy(NodeGrid from, NodeGrid to) {
        if (from == null)
            return false;

        // Assuming equal sided grid.
        for (int x=0; x<from.size(); x++) {
            for (int y=0; y<from.size(); y++) {
                to.addNode(from.getNode(x,y), x, y);
            }
        }

        return true;
    }

    private Node getNode(int x, int y) {
        if (isValidPosition(x, y))
            return grid[x][y];

        return null;
    }

    @Override
    public String toString() {
        String s = "";

        for (int x=0; x<grid.length; x++) {
            for (int y=0; y<grid[0].length; y++) {
                if (grid[x][y] != null) {
                    if (y==0)
                        s += getNode(x, y).getNodeId();
                    else
                        s += " "+getNode(x, y).getNodeId();
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
