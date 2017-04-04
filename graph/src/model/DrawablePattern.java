package model;

import javafx.util.Pair;
import utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by vilddjur on 1/28/17.
 */
public class DrawablePattern implements Pattern {
    //TODO: Refactor so DrawablePattern only uses drawableAreaNodes?
    public List<DrawableAreaNode> drawableAreaNodes;
    public List<DrawableSubnode> drawableSubnodes;

    private Random random;
    public DrawablePattern(){
        drawableAreaNodes = new ArrayList<DrawableAreaNode>();
        drawableSubnodes = new ArrayList<DrawableSubnode>();
        random = new Random();
    }
    public DrawablePattern(Pair<ArrayList<DrawableAreaNode>,ArrayList<DrawableEdge>> pair){
        drawableAreaNodes = new ArrayList<>();
        drawableAreaNodes.addAll(pair.getKey());
        random = new Random();
    }
    @Override
    public int hashCode() {
        return super.hashCode()+ drawableAreaNodes.hashCode()*3;
    }

    @Override
    public boolean equals(Object o) {
            if(o instanceof DrawablePattern){
                DrawablePattern tmp = (DrawablePattern) o;
                return drawableAreaNodes.equals(tmp.drawableAreaNodes); // maybe sort lists
            }else{
                return false;
            }
    }

    public void resetIds() {
        int idCounter = DrawableAreaNode.idCounter;
        for (DrawableAreaNode drawableAreaNode :
                drawableAreaNodes) {
            drawableAreaNode.setNodeId(idCounter);
            idCounter++;
        }
    }

    @Override
    public String toString() {
        String ret = "DrawablePattern : \n";
        for (DrawableAreaNode n : this.drawableAreaNodes) {
            ret += " node; " + n + "\n";
        }
        return ret;
    }

    @Override
    public void removeNode(Node node) {
        drawableAreaNodes.remove(node);
    }
    @Override
    public Node remove(int i) {
        return drawableAreaNodes.remove(i);
    }

    @Override
    public void addNode(AreaNode node) {
        if (node instanceof DrawableAreaNode) {
            this.drawableAreaNodes.add((DrawableAreaNode) node);
        } else {
            Log.print("given node of incorrect type", Log.LEVEL.ERROR);
        }
    }

    @Override
    public ArrayList<AreaNode> getNodes() {
        ArrayList<AreaNode> nodes = new ArrayList<>();
        nodes.addAll(drawableAreaNodes);
        return nodes;
    }
}
