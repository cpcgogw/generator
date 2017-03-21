package translator.model;

import model.Edge;
import model.Node;
import model.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to represent node-grid.
 *
 * Created by time on 3/9/17.
 */
public class NodeGrid extends TileGrid {

    public NodeGrid(int size) {
        super(size);
    }

    @Override
    protected boolean isValidPlacement(Tile tile, int x, int y) {
        if (!super.isValidPlacement(tile, x, y))
            return false;

        Node node = (Node) tile;

        // Check neighbours
        for (Edge e : node.getEdges()) {

            // Is already placed so we must be next to it.
            if (placed.contains(e.getTo())) {
                if (!isNeighbour(x, y, (Tile) e.getTo())) {
                    return false;
                }
            }else if(placed.contains(e.getFrom())){
                if (!isNeighbour(x, y, (Tile) e.getFrom())) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Node> getNodes() {
        List<Tile> tiles = getTiles();
        List<Node> nodes = new ArrayList<>();

        for (Tile t : tiles) {
            if (t instanceof Node)
                nodes.add((Node) t);
        }
        return nodes;
    }

    @Override
    public NodeGrid clone() {
        NodeGrid nodeGrid = new NodeGrid(grid.length);

        if (copy(this, nodeGrid))
            return nodeGrid;

        return null;
    }
}
