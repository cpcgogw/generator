package model.implementations;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.enums.AREA_TYPE;
import model.enums.TILE_TYPE;
import model.interfaces.AreaNode;
import model.interfaces.Edge;
import model.interfaces.Subnode;
import model.interfaces.Tile;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 1/24/17.
 */
public class DrawableAreaNode extends Circle implements AreaNode, Tile {
    //TODO: change name drawableEdges->edges
    private List<DrawableEdge> drawableEdges;
    private List<DrawableSubnode> subnodes = new ArrayList<>();
    private AREA_TYPE type;
    private int id;

    public static int idCounter = 0;
    public static final int DEFAULT_RADIUS = 40;

    @Override
    public TILE_TYPE getTILE_TYPE() {
        return TILE_TYPE.TOWN;
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

    public void addAllEdges(List<DrawableEdge> drawableEdges) {
        this.drawableEdges.addAll(drawableEdges);
    }

    public ArrayList<DrawableEdge> extractOutgoingEdges(DrawablePattern p) {
        ArrayList<DrawableEdge> outgoingDrawableEdges = new ArrayList<>();
        for (DrawableEdge e : drawableEdges) {
            if(this == e.getEndDrawableAreaNode()){ // we are end node, check if start node is in given pattern
                if(!p.drawableAreaNodes.contains(e.getStartDrawableAreaNode())){
                    outgoingDrawableEdges.add(e);
                }
            }else{ // we are start node, check if end node is in given pattern
                if(!p.drawableAreaNodes.contains(e.getEndDrawableAreaNode())){
                    outgoingDrawableEdges.add(e);
                }
            }
        }
        return outgoingDrawableEdges;
    }

    public void setDrawableEdges(ArrayList<DrawableEdge> drawableEdges) {
        this.drawableEdges = drawableEdges;
    }

    public void setType(AREA_TYPE type) {
        this.type = type;
        setColor();
    }

    public DrawableAreaNode(double x, double y, AREA_TYPE type) {
        this(x, y, idCounter++, type);
    }

    public DrawableAreaNode(double x, double y, int id, AREA_TYPE type) {
        super(x, y, DEFAULT_RADIUS, Color.YELLOW);
        this.type = type;
        this.id = id;
        drawableEdges = new ArrayList<>();
        setColor();
    }

    private void setColor() {
        switch (type){
            case GRASSFIELD:
                this.setFill(Color.FORESTGREEN);
                break;
            case TOWN:
                this.setFill(Color.GRAY);
                break;
            case DESERT:
                this.setFill(Color.DARKKHAKI);
                break;
            default:
                this.setFill(Color.BLACK);
                break;
        }
    }

    public void setPos(double x, double y) {
        super.setCenterX(x);
        super.setCenterY(y);
        updateSubnodes();
        updateEdges();
    }

    public List<DrawableEdge> getDrawableEdges() {
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
            if (e.getStartDrawableAreaNode() == this){
                if(!e.getEndDrawableAreaNode().getType().equals(type)){
                    newDrawableEdges.add(e);
                }
            }else if(e.getEndDrawableAreaNode() == this){
                if(!e.getStartDrawableAreaNode().getType().equals(type)){
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

    public AREA_TYPE getType(){
        return type;
    }

    public List<? extends Subnode> getSubnodes() {
        return subnodes;
    }

    @Override
    public void addObject(Subnode node) {
    }

    public boolean addObject(DrawableSubnode node) {
        if (subnodes.size() < 12) {
            subnodes.add(node);
            Log.print("DrawableAreaNode: Added a subnode of type: "+node.getType()
                    +" to area node: "+this.getType(), Log.LEVEL.DEBUG);
            updateSubnodes();
            return true;
        } else {
            Log.print("DrawableAreaNode: Couldn't add subnode to node! Too many subnodes already.", Log.LEVEL.DEBUG);
            return false;
        }
    }

    public void removeEdge(DrawableEdge drawableEdge) {
        drawableEdges.remove(drawableEdge);
    }

    @Override
    public int hashCode() {
        return super.hashCode()+id*5+type.hashCode()*7;
    }

    public DrawableAreaNode clone() {
        DrawableAreaNode drawableAreaNode = new DrawableAreaNode(this.getCenterX(), this.getCenterY(), this.id, this.getType());
        for (DrawableEdge e: this.drawableEdges) {
            if (e.getStartDrawableAreaNode().getNodeId() == this.id)
                e.setStartNode(drawableAreaNode);
            if (e.getEndDrawableAreaNode().getNodeId() == this.id)
                e.setEndNode(drawableAreaNode);
            drawableAreaNode.drawableEdges.add(e);
        }
        for (DrawableSubnode subnode : this.subnodes) {
            drawableAreaNode.addObject(subnode);
        }
        return drawableAreaNode;
    }

    @Override
    public String toString() {
        return "Type: " + this.getType() + ", id:" + this.getNodeId() + ", edges: " + this.getDrawableEdges() + ", subnodes: "+subnodes;
    }

    public void updateSubnodes() {
        int i = subnodes.size();
        double r = 0;

        for (DrawableSubnode subnode : subnodes) {
            Log.print("DrawableAreaNode: Updating subnode: "+subnode.getType(), Log.LEVEL.INFO);
            Log.print("DrawableAreaNode: edges: "+subnode.getDrawableEdges().size(), Log.LEVEL.INFO);
            Log.print("DrawableAreaNode: radius: "+subnode.getRadius(), Log.LEVEL.INFO);
            r = subnode.getRadius()+this.getRadius();
            subnode.setPosition(this.getCenterX()+Math.cos(Math.toRadians(30*i))*r, this.getCenterY()+Math.sin(Math.toRadians(30*i))*r);
            for (DrawableEdge edge : subnode.getDrawableEdges()) {
                edge.updateNodes();
            }
            subnode.drawEdges();
            i--;
        }
    }

    public void setSubnodes(List<DrawableSubnode> subnodes) {
        this.subnodes = subnodes;
    }

    public void setEdges(List<DrawableEdge> edges) {
        this.drawableEdges = edges;
    }

    public List<DrawableSubnode> getDrawableSubnodes() {
        return subnodes;
    }

    //TODO: The equals method is wrong in DrawableSubnode, matches nodes without having same node ID.
    public void removeSubnode(DrawableSubnode subnode) {
        if(subnodes.contains(subnode)) {
            subnodes.remove(subnode);
        }
    }

    public void removeAllEdges() {
        // Remove references to this node
        for (DrawableEdge edge : drawableEdges) {
            if (edge.getFrom() == this) {
                ((DrawableAreaNode) edge.getTo()).removeEdge(edge);
            } else {
                ((DrawableAreaNode) edge.getFrom()).removeEdge(edge);
            }
        }
        drawableEdges.clear();
    }
}
