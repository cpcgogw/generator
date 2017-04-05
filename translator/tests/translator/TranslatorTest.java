package translator;

import model.*;
import org.junit.Test;
import translator.model.*;
import utils.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * Created by vilddjur on 3/21/17.
 */
public class TranslatorTest {
    private final int GRAPH_SIZE = 15;
    private final Random rand = new Random();
    @Test
    public void outPutRandomHighResGraph() throws Exception{
        AbstractPattern pattern = getRandomPattern(GRAPH_SIZE);
        int size = (int)(Math.sqrt(pattern.getNodes().size())+1);
        NodeGrid grid = new NodeGrid(size);
        Translator.placeGraphOnGrid(pattern, grid);
        System.out.println(grid);
        TileGrid lowRes = Translator.translateToLowRes(grid);
        System.out.println(lowRes);
        PopulatedTileGrid popGrid = Translator.translateToPopulatedGrid(lowRes);
        System.out.println(popGrid);
        ExportLevelHandler.saveGridAsTMX(popGrid, "../saves/levels/test.xml");

    }
    @Test
    public void placeGraphOnGridDrawablePattern() throws Exception{
        DrawablePattern drawablePattern = getRandomDrawablePattern();
        int sizeBefore = drawablePattern.getNodes().size();
        int size = (int) Math.sqrt(GRAPH_SIZE);
        NodeGrid grid = new NodeGrid(size);
        Translator.placeGraphOnGrid(drawablePattern, grid);
        int sizeAfter = drawablePattern.getNodes().size();
        assert sizeAfter == sizeBefore;
//        assert abstractPattern.getNodes() == grid.getNodes();
        for (Node n : drawablePattern.getNodes()) {
            String s = n.getNodeId() + ": ";
            for (Edge edge : n.getEdges()) {
                s += ", " + edge.getFrom().getNodeId() + "->" + edge.getTo().getNodeId();
            }
            System.out.println(s);
        }

        Class[] argTypes = new Class[] {int.class, int.class, Tile.class};
        Method isNeighbour = grid.getClass().getSuperclass().getDeclaredMethod("isNeighbour", argTypes);
        isNeighbour.setAccessible(true);
        Field placedField = grid.getClass().getSuperclass().getDeclaredField("placed");
        placedField.setAccessible(true);
        List<Tile> placedList = (List<Tile>) placedField.get(grid);

        for (int x = 0; x < grid.size(); x++) {
            for (int y = 0; y < grid.size(); y++) {
                Tile t = grid.getTile(x,y);
                if(t != null){
                    if(t instanceof Node){
                        Node node = (Node) t;
                        // Check neighbours
                        for (Edge e : node.getEdges()) {

                            // Is already placed so we must be next to it.
                            if (placedList.contains(e.getTo())) {
                                Object[] args = new Object[] {x,y,(Tile) e.getTo()};
                                //assert !(Boolean)isNeighbour.invoke(grid, args);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(grid);

    }

    @Test
    public void testSqrtSize() throws Exception {
        int size;
        int sqrt;
        long time;
        long delta;
        long largestDelta = 0;
        Pattern pattern;
        Pattern largestPattern = null;
        NodeGrid grid;
        NodeGrid largestGrid = null;

        for (int i = 0; i < 100; i++) {
            time = System.currentTimeMillis();
            size = rand.nextInt(5)+10;
            sqrt = (int) (Math.sqrt(size)+1);
            pattern = getRandomPattern(size);
            grid = new NodeGrid(sqrt);
            assertTrue(Translator.placeGraphOnGrid(pattern, grid));
            delta = System.currentTimeMillis() - time;
            time = System.currentTimeMillis();
            if (delta > largestDelta) {
                largestDelta = delta;
                largestGrid = grid;
                largestPattern = pattern;
            }

        }

        Log.level = Log.LEVEL.DEBUG;
        Log.print("TranslatorTest: It were possible to place all patterns on a grid with sqrt of size of number of nodes.", Log.LEVEL.DEBUG);
        Log.print("TranslatorTest: Grid: \n"+largestGrid+"Pattern: "+largestPattern, Log.LEVEL.DEBUG);
    }

    @Test
    public void placeGraphOnGridAbstractPattern() throws Exception {

        /*
        Create cool pattern using abstract models
        apply it to nodegrid
        see if it makes sense

        do same with view thingys
         */
        AbstractPattern abstractPattern = getRandomPattern(GRAPH_SIZE);

        int sizeBefore = abstractPattern.getNodes().size();
        NodeGrid grid = new NodeGrid(GRAPH_SIZE);
        Translator.placeGraphOnGrid(abstractPattern, grid);
        int sizeAfter = abstractPattern.getNodes().size();
        assert sizeAfter == sizeBefore;
//        assert abstractPattern.getNodes() == grid.getNodes();
        for (Node n : abstractPattern.getNodes()) {
            String s = n.getNodeId() + ": ";
            for (Edge edge : n.getEdges()) {
                s += ", " + edge.getFrom().getNodeId() + "->" + edge.getTo().getNodeId();
            }
            System.out.println(s);
        }

        Class[] argTypes = new Class[] {int.class, int.class, Tile.class};
        Method isNeighbour = grid.getClass().getSuperclass().getDeclaredMethod("isNeighbour", argTypes);
        isNeighbour.setAccessible(true);
        Field placedField = grid.getClass().getSuperclass().getDeclaredField("placed");
        placedField.setAccessible(true);
        List<Tile> placedList = (List<Tile>) placedField.get(grid);

        for (int x = 0; x < grid.size(); x++) {
            for (int y = 0; y < grid.size(); y++) {
                Tile t = grid.getTile(x,y);
                if(t != null){
                    if(t instanceof Node){
                        Node node = (Node) t;
                        // Check neighbours
                        for (Edge e : node.getEdges()) {

                            // Is already placed so we must be next to it.
                            if (placedList.contains(e.getTo())) {
                                Object[] args = new Object[] {x,y,(Tile) e.getTo()};
                                //assert !(Boolean)isNeighbour.invoke(grid, args);
                            }
                        }

                    }

                }
            }
        }
        System.out.println(grid);
    }

    private void addRandomEdge(AbstractNode node, AbstractPattern abstractPattern) {
        if(abstractPattern.getNodes().size()>0) {
            Node randNode = abstractPattern.getNodes().get(rand.nextInt(abstractPattern.getNodes().size()));
            if(randNode != node){
                if(rand.nextBoolean() && randNode.getEdges().size()<8 && node.getEdges().size()<8) {
                    AbstractEdge e = new AbstractEdge(randNode, node);
                    node.addEdge(e);
                    randNode.addEdge(e);
                } else{
                    AbstractEdge e = new AbstractEdge(node, randNode);
                    node.addEdge(e);
                    randNode.addEdge(e);
                }
            }
        }
    }

    public AbstractPattern getRandomPattern(int size) {
        AbstractPattern p = new AbstractPattern();
        for (int i = 0; i < size; i++) {
            AbstractNode node = new AbstractNode();
            node.setType(TILE_TYPE.TOWN);
            randomObjects(node);
            addRandomEdge(node, p);
            p.addNode(node);
        }
        return p;
    }

    private void randomObjects(AreaNode node) {
        int numberOfObjects = rand.nextInt(5)+2;
        for (int i = 0; i < numberOfObjects; i++) {
            int num = rand.nextInt(3);
            if(num == 0){
                node.addObject(new SubNode(OBJECT_TYPE.MONSTER));
            }else if(num == 1){
                node.addObject(new SubNode(OBJECT_TYPE.DANGER));
            }else if(num == 2){
                node.addObject(new SubNode(OBJECT_TYPE.TRAP));
            }
        }
    }

    /**
     * Will have overflowing '0' node
     * @return
     */
    private AbstractPattern getTestZeroOverflowPattern(){
        AbstractPattern p = new AbstractPattern();
        AbstractNode node0 = new AbstractNode();
        p.addNode(node0);
        AbstractNode node1 = new AbstractNode();
        p.addNode(node1);
        AbstractNode node2 = new AbstractNode();
        p.addNode(node2);
        AbstractNode node3 = new AbstractNode();
        p.addNode(node3);
        AbstractNode node4 = new AbstractNode();
        p.addNode(node4);
        AbstractNode node5 = new AbstractNode();
        p.addNode(node5);
        AbstractEdge e01 = new AbstractEdge(node0,node1);
        node0.addEdge(e01);
        node1.addEdge(e01);
        AbstractEdge e02 = new AbstractEdge(node0,node2);
        node0.addEdge(e02);
        node2.addEdge(e02);
        AbstractEdge e03 = new AbstractEdge(node0,node3);
        node0.addEdge(e03);
        node3.addEdge(e03);
        AbstractEdge e04 = new AbstractEdge(node0,node4);
        node0.addEdge(e04);
        node4.addEdge(e04);
        AbstractEdge e35 = new AbstractEdge(node3,node5);
        node3.addEdge(e35);
        node5.addEdge(e35);

        return p;
    }
    /**
     * Will have overflowing '1' node
     * @return
     */
    private AbstractPattern getTestOneOverflowPattern(){
        AbstractPattern p = new AbstractPattern();
        AbstractNode node0 = new AbstractNode();
        p.addNode(node0);
        AbstractNode node1 = new AbstractNode();
        p.addNode(node1);
        AbstractNode node2 = new AbstractNode();
        p.addNode(node2);
        AbstractNode node3 = new AbstractNode();
        p.addNode(node3);
        AbstractNode node4 = new AbstractNode();
        p.addNode(node4);
        AbstractNode node5 = new AbstractNode();
        p.addNode(node5);
        AbstractNode node6 = new AbstractNode();
        p.addNode(node6);
        AbstractEdge e01 = new AbstractEdge(node0,node1);
        node0.addEdge(e01);
        node1.addEdge(e01);
        AbstractEdge e12 = new AbstractEdge(node1,node2);
        node1.addEdge(e12);
        node2.addEdge(e12);
        AbstractEdge e13 = new AbstractEdge(node1,node3);
        node1.addEdge(e13);
        node3.addEdge(e13);
        AbstractEdge e14 = new AbstractEdge(node1,node4);
        node1.addEdge(e14);
        node4.addEdge(e14);
        AbstractEdge e15 = new AbstractEdge(node1,node5);
        node1.addEdge(e15);
        node5.addEdge(e15);
        AbstractEdge e16 = new AbstractEdge(node1,node6);
        node1.addEdge(e16);
        node6.addEdge(e16);

        return p;
    }

    public DrawablePattern getRandomDrawablePattern() {
        DrawablePattern pattern = new DrawablePattern();
        for (int i = 0; i < GRAPH_SIZE; i++) {
            DrawableAreaNode node = new DrawableAreaNode(0,0, AREA_TYPE.GRASSFIELD);
            addRandomDrawableEdge(node, pattern);
            pattern.addNode(node);
        }
        return pattern;
    }

    private void addRandomDrawableEdge(DrawableAreaNode node, DrawablePattern pattern) {
        if(pattern.getNodes().size()>0) {
            DrawableAreaNode randNode = (DrawableAreaNode) pattern.getNodes().get(rand.nextInt(pattern.getNodes().size()));
            if(randNode != node){
                if(rand.nextBoolean() && randNode.getEdges().size()<8 && node.getEdges().size()<8) {
                    DrawableEdge e = new DrawableEdge(randNode, node);
                } else{
                    DrawableEdge e = new DrawableEdge(node, randNode);
                }
            }
        }
    }
}