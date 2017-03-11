package translator.model;

import model.Edge;
import model.Node;

/**
 * Created by vilddjur on 3/11/17.
 */
public class AbstractEdge implements Edge {
    Node from;
    Node to;

    public AbstractEdge(Node from, Node to){
        this.from = from;
        this.to = to;
    }
    @Override
    public Node getFrom() {
        return from;
    }

    @Override
    public Node getTo() {
        return to;
    }
}
