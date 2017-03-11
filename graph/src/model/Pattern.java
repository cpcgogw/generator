package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vilddjur on 1/28/17.
 */
public class Pattern {
    public ArrayList<DrawableNode> drawableNodes;
    private Random random;
    public Pattern(){
        drawableNodes = new ArrayList<DrawableNode>();
        random = new Random();
    }
    public Pattern(Pair<ArrayList<DrawableNode>,ArrayList<DrawableEdge>> pair){
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
            if(o instanceof Pattern){
                Pattern tmp = (Pattern) o;
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
        String ret = "Pattern : \n";
        for (DrawableNode n : this.drawableNodes) {
            ret += " node; " + n + "\n";
        }
        return ret;
    }
}
