package graph_translator.model;

import javafx.util.Pair;
import model.Node;

import java.util.ArrayList;

/**
 * Created by vilddjur on 3/6/17.
 */
public class NodeGrid {
    private Node[][] nodes;
    private int size;
    public NodeGrid(int n){
        nodes = new Node[n][n];
        size = n;
    }

    public Node getNode(int x,int y){
        return nodes[x][y];
    }

    public boolean insertNode(int x, int y, Node node){
        if(x>=nodes.length){
            return false;
        }else{
            if (nodes[x].length>=y){
                return false;
            }else{
                nodes[x][y] = node;
                return true;
            }
        }
    }

    public ArrayList<Pair<Node, Pair<Integer,Integer>>> getNodesIterable(){
        ArrayList<Pair<Node, Pair<Integer,Integer>>> retList = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Pair p = new Pair(nodes[x][y], new Pair(x,y));
                retList.add(p);
            }
        }
        return retList;
    }
}
