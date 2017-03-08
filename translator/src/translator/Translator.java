package translator;

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
        Node [][] grid = new Node[3][3];

        System.out.println("Grid before: ");
        initGrid(grid);
        printGrid(grid);
        placeGraphOnGrid(graph, grid);
        System.out.println("Grid after: ");
        printGrid(grid);
    }

    /**
     * Initializes an empty graph grid.
     *
     * @param grid
     * Grid to be initialized.
     */
    private static void initGrid(Node[][] grid) {
        for (int i=0; i<grid.length; i++) {
            for (int j=0; j<grid[0].length; j++) {
                grid[i][j] = null;
            }
        }
    }

    /**
     * Tries to place the graph onto the grid.
     *
     * @param graph
     * Graph to be placed.
     * @param grid
     * Grid to place graph on.
     */
    private static void placeGraphOnGrid(Pattern graph, Node[][] grid) {
        Log.print("Translator: Placing graph on grid...", Log.LEVEL.INFO);

        // Place all nodes on graph
        if (!placeAll(graph.pattern.remove(0), (ArrayList<Node>) graph.pattern, grid, new ArrayList<Node>())) {
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
    private static void printGrid(Node[][] grid) {
        Log.print("Translator: Printing grid...", Log.LEVEL.INFO);

        for (int i=0; i<grid.length; i++) {
            for (int j=0; j<grid[0].length; j++) {
                if (grid[i][j] != null) {
                    if (j==0)
                        System.out.print(grid[i][j].getId());
                    else
                        System.out.print(" "+grid[i][j].getId());
                } else {
                    if (j==0)
                        System.out.print("*");
                    else
                        System.out.print(" *");
                }
            }
            System.out.println();
        }
    }

    /**
     * Place a single node on the graph.
     *
     * @param n
     * Node to be placed.
     * @param grid
     * Grid to place node on.
     * @return
     * True if node was placed successfully, otherwise False.
     */
    private static boolean place(Node n, Node[][] grid) {
        Log.print("Translator: Placing node on grid...", Log.LEVEL.INFO);

        // Go through all possible locations on grid and try to place node
        for (int i=0; i<grid.length; i++) {
            for (int j=0; j<grid[0].length; j++) {
                if (tryPlace(n, i, j, grid)) {
                    Log.print("Translator: Successfully placed node.", Log.LEVEL.INFO);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tries to place a node and all nodes after it on the grid.
     *
     * @param node
     * Node to be placed.
     * @param rest
     * All other nodes to be placed.
     * @param grid
     * The grid to place the nodes on.
     * @param placed
     * All currently placed nodes.
     * @return
     * True if node and all other nodes were placed successfully, otherwise False.
     */
    public static boolean placeAll(Node node, ArrayList<Node> rest, Node[][] grid, ArrayList<Node> placed) {
        // Try all places.
        for (int i=0; i<grid.length; i++) {
            for (int j=0; j<grid[0].length; j++) {

                // If space is not free, skip.
                if (grid[i][j] != null)
                    continue;

                // Check to see no already placed neighbours are out of reach.
                if (!checkNeighbours(node, placed, j, i, grid))
                    continue;

                // If not the absolute last node.
                if (rest.size() != 0) {
                    // Needed for passing references along without changing local references.
                    Node[][] grid2 = clone(grid);
                    grid2[i][j] = node;
                    ArrayList<Node> placed2 = (ArrayList<Node>) placed.clone();
                    placed2.add(node);
                    ArrayList<Node> rest2 = (ArrayList<Node>) rest.clone();

                    // Try to place the remaining nodes.
                    if (placeAll(rest2.remove(0),rest2,grid2,placed2)) {
                        // We were able to successfully place all nodes using this configuration.
                        // So copy all references so they will be saved further up the chain.
                        // The brute force is complete.
                        copy(grid, grid2);
                        placed = placed2;
                        rest = rest2;
                        return true;
                    }
                } else {
                    // Since not doing more recursions copying is not necessary.
                    grid[i][j] = node;
                    placed.add(node);
                    return true;
                }
            }
        }

        // There is no possible way to place the graph on the grid.
        return false;
    }

    /**
     * Utility function for copying the values of a 2D array onto another 2D array.
     *
     * @param to
     * 2D array to copy to.
     * @param from
     * 2D array to copy from.
     */
    private static void copy(Object[][] to, Object[][] from) {
        for (int i=0; i<to.length; i++) {
            for (int j=0; j<to[0].length; j++) {
                to[i][j] = from[i][j];
            }
        }
    }

    /**
     * Checks to see if node is in range of any already placed neighbours at position (x,y) on the grid.
     *
     * @param node
     * Node to check.
     * @param placed
     * All nodes that has been placed already.
     * @param x
     * X coordinate of position to check.
     * @param y
     * Y coordinate of position to check.
     * @param grid
     * Grid to check neighbours.
     * @return
     * True if placing node at position (x,y) doesn't violate being neighbour with connected nodes, otherwise False.
     */
    private static boolean checkNeighbours(Node node, ArrayList<Node> placed, int x, int y, Node[][] grid) {
        // Check neighbours
        for (Edge e : node.edges) {

            // Is already placed so we must be next to it
            if (placed.contains(e.to)) {
                if (!isNeighbour(x, y, e.to, grid)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Utility function for cloning a grid.
     *
     * @param grid
     * The grid to be cloned.
     * @return
     * The cloned grid.
     */
    private static Node[][] clone(Node[][] grid) {
        Node[][] grid2 = new Node[grid.length][grid[0].length];

        for (int i=0; i<grid.length; i++) {
            grid2[i] = grid[i].clone();
        }

        return grid2;
    }

    /**
     * Checks to see whether the given node is a neighbour to position (x,y) on grid.
     *
     * @param x
     * X coordinate of origin.
     * @param y
     * Y coordinate of origin.
     * @param node
     * Node to be searched for.
     * @param grid
     * Grid to search on.
     * @return
     * True if node is neighbour to position (x,y), otherwise False.
     */
    private static boolean isNeighbour(int x, int y, Node node, Node[][] grid) {
        // Iterate through all neighbours
        for (int i=y-1; i<=y+1; i++) {
            for (int j = x-1; j<=x+1; j++) {
                // Only check inside the grid
                if (i >= 0 && i < grid.length && j >= 0 && j < grid[0].length) {
                    if (grid[i][j] == node) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Try placing node at position (x,y) on grid.
     *
     * @param node
     * Node to be placed.
     * @param x
     * X coordinate of position to place node on.
     * @param y
     * Y coordinate of position to place node on.
     * @param grid
     * Grid to place node on.
     * @return
     * True if node was placed successfully, otherwise False.
     */
    private static boolean tryPlace(Node node, int x, int y, Node[][] grid) {
        Log.print("Translator: Trying to place node...", Log.LEVEL.INFO);
        Log.print("Translator: Trying to place node on position (x:"+x+",y:"+y+")", Log.LEVEL.DEBUG);

        if (grid[y][x] != null) {
            return false;
        }

        int free = 0;
        int neighbours = 0;

        Log.print("Translator: Checking neighbours...", Log.LEVEL.INFO);
        // Check if neighbours are OK
        for (int i=y-1; i<=y+1; i++) {
            for (int j=x-1; j<=x+1; j++) {
                // Only check inside the grid
                if (i >= 0 && i < grid.length && j >= 0 && j < grid[0].length) {
                    // Not a neighbour (node itself)
                    if (i==y && j==x)
                        continue;

                    if (grid[i][j] == null) {
                        free++;
                    } else { // There is some random node here, check that
                        for (Edge e : node.edges) {
                            if (grid[i][j] == e.to) {
                                neighbours++;
                            }
                        }
                    }
                }
            }
        }
        if (free>=node.edges.size()-neighbours) {
            Log.print("Translator: Enough free nodes/nodes who are neighbours. Free: "+(free-(node.edges.size()-neighbours)), Log.LEVEL.INFO);
            grid[y][x] = node;
            return true;
        }
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
