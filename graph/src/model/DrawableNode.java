package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import utils.Log;

import java.util.ArrayList;

/**
 * Created by vilddjur on 1/24/17.
 */
public class DrawableNode extends Circle implements Node, Tile {
    private ArrayList<DrawableEdge> drawableEdges;
    private int id;
    public static int idCounter=0;
    public static final int DEFAULT_RADIUS = 40;

    @Override
    public TILE_TYPE getTILE_TYPE() {
        return TILE_TYPE.TOWN;
    }

    @Override
    public String getRepresentation() {
        return ""+id;
    }

    public enum NodeType{
        START, END, LOCK, KEY, ROOM, ANY
    }
    private String type;

    public DrawableNode(DrawableNode n) {
        super(n.getCenterX(), n.getCenterY(), n.getRadius(), n.getFill());
        drawableEdges = new ArrayList<>();
        id = n.id;
        this.type = n.getType();
        setColor();
    }

    /**
     * Sets the node id to the given integer, sets the idCounter to that id if the idCounter is less than or equal
     * to the given id
     * @param nodeId
     * Id to set this.id to
     */
    public void setNodeId(int nodeId) {
        this.id = nodeId;
        if(idCounter<=nodeId){
            idCounter = nodeId+1;
        }
    }

    public void addAllEdges(ArrayList<DrawableEdge> drawableEdges) {
        this.drawableEdges.addAll(drawableEdges);
    }

    public ArrayList<DrawableEdge> extractOutgoingEdges(DrawablePattern p) {
        ArrayList<DrawableEdge> outgoingDrawableEdges = new ArrayList<>();
        for (DrawableEdge e : drawableEdges) {
            if(this == e.getEndDrawableNode()){ // we are end node, check if start node is in given pattern
                if(!p.drawableNodes.contains(e.getStartDrawableNode())){
                    outgoingDrawableEdges.add(e);
                }
            }else{ // we are start node, check if end node is in given pattern
                if(!p.drawableNodes.contains(e.getEndDrawableNode())){
                    outgoingDrawableEdges.add(e);
                }
            }
        }
        return outgoingDrawableEdges;
    }

    public void setDrawableEdges(ArrayList<DrawableEdge> drawableEdges) {
        this.drawableEdges = drawableEdges;
    }

    public void setType(NodeType type) {
        this.type = type.toString();
        setColor();
    }
    public void setType(String  type) {
        this.type = type;
        setColor();
    }

    public DrawableNode(double x, double y, int radius, Color color, String type){
        super(x, y, radius, color);
        drawableEdges = new ArrayList<DrawableEdge>();
        id = idCounter++;
        this.type = type;
        setColor();
    }
    public DrawableNode(double x, double y, int radius, Color color, NodeType type){
        super(x, y, radius, color);
        drawableEdges = new ArrayList<DrawableEdge>();
        id = idCounter++;
        this.type = type.toString();
        setColor();
    }
    public DrawableNode(int id, double x, double y, int radius, Color color, NodeType type){
        this(x, y, radius, color, type);
        this.id = id;
        if(id>=idCounter){
            idCounter = id+1;
        }
    }
    public DrawableNode(int id, double x, double y, int radius, Color color, String type){
        this(x, y, radius, color, type);
        this.id = id;
        if(id>=idCounter){
            idCounter = id+1;
        }
    }

    private void setColor() {
        switch (type){
            case "END":
                this.setFill(Color.FORESTGREEN);
                break;
            case "KEY":
                this.setFill(Color.ORANGE);
                break;
            case "LOCK":
                this.setFill(Color.RED);
                break;
            case "ROOM":
                this.setFill(Color.PINK);
                break;
            case "START":
                this.setFill(Color.DEEPSKYBLUE);
                break;
            case "ANY":
                this.setFill(Color.DARKKHAKI);
                break;
        }
    }

    public void setPos(double x, double y) {
        super.setCenterX(x);
        super.setCenterY(y);
    }

    public ArrayList<DrawableEdge> getDrawableEdges() {
        return drawableEdges;
    }

    public void updateEdges(){
        for (DrawableEdge e : drawableEdges) {
            e.updateNodes();
        }
    }

    public void addEdge(DrawableEdge e){
        drawableEdges.add(e);
    }

    public void removeEdgesToNodesWithType(String type) {
        ArrayList<DrawableEdge> newDrawableEdges = new ArrayList<>();
        for (DrawableEdge e :
                drawableEdges) {
            if (e.getStartDrawableNode() == this){
                if(!e.getEndDrawableNode().getType().equals(type)){
                    newDrawableEdges.add(e);
                }
            }else if(e.getEndDrawableNode() == this){
                if(!e.getStartDrawableNode().getType().equals(type)){
                    newDrawableEdges.add(e);
                }
            }
        }
        this.setDrawableEdges(newDrawableEdges);
    }

    public int getNodeId() {
        return id;
    }

    @Override
    public void addEdge(Edge e) {
        String tmpPrefix = Log.prefix;
        Log.prefix = "addEdge: ";
        if(e instanceof DrawableEdge) {
            this.drawableEdges.add((DrawableEdge) e);
        }else{
            Log.print("given edge of incorrect type", Log.LEVEL.ERROR);
        }
        Log.prefix = tmpPrefix;
    }

    @Override
    public ArrayList<Edge> getEdges() {
        ArrayList<Edge> edges = new ArrayList<>();
        edges.addAll(drawableEdges);
        return edges;
    }

    public String getType(){
        return type;
    }

    public void removeEdge(DrawableEdge drawableEdge) {
        drawableEdges.remove(drawableEdge);
    }

    @Override
    public int hashCode() {
        return super.hashCode()+id*5+type.hashCode()*7;
    }

    public DrawableNode clone(){
        DrawableNode drawableNode = new DrawableNode(this.getCenterX(), this.getCenterY(), (int)this.getRadius(), Color.AQUA, this.getType());
        for (DrawableEdge e: this.drawableEdges){
            if(e.getStartDrawableNode().getNodeId() == this.id)
                e.setStartDrawableNode(drawableNode);
            if(e.getEndDrawableNode().getNodeId() == this.id)
                e.setEndNode(drawableNode);
            drawableNode.drawableEdges.add(e);
        }
        return drawableNode;
    }

    @Override
    public String toString() {
        return "Type: " + this.getType() + ", id:" + this.getNodeId() + ", #drawableEdges: " + this.getDrawableEdges().size();
    }
}
