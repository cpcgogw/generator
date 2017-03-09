package translator.model;

/**
 * Created by time on 3/8/17.
 */
public class Edge {
    public Node from = null;
    public Node to = null;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }
}
