package model.interfaces;

import java.util.ArrayList;

/**
 * Created by vilddjur on 3/11/17.
 */
public interface Pattern {

    void removeNode(Node node);
    Node remove(int i);
    void addNode(AreaNode node);
    ArrayList<AreaNode> getNodes();
}
