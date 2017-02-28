package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

/**
 * Created by vilddjur on 1/24/17.
 */
public class Node extends Circle {
    private ArrayList<Edge> edges;
    private int id;
    private static int idCounter=0;
    public static final int DEFAULT_RADIUS = 40;

    public enum NodeType{
        START, END, LOCK, KEY, ROOM
    }
    private String type;

    public Node(Node n) {
        super(n.getCenterX(), n.getCenterY(), n.getRadius(), n.getFill());
        edges = new ArrayList<>();
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

    public void addAllEdges(ArrayList<Edge> edges) {
        this.edges.addAll(edges);
    }

    public ArrayList<Edge> extractOutgoingEdges(Pattern p) {
        ArrayList<Edge> outgoingEdges = new ArrayList<>();
        for (Edge e : edges) {
            if(this == e.getEndNode()){ // we are end node, check if start node is in given pattern
                if(!p.nodes.contains(e.getStartNode())){
                    outgoingEdges.add(e);
                }
            }else{ // we are start node, check if end node is in given pattern
                if(!p.nodes.contains(e.getEndNode())){
                    outgoingEdges.add(e);
                }
            }
        }
        return outgoingEdges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public void setType(NodeType type) {
        this.type = type.toString();
        setColor();
    }
    public Node(double x, double y, int radius, Color color, String type){
        super(x, y, radius, color);
        edges = new ArrayList<Edge>();
        id = idCounter++;
        this.type = type;
        setColor();
    }
    public Node(double x, double y, int radius, Color color, NodeType type){
        super(x, y, radius, color);
        edges = new ArrayList<Edge>();
        id = idCounter++;
        this.type = type.toString();
        setColor();
    }
    public Node(int id, double x, double y, int radius, Color color, NodeType type){
        this(x, y, radius, color, type);
        this.id = id;
        if(id>=idCounter){
            idCounter = id+1;
        }
    }
    public Node(int id, double x, double y, int radius, Color color, String type){
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
        }
    }

    public void setPos(double x, double y) {
        super.setCenterX(x);
        super.setCenterY(y);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void updateEdges(){
        for (Edge e : edges) {
            e.updateNodes();
        }
    }

    public void addEdge(Edge e){
        edges.add(e);
    }

    public int getNodeId() {
        return id;
    }
    public String getType(){
        return type;
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    @Override
    public int hashCode() {
        return super.hashCode()+id*5+type.hashCode()*7;
    }

    public Node clone(){
        Node node = new Node(this.getCenterX(), this.getCenterY(), (int)this.getRadius(), Color.AQUA, this.getType());
        for (Edge e: this.edges){
            if(e.getStartNode().getNodeId() == this.id)
                e.setStartNode(node);
            if(e.getEndNode().getNodeId() == this.id)
                e.setEndNode(node);
            node.edges.add(e);
        }
        return node;
    }
}
