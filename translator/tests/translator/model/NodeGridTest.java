package translator.model;

import model.Edge;
import model.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by time on 3/19/17.
 */
public class NodeGridTest {
    private NodeGrid nodeGrid;
    private final int GRID_SIZE = 3;

    @Before
    public void setup() throws Exception {
        nodeGrid = new NodeGrid(GRID_SIZE);
    }

    @Test
    public void isValidPlacement() throws Exception {
        Node n1 = new AbstractNode();
        Node n2 = new AbstractNode();
        Node n3 = new AbstractNode();
        Node n4 = new AbstractNode();

        Edge e1 = new AbstractEdge(n1, n2);
        Edge e2 = new AbstractEdge(n2, n1);
        Edge e3 = new AbstractEdge(n1, n3);
        Edge e4 = new AbstractEdge(n1, n4);

        n1.addEdge(e1);
        n1.addEdge(e2);
        n1.addEdge(e3);
        n1.addEdge(e4);
        n2.addEdge(e1);
        n2.addEdge(e2);
        n3.addEdge(e3);
        n4.addEdge(e4);

        assertTrue(nodeGrid.isValidPlacement((Tile) n1, 0, 0));
        assertTrue(nodeGrid.isValidPlacement((Tile) n2, 0, 0));
        assertTrue(nodeGrid.isValidPlacement((Tile) n3, 0, 0));
        assertTrue(nodeGrid.isValidPlacement((Tile) n4, 0, 0));
        assertFalse(nodeGrid.isValidPlacement((Tile) n4, -1, -1));
        assertTrue(nodeGrid.addTile((Tile) n1, 0, 0));
        assertFalse(nodeGrid.isValidPlacement((Tile) n2, 0, 0));
        assertTrue(nodeGrid.isValidPlacement((Tile) n4, 1, 1));
        assertTrue(nodeGrid.addTile((Tile) n4, 1, 1));
        assertFalse(nodeGrid.isValidPlacement((Tile) n2, 2, 2));
    }

    @Test
    public void getNodes() throws Exception {
        Road r1 = new Road();
        Road r2 = new Road();

        Node n1 = new AbstractNode();
        Node n2 = new AbstractNode();
        Node n3 = new AbstractNode();
        Node n4 = new AbstractNode();

        List<Node> nodeList = new ArrayList<>();

        nodeList.add(n1);
        nodeList.add(n2);
        nodeList.add(n3);
        nodeList.add(n4);

        assertTrue(nodeGrid.addTile((Tile) n1, 0, 0));
        assertTrue(nodeGrid.addTile((Tile) n2, 1, 0));
        assertTrue(nodeGrid.addTile((Tile) n3, 2, 0));
        assertTrue(nodeGrid.addTile((Tile) n4, 2, 2));
        assertTrue(nodeGrid.addTile(r1, 1, 1));
        assertTrue(nodeGrid.addTile(r2, 1, 2));
        assertEquals(nodeList, nodeGrid.getNodes());
    }

    @Test
    public void cloneTest() throws Exception {
        nodeGrid = simpleGrid();
        NodeGrid nodeGrid2 = nodeGrid.clone();
        assertEquals(nodeGrid, nodeGrid2);
    }

    private NodeGrid simpleGrid() {
        NodeGrid grid = new NodeGrid(GRID_SIZE);

        Node n1 = new AbstractNode();
        Node n2 = new AbstractNode();
        Node n3 = new AbstractNode();
        Node n4 = new AbstractNode();

        Edge e1 = new AbstractEdge(n1, n2);
        Edge e2 = new AbstractEdge(n2, n1);
        Edge e3 = new AbstractEdge(n1, n3);
        Edge e4 = new AbstractEdge(n1, n4);

        n1.addEdge(e1);
        n1.addEdge(e2);
        n1.addEdge(e3);
        n1.addEdge(e4);
        n2.addEdge(e1);
        n2.addEdge(e2);
        n3.addEdge(e3);
        n4.addEdge(e4);

        grid.addTile((Tile) n1, 0, 0);
        grid.addTile((Tile) n2, 1, 0);
        grid.addTile((Tile) n3, 0, 1);
        grid.addTile((Tile) n4, 1, 1);

        return grid;
    }
}
