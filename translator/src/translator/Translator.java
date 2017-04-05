package translator;


import javafx.util.Pair;
import model.*;
import translator.model.*;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for translating a graph into a game world.
 *
 * Created by time on 3/8/17.
 */
public class Translator {
    public static void main(String[] args) {
        Log.level = Log.LEVEL.INFO;
        AbstractPattern graph = testGraph();
        exportLevel(graph, "saves/levels/level.xml");
    }

    public static PopulatedTileGrid translateToPopulatedGrid(TileGrid lowRes) {
        TranslationStrategy strategy = new SimpleTranslation();
        return strategy.apply(lowRes);
    }

    public static void exportLevel(Pattern graph, String path){
        int size = (int) Math.sqrt(graph.getNodes().size())+1;
        NodeGrid grid = new NodeGrid(size);
        TileGrid lowRes;
        PopulatedTileGrid highRes;

        System.out.println("Grid before: ");
        System.out.println(grid);
        placeGraphOnGrid(graph, grid);
        System.out.println("Grid after: ");
        System.out.println(grid);
        System.out.println("Translated to low res: ");
        lowRes = translateToLowRes(grid);
        highRes = translateToPopulatedGrid(lowRes);
        System.out.println(highRes);
        ExportLevelHandler.saveGridAsTMX(highRes, path);
    }
    /**
     * Tries to place the graph onto the grid.
     *
     * @param graph
     * Graph to be placed.
     * @param grid
     * Grid to place graph on.
     */
    public static boolean placeGraphOnGrid(Pattern graph, NodeGrid grid) {
        Log.print("Translator: Placing graph on grid...", Log.LEVEL.INFO);


        ArrayList<Node> tmpRest = (ArrayList<Node>) graph.getNodes().clone();

        // Place all drawableAreaNodes on graph
        if (!placeAll(tmpRest.remove(0), tmpRest, grid)) {
            Log.print("Translator: Unable to place graph on grid!", Log.LEVEL.ERROR);
            return false;
        }

        Log.print("Translator: Graph successfully placed on grid.", Log.LEVEL.INFO);
        return true;
    }

    /**
     * Tries to place a node and all drawableAreaNodes after it on the grid.
     *
     * @param node
     * DrawableAreaNode to be placed.
     * @param rest
     * All other drawableAreaNodes to be placed.
     * @param grid
     * The grid to place the drawableAreaNodes on.
     * @return
     * True if node and all other drawableAreaNodes were placed successfully, otherwise False.
     */
    public static boolean placeAll(Node node, ArrayList<Node> rest, NodeGrid grid) {
        Log.print("----", Log.LEVEL.DEBUG);
        Log.print(grid, Log.LEVEL.DEBUG);

        // Try all places.
        for (int y=0; y<grid.size(); y++) {
            for (int x=0; x<grid.size(); x++) {
                if (!grid.tryPlace((Tile) node, x, y))
                    continue;
                Log.print("trying placement, "+ x+ ", " + y+"---", Log.LEVEL.DEBUG);
                Log.print("\n"+grid.toString(), Log.LEVEL.DEBUG);
                // If not the absolute last node.
                if (rest.size() != 0) {
                    // Needed for passing references along without changing local references.
                    NodeGrid tmpGrid = grid.clone();
                    tmpGrid.addTile((Tile)node, x, y);
                    ArrayList<Node> tmpRest = (ArrayList<Node>) rest.clone();

                    // Try to place the remaining drawableAreaNodes.
                    if (placeAll(tmpRest.remove(0),tmpRest,tmpGrid)) {
                        // We were able to successfully place all drawableAreaNodes using this configuration.
                        // So copy all references so they will be saved further up the chain.
                        // The brute force is complete.
                        NodeGrid.copy(tmpGrid, grid);
                        rest = tmpRest;
                        return true;
                    }else{
                        Log.print("Unable to place on " + x + ", " + y, Log.LEVEL.DEBUG);
                        Log.print("\n" + grid.toString(), Log.LEVEL.DEBUG);
                        grid.removeTile(x,y);
                    }
                } else {
                    // Since not doing more recursions copying is not necessary.
                    grid.addTile((Tile) node, x, y);
                    return true;
                }
                Log.print("i am here" , Log.LEVEL.DEBUG);
            }
        }

        // There is no possible way to place the graph on the grid.
        return false;
    }

    /**
     * Translates a NodeGrid into a low res version of the same grid.
     *
     * @param grid
     * Grid to be translated.
     * @return
     * The low res version of the grid.
     */
    public static TileGrid translateToLowRes(NodeGrid grid) {
        TileGrid lowResGrid = new TileGrid(1+grid.size()*2);

        for (int y = 0; y < lowResGrid.size(); y++) {
            for (int x = 0; x < lowResGrid.size(); x++) {
                if (x % 2 == 1 && y % 2 == 1) {
                    lowResGrid.addTile(grid.getTile(x/2, y/2), x, y);
                }
            }
        }
        //HashMap<Node, Pair<Integer, Integer>> placed = lowResGrid.getPlacedPositions();
        List<Node> placed = grid.getNodes();
        List<Pair<Integer, Integer>> toBePlaced = new ArrayList<>();
        Log.print("Translator: Translating "+placed.size()+" nodes.", Log.LEVEL.INFO);
        for (Node n : placed) {
            if (n == null)
                continue;
            Log.print("Translator: Translating node "+n.getNodeId(), Log.LEVEL.INFO);


            List<? extends Edge> edges = n.getEdges();
            for (Edge e : edges) {
                Pair<Integer, Integer> first = lowResGrid.getNodePosition((Tile) e.getFrom());
                Pair<Integer, Integer> second = lowResGrid.getNodePosition((Tile) e.getTo());

                Pair<Integer, Integer> midPoint = midPoint(first, second);

                toBePlaced.add(midPoint);
            }
        }

        for (Pair p : toBePlaced) {
            Road r = new Road();
            lowResGrid.addTile(r, ((Integer)p.getKey()), ((Integer)p.getValue()));
        }

        return lowResGrid;
    }

    public static NodeGrid trimGrid(TileGrid grid){
        int maxPos = 0;
        for (int x = 0; x < grid.size(); x++) {
            for (int y = 0; y < grid.size(); y++) {
                if(grid.getTile(x,y)!=null){
                    if(x>maxPos){
                        maxPos = x;
                    }
                    if(y>maxPos){
                        maxPos = y;
                    }
                }
            }
        }

        NodeGrid newGrid = new NodeGrid(maxPos+1);
        TranslationStrategy.copyFromPos(grid, newGrid, 0,0);
        Log.print("Trimming grid from " + grid.size() + " to " + newGrid.size(), Log.LEVEL.INFO);
        return newGrid;
    }

    private static Pair<Integer,Integer> midPoint(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
        return new Pair<>((from.getKey()+to.getKey())/2, (from.getValue()+to.getValue())/2);
    }

    /**
     * A simple graph to test out the translation function.
     *
     * @return
     * The simple graph.
     */
    public static AbstractPattern testGraph() {
        AbstractPattern pattern = new AbstractPattern();
        ArrayList<AreaNode> graph = new ArrayList<>();

        AreaNode n1 = new AbstractNode();
        AreaNode n2 = new AbstractNode();
        AreaNode n3 = new AbstractNode();
        AreaNode n4 = new AbstractNode();

        graph.add(n1);
        graph.add(n2);
        graph.add(n3);
        graph.add(n4);
        graph.add(new AbstractNode());

        Edge e1 = new AbstractEdge(n1, n2);
        Edge e2 = new AbstractEdge(n2, n3);

        Edge e3 = new AbstractEdge(n3, n4);
        Edge e4 = new AbstractEdge(n4, n3);

        Edge e5 = new AbstractEdge(n4, n1);
        Edge e6 = new AbstractEdge(n1, n4);

        n1.addEdge(e1);
        n1.addEdge(e6);
        n2.addEdge(e2);
        n3.addEdge(e3);
        n4.addEdge(e4);
        n4.addEdge(e5);
        /*
        0->1->2
        2<->3
        0<->3
         */
        pattern.nodes = graph;
        return pattern;
    }
}
