package translator.model;

import model.EDGE_TYPE;
import model.Edge;
import model.Node;

/**
 * Created by vilddjur on 3/11/17.
 */
public class AbstractEdge implements Edge {
    Node from;
    Node to;
    private EDGE_TYPE type;

    public AbstractEdge(Node from, Node to){
        this.from = from;
        this.to = to;
        this.type = EDGE_TYPE.NORMAL;
    }
    public AbstractEdge(Node from, Node to, EDGE_TYPE type){
        this(from, to);
        this.type = type;
    }

    @Override
    public EDGE_TYPE getType() {
        return this.type;
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
