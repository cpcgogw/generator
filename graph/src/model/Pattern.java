package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vilddjur on 1/28/17.
 */
public class Pattern {
    public ArrayList<Node> nodes;
    private Random random;
    public Pattern(){
        nodes = new ArrayList<Node>();
        random = new Random();
    }
    public Pattern(Pair<ArrayList<Node>,ArrayList<Edge>> pair){
        nodes = new ArrayList<>();
        nodes.addAll(pair.getKey());
        random = new Random();
    }
    @Override
    public int hashCode() {
        return super.hashCode()+nodes.hashCode()*3;
    }

    @Override
    public boolean equals(Object o) {
            if(o instanceof Pattern){
                Pattern tmp = (Pattern) o;
                return nodes.equals(tmp.nodes); // maybe sort lists
            }else{
                return false;
            }
    }

    public void resetIds() {
        int idCounter = Node.idCounter;
        for (Node node :
                nodes) {
            node.setNodeId(idCounter);
            idCounter++;
        }
    }
}
