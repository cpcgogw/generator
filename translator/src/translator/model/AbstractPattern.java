package translator.model;

import model.AreaNode;
import model.Node;
import model.Pattern;

import java.util.ArrayList;

/**
 * Created by vilddjur on 3/11/17.
 */
public class AbstractPattern implements Pattern {
    public ArrayList<AreaNode> nodes = new ArrayList<>();

    @Override
    public void removeNode(Node node) {
        nodes.remove(node);
    }

    @Override
    public Node remove(int i) {
        return nodes.remove(i);
    }

    @Override
    public void addNode(AreaNode node) {
        nodes.add(node);
    }

    @Override
    public ArrayList<AreaNode> getNodes() {
        return nodes;
    }
}
