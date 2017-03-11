package model;

import javafx.util.Pair;
import utils.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vilddjur on 1/28/17.
 */
public class DrawablePattern implements Pattern {
    public ArrayList<DrawableNode> drawableNodes;
    private Random random;
    public DrawablePattern(){
        drawableNodes = new ArrayList<DrawableNode>();
        random = new Random();
    }
    public DrawablePattern(Pair<ArrayList<DrawableNode>,ArrayList<DrawableEdge>> pair){
        drawableNodes = new ArrayList<>();
        drawableNodes.addAll(pair.getKey());
        random = new Random();
    }
    @Override
    public int hashCode() {
        return super.hashCode()+ drawableNodes.hashCode()*3;
    }

    @Override
    public boolean equals(Object o) {
            if(o instanceof DrawablePattern){
                DrawablePattern tmp = (DrawablePattern) o;
                return drawableNodes.equals(tmp.drawableNodes); // maybe sort lists
            }else{
                return false;
            }
    }

    public void resetIds() {
        int idCounter = DrawableNode.idCounter;
        for (DrawableNode drawableNode :
                drawableNodes) {
            drawableNode.setNodeId(idCounter);
            idCounter++;
        }
    }

    @Override
    public String toString() {
        String ret = "DrawablePattern : \n";
        for (DrawableNode n : this.drawableNodes) {
            ret += " node; " + n + "\n";
        }
        return ret;
    }

    @Override
    public void removeNode(Node node) {
        drawableNodes.remove(node);
    }
    @Override
    public Node remove(int i) {
        return drawableNodes.remove(i);
    }

    @Override
    public void addNode(Node node) {
        String tmpPrefix = Log.prefix;
        Log.prefix = "addNode: ";
        if(node instanceof DrawableNode) {
            this.drawableNodes.add((DrawableNode) node);
        }else{
            Log.print("given node of incorrect type", Log.LEVEL.ERROR);
        }
        Log.prefix = tmpPrefix;
    }

    @Override
    public ArrayList<Node> getNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.addAll(drawableNodes);
        return nodes;
    }
}
