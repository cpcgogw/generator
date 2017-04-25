package translator.model;

import javafx.util.Pair;
import model.interfaces.Edge;
import model.interfaces.Node;
import model.interfaces.Tile;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by time on 3/18/17.
 */
public class TileGridTest {
    private TileGrid tileGrid;
    private final int GRID_SIZE = 3;

    @Before
    public void init() {
        tileGrid = new TileGrid(GRID_SIZE);
    }

    @Test
    public void size() throws Exception {
        assertEquals(tileGrid.size(), GRID_SIZE);
    }

    @Test
    public void addTile() throws Exception {
        assertNotNull(tileGrid.getTiles());
        assertTrue(tileGrid.getTiles().isEmpty());
        assertFalse(tileGrid.addTile(new Road(),  -1, -1));
        assertFalse(tileGrid.addTile(new Road(), GRID_SIZE+1, GRID_SIZE+1));
        assertTrue(tileGrid.addTile(new Road(), 0, 0));
        assertEquals(tileGrid.getTiles().size(), 1);
        assertFalse(tileGrid.addTile(new Road(), 0, 0));
        assertTrue(tileGrid.addTile(new Road(), GRID_SIZE-1, GRID_SIZE-1));
        assertEquals(tileGrid.getTiles().size(), 2);
        assertNull(tileGrid.getTile(-1, -1));
    }

    @Test
    public void getPlacedPositions() throws Exception {
        Road r1 = new Road();
        Road r2 = new Road();

        assertEquals(tileGrid.getPlacedPositions().size(), 0);
        assertTrue(tileGrid.addTile(r1, 0, 0));
        assertEquals(tileGrid.getPlacedPositions().size(), 1);
        assertEquals(tileGrid.getPlacedPositions().get(r1), new Pair<>(0, 0));
        assertTrue(tileGrid.addTile(r2, 2, 2));
        assertEquals(tileGrid.getPlacedPositions().size(), 2);
        assertEquals(tileGrid.getPlacedPositions().get(r2), new Pair<>(2, 2));
    }

    @Test
    public void removeTile() throws Exception {
        assertEquals(tileGrid.getTiles().size(), 0);
        assertFalse(tileGrid.removeTile(1,0));
        assertEquals(tileGrid.getTiles().size(), 0);
        assertTrue(tileGrid.addTile(new Road(), 0, 0));
        assertEquals(tileGrid.getTiles().size(), 1);
        assertTrue(tileGrid.removeTile(0,0));
        assertEquals(tileGrid.getTiles().size(), 0);
        assertTrue(tileGrid.addTile(new Road(), GRID_SIZE-1, GRID_SIZE-1));
        assertFalse(tileGrid.removeTile(0,0));
        assertEquals(tileGrid.getTiles().size(), 1);
    }

    @Test
    public void cloneTest() throws Exception {
        assertTrue(tileGrid.addTile(new Road(), 0, 0));
        assertTrue(tileGrid.addTile(new Road(), 1, 1));
        assertTrue(tileGrid.addTile(new Road(), 2, 2));
        TileGrid tileGrid2 = tileGrid.clone();
        assertEquals(tileGrid, tileGrid2);
    }

    @Test
    public void tryPlace() throws Exception {
        Node n1 = new AbstractNode();
        Node n2 = new AbstractNode();
        Node n3 = new AbstractNode();
        Edge e1 = new AbstractEdge(n1, n2);
        Edge e2 = new AbstractEdge(n2, n1);
        Edge e3 = new AbstractEdge(n2, n3);

        n1.addEdge(e1);
        n1.addEdge(e2);
        n2.addEdge(e1);
        n2.addEdge(e2);
        n2.addEdge(e3);
        n3.addEdge(e3);

        assertTrue(tileGrid.tryPlace((Tile) n1, 0, 0));
        assertFalse(tileGrid.tryPlace((Tile) n2, 0, 0));
        assertTrue(tileGrid.tryPlace((Tile) n2, 1, 1));
        assertFalse(tileGrid.tryPlace((Tile) n2, 2, 2));
        assertFalse(tileGrid.tryPlace((Tile) n2, 0, 1));
        assertTrue(tileGrid.tryPlace((Tile) n3, 2, 1));
    }

    @Test
    public void isValidPlacement() throws Exception {
        assertTrue(tileGrid.isValidPlacement(new Road(), 0, 0));
        assertTrue(tileGrid.isValidPlacement(new Road(), GRID_SIZE-1, GRID_SIZE-1));
        assertTrue(tileGrid.isValidPlacement(new Road(), 0, GRID_SIZE-1));
        assertFalse(tileGrid.isValidPlacement(new Road(), 0, GRID_SIZE+3));
        assertFalse(tileGrid.isValidPlacement(new Road(), -1, 0));
        assertTrue(tileGrid.addTile(new Road(), 0, 0));
        assertFalse(tileGrid.isValidPlacement(new Road(), 0, 0));
    }

    @Test
    public void isNeighbour() throws Exception {
        Road r1 = new Road();
        Road r2 = new Road();
        Road r3 = new Road();

        assertTrue(tileGrid.addTile(r1, 0, 0));
        assertTrue(tileGrid.addTile(r2, 1, 1));
        assertTrue(tileGrid.addTile(r3, 2, 2));
        assertFalse(tileGrid.isNeighbour(0,0, r1));
        assertTrue(tileGrid.isNeighbour(0,0, r2));
        assertFalse(tileGrid.isNeighbour(0,0, r3));
        assertTrue(tileGrid.isNeighbour(1,1, r1));
        assertTrue(tileGrid.isNeighbour(1,1, r3));
    }

    @Test
    public void copy() throws Exception {
        TileGrid tileGrid2 = new TileGrid(GRID_SIZE);
        TileGrid tileGrid3 = new TileGrid(GRID_SIZE+1);

        assertTrue(tileGrid.addTile(new Road(), 0, 0));
        assertTrue(tileGrid.addTile(new Road(), GRID_SIZE-1, 0));
        assertTrue(tileGrid.addTile(new Road(), GRID_SIZE-1, GRID_SIZE-1));


        assertNotEquals(tileGrid, tileGrid2);
        assertNotEquals(tileGrid2, tileGrid3);
        assertNotEquals(tileGrid, tileGrid3);
        assertTrue(TileGrid.copy(tileGrid, tileGrid2));
        assertFalse(TileGrid.copy(tileGrid, tileGrid3));
        assertEquals(tileGrid, tileGrid2);
        assertFalse(TileGrid.copy(tileGrid, null));
    }

    @Test
    public void getTile() throws Exception {
        Road first = new Road();
        Road second = new Road();
        assertTrue(tileGrid.addTile(first, 0, 0));
        assertTrue(tileGrid.addTile(second, GRID_SIZE-1, GRID_SIZE-1));
        assertEquals(tileGrid.getTile(0,0), first);
        assertEquals(tileGrid.getTile(GRID_SIZE-1,GRID_SIZE-1), second);
    }

    @Test
    public void getNodePosition() throws Exception {
        Road r1 = new Road();
        Road r2 = new Road();
        Road r3 = new Road();

        assertTrue(tileGrid.addTile(r1, 0, 0));
        assertTrue(tileGrid.addTile(r2, 1, 1));
        assertTrue(tileGrid.addTile(r3, 2, 2));

        assertEquals(tileGrid.getNodePosition(r1), new Pair<>(0, 0));
        assertEquals(tileGrid.getNodePosition(r2), new Pair<>(1, 1));
        assertEquals(tileGrid.getNodePosition(r3), new Pair<>(2, 2));
    }

    @Test
    public void getTiles() throws Exception {
        Road r1 = new Road();
        Road r2 = new Road();
        Road r3 = new Road();

        assertTrue(tileGrid.addTile(r1, 0,0));
        assertTrue(tileGrid.addTile(r2, GRID_SIZE/2,GRID_SIZE/2));
        assertTrue(tileGrid.addTile(r3, GRID_SIZE-1,GRID_SIZE-1));

        List<Tile> tiles = tileGrid.getTiles();
        assertEquals(tiles.get(0), r1);
        assertEquals(tiles.get(1), r2);
        assertEquals(tiles.get(2), r3);
    }
}