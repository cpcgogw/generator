package translator;

import translator.model.Edge;
import translator.model.Node;
import translator.model.NodeGrid;
import translator.model.Pattern;
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
        Log.level = Log.LEVEL.NONE;
        Pattern graph = testGraph();
        NodeGrid grid = new NodeGrid(3);

        System.out.println("Grid before: ");
        System.out.println(grid);
        placeGraphOnGrid(graph, grid);
        System.out.println("Grid after: ");
        System.out.println(grid);
    }

    /**
     * Tries to place the graph onto the grid.
     *
     * @param graph
     * Graph to be placed.
     * @param grid
     * Grid to place graph on.
     */
    private static void placeGraphOnGrid(Pattern graph, NodeGrid grid) {
        Log.print("Translator: Placing graph on grid...", Log.LEVEL.INFO);

        // Place all nodes on graph
        if (!placeAll(graph.pattern.remove(0), (ArrayList<Node>) graph.pattern, grid)) {
            Log.print("Translator: Unable to place graph on grid!", Log.LEVEL.ERROR);
            return;
        }

        Log.print("Translator: Graph successfully placed on grid.", Log.LEVEL.INFO);
    }

    /**
     * Print the grid to standard output.
     *
     * @param grid
     * The grid to be printed.
     */

    /**
     * Tries to place a node and all nodes after it on the grid.
     *
     * @param node
     * Node to be placed.
     * @param rest
     * All other nodes to be placed.
     * @param grid
     * The grid to place the nodes on.
     * @return
     * True if node and all other nodes were placed successfully, otherwise False.
     */
    public static boolean placeAll(Node node, ArrayList<Node> rest, NodeGrid grid) {
        int a = 0;
        System.out.println("----");
        System.out.println(grid);

        if (rest.size() == 2)
            a++;
        // Try all places.
        for (int x=0; x<grid.size(); x++) {
            for (int y=0; y<grid.size(); y++) {
                if (!grid.tryPlace(node, x, y))
                    continue;

                // If not the absolute last node.
                if (rest.size() != 0) {
                    // Needed for passing references along without changing local references.
                    NodeGrid tmpGrid = grid.clone();
                    tmpGrid.addNode(node, x, y);
                    ArrayList<Node> tmpRest = (ArrayList<Node>) rest.clone();

                    // Try to place the remaining nodes.
                    if (placeAll(tmpRest.remove(0),tmpRest,tmpGrid)) {
                        // We were able to successfully place all nodes using this configuration.
                        // So copy all references so they will be saved further up the chain.
                        // The brute force is complete.
                        NodeGrid.copy(tmpGrid, grid);
                        rest = tmpRest;
                        return true;
                    }
                } else {
                    // Since not doing more recursions copying is not necessary.
                    grid.addNode(node, x, y);
                    return true;
                }
            }
        }

        // There is no possible way to place the graph on the grid.
        return false;
    }

    /**
     * A simple graph to test out the translation function.
     *
     * @return
     * The simple graph.
     */
    public static Pattern testGraph() {
        Pattern pattern = new Pattern();
        List<Node> graph = new ArrayList<>();

        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();
        Node n4 = new Node();

        graph.add(n1);
        graph.add(n2);
        graph.add(n3);
        graph.add(n4);

        Edge e1 = new Edge(n1, n2);
        Edge e2 = new Edge(n2, n3);

        Edge e3 = new Edge(n3, n4);
        Edge e4 = new Edge(n4, n3);

        Edge e5 = new Edge(n4, n1);
        Edge e6 = new Edge(n1, n4);

        n1.edges.add(e1);
        n1.edges.add(e6);
        n2.edges.add(e2);
        n3.edges.add(e3);
        n4.edges.add(e4);
        n4.edges.add(e5);

        pattern.pattern = graph;
        return pattern;
    }
}
