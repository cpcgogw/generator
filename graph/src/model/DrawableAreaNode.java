package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 1/24/17.
 */
public class DrawableAreaNode extends Circle implements AreaNode, Tile {
    private ArrayList<DrawableEdge> drawableEdges;
    private ArrayList<DrawableObjectNode> objects = new ArrayList<>();
    private int id;
    public static int idCounter=0;
    public static final int DEFAULT_RADIUS = 40;


    @Override
    public TILE_TYPE getTILE_TYPE() {
        return TILE_TYPE.TOWN;
    }

    private AREA_TYPE type;

    public DrawableAreaNode(DrawableAreaNode n) {
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

    public DrawableAreaNode(double x, double y, int radius, Color color, AREA_TYPE type){
        super(x, y, radius, color);
        drawableEdges = new ArrayList<>();
        id = idCounter++;
        this.type = type;
        setColor();
    }
    public DrawableAreaNode(int id, double x, double y, int radius, Color color, AREA_TYPE type){
        this(x, y, radius, color, type);
        this.id = id;
        if(id>=idCounter){
            idCounter = id+1;
        }
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
    }

    public ArrayList<DrawableEdge> getDrawableEdges() {
        return drawableEdges;
    }

    public void updateEdges(){
        for (DrawableEdge e : drawableEdges) {
            e.updateNodes();
        }
        if (this.getType() == AREA_TYPE.DESERT) {
            Log.print("DrawableAreaNode: Updating desert node. It has "+objects.size()+" subnodes.", Log.LEVEL.DEBUG);
        }
        updateSubnodes();
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

    @Override
    public List<ObjectNode> getObjects() {
        return null;
    }

    @Override
    public void addObject(ObjectNode node) {
    }

    public boolean addObject(DrawableObjectNode node) {
        if (objects.size() < 12) {
            objects.add(node);
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

    public DrawableAreaNode clone(){
        DrawableAreaNode drawableAreaNode = new DrawableAreaNode(this.getCenterX(), this.getCenterY(), (int)this.getRadius(), Color.AQUA, this.getType());
        for (DrawableEdge e: this.drawableEdges){
            if(e.getStartDrawableAreaNode().getNodeId() == this.id)
                e.setStartDrawableAreaNode(drawableAreaNode);
            if(e.getEndDrawableAreaNode().getNodeId() == this.id)
                e.setEndNode(drawableAreaNode);
            drawableAreaNode.drawableEdges.add(e);
        }
        return drawableAreaNode;
    }

    @Override
    public String toString() {
        return "Type: " + this.getType() + ", id:" + this.getNodeId() + ", #drawableEdges: " + this.getDrawableEdges().size();
    }

    public void updateSubnodes() {
        int i = objects.size();
        double r = 0;

        for (DrawableObjectNode node : objects) {
            Log.print("DrawableAreaNode: Updating subnode: "+node.getType(), Log.LEVEL.DEBUG);
            Log.print("DrawableAreaNode: edges: "+node.getDrawableEdges().size(), Log.LEVEL.DEBUG);
            Log.print("DrawableAreaNode: radius: "+node.getRadius(), Log.LEVEL.DEBUG);
            r = node.getRadius()+this.getRadius();
            node.setPosition(this.getCenterX()+Math.cos(Math.toRadians(30*i))*r, this.getCenterY()+Math.sin(Math.toRadians(30*i))*r);
            for (DrawableEdge edge : node.getDrawableEdges()) {
                edge.updateNodes();
            }
            i--;
        }
    }
}
