package model.implementations;


import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import model.enums.EDGE_TYPE;
import model.interfaces.Edge;
import model.interfaces.Node;

/**
 * Created by vilddjur on 1/24/17.
 */
public class DrawableEdge extends Line implements Edge {
    public static final double STROKE_WIDTH = 3;

    private DrawableAreaNode startDrawableAreaNode;
    private DrawableAreaNode endDrawableAreaNode;
    private DrawableSubnode start;
    private DrawableSubnode end;

    private Path arrowHead;
    private Node startObject;
    private EDGE_TYPE type;

    /**
     * Takes a start DrawableAreaNode and an end DrawableAreaNode, draws a line between the center of the two.
     * @param startDrawableAreaNode
     * @param endDrawableAreaNode
     */
    public DrawableEdge(DrawableAreaNode startDrawableAreaNode, DrawableAreaNode endDrawableAreaNode) {
        arrowHead = new Path();
        this.type = EDGE_TYPE.NORMAL;
        this.arrowHead.setStrokeWidth(STROKE_WIDTH);
        this.setStartDrawableAreaNode(startDrawableAreaNode);

        if (endDrawableAreaNode != null) {
            this.setEndDrawableAreaNode(endDrawableAreaNode);
        }

        this.setFill(new Color(0,0,0,0));
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    public String toString() {
        return "("+getFrom().getNodeId()+", "+getTo().getNodeId()+")";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DrawableEdge) {
            DrawableEdge edge = (DrawableEdge) o;
            return this.getFrom() == edge.getFrom() && this.getTo() == edge.getTo();
        }
        return false;
    }

    public DrawableEdge(DrawableAreaNode startDrawableAreaNode, DrawableAreaNode endDrawableAreaNode, EDGE_TYPE type) {
        this(startDrawableAreaNode, endDrawableAreaNode);
        this.type = type;
    }

    public DrawableEdge(DrawableSubnode from, DrawableSubnode to) {
        start = from;
        end = to;

        from.getDrawableEdges().add(this);
        if (to != null) {
            to.getDrawableEdges().add(this);
        }

        this.setStartX(from.getCenterX());
        this.setStartY(from.getCenterY());
        this.setFill(Color.BLACK);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(3);
    }

    /**
     * Sets the startDrawableAreaNode to the given DrawableAreaNode
     * @param startDrawableAreaNode
     */
    public void setStartDrawableAreaNode(DrawableAreaNode startDrawableAreaNode) {
        this.setStartNode(startDrawableAreaNode);
        startDrawableAreaNode.addEdge(this);
    }

    public void setStartNode(DrawableAreaNode startDrawableAreaNode) {
        this.setStartX(startDrawableAreaNode.getCenterX());
        this.setStartY(startDrawableAreaNode.getCenterY());
        this.startDrawableAreaNode = startDrawableAreaNode;
    }

    public Shape setEndDrawableAreaNode(DrawableAreaNode endDrawableAreaNode) {
        this.setEndNode(endDrawableAreaNode);
        this.endDrawableAreaNode.addEdge(this);

        return makeArrow();
    }

    public DrawableAreaNode getStartDrawableAreaNode() {
        return startDrawableAreaNode;
    }

    /**
     * Sets the endDrawableAreaNode to the given DrawableAreaNode
     * @param endDrawableAreaNode
     */
    public Shape setEndNode(DrawableAreaNode endDrawableAreaNode) {
        this.setEndX(endDrawableAreaNode.getCenterX());
        this.setEndY(endDrawableAreaNode.getCenterY());
        this.endDrawableAreaNode = endDrawableAreaNode;

        return makeArrow();
    }

    public Shape makeArrow() {
        double deltaY = (startDrawableAreaNode.getCenterY() - endDrawableAreaNode.getCenterY());
        double deltaX = (startDrawableAreaNode.getCenterX() - endDrawableAreaNode.getCenterX());
        double angle = Math.atan2(deltaY,deltaX);
        double x = endDrawableAreaNode.getCenterX() + Math.cos(angle)* DrawableAreaNode.DEFAULT_RADIUS;
        double y = endDrawableAreaNode.getCenterY() + Math.sin(angle)* DrawableAreaNode.DEFAULT_RADIUS;
        arrowHead.getElements().clear();
        arrowHead.getElements().add(new MoveTo(x, y));
        arrowHead.getElements().add(new LineTo(x + Math.cos(angle+Math.toRadians(45))*(DrawableAreaNode.DEFAULT_RADIUS/2),y + Math.sin(angle+Math.toRadians(45))*(DrawableAreaNode.DEFAULT_RADIUS/2)));
        arrowHead.getElements().add(new LineTo(x + Math.cos(angle+Math.toRadians(-45))*(DrawableAreaNode.DEFAULT_RADIUS/2),y + Math.sin(angle+Math.toRadians(-45))*(DrawableAreaNode.DEFAULT_RADIUS/2)));
        arrowHead.getElements().add(new LineTo(x,y));
        if(type == EDGE_TYPE.LOCKED){
            deltaX = getMiddleX();
            deltaY = getMiddleY();
            arrowHead.getElements().add(new MoveTo(deltaX, deltaY));
            arrowHead.getElements().add(new LineTo(deltaX, deltaY+20));
            arrowHead.getElements().add(new LineTo(deltaX, deltaY-20));
            arrowHead.getElements().add(new MoveTo(deltaX, deltaY));
            arrowHead.getElements().add(new LineTo(deltaX-20, deltaY));
            arrowHead.getElements().add(new LineTo(deltaX+20, deltaY));
        }

        return arrowHead;
    }

    public double getMiddleX() {
        if (startDrawableAreaNode == null) {
            return (start.getCenterX() + end.getCenterX()) / 2;
        } else {
            return (startDrawableAreaNode.getCenterX() + endDrawableAreaNode.getCenterX()) / 2;
        }
    }
    public double getMiddleY(){
        if (startDrawableAreaNode == null) {
            return (start.getCenterY() + end.getCenterY()) / 2;
        } else {
            return (startDrawableAreaNode.getCenterY() + endDrawableAreaNode.getCenterY()) / 2;
        }
    }

    public DrawableAreaNode getEndDrawableAreaNode() {
        return endDrawableAreaNode;
    }

    public void updateNodes() {
        if (startDrawableAreaNode != null){
            this.setStartX(startDrawableAreaNode.getCenterX());
            this.setStartY(startDrawableAreaNode.getCenterY());
        }
        if (endDrawableAreaNode != null) {
            this.setEndX(endDrawableAreaNode.getCenterX());
            this.setEndY(endDrawableAreaNode.getCenterY());
        }
        if (start != null) {
            this.setStartX(start.getCenterX());
            this.setStartY(start.getCenterY());
        }
        if (end != null) {
            this.setEndX(end.getCenterX());
            this.setEndY(end.getCenterY());
        }
        if (startDrawableAreaNode != null && endDrawableAreaNode != null) {
            makeArrow();
        }
    }

    public Shape getArrow(){
        return arrowHead;
    }

    public void delete() {
        if (startDrawableAreaNode == null) {
            start.removeEdge(this);
            end.removeEdge(this);
        } else {
            getStartDrawableAreaNode().removeEdge(this);
            getEndDrawableAreaNode().removeEdge(this);
        }
    }

    public DrawableSubnode getStart() {
        return start;
    }

    public DrawableSubnode getEnd() {
        return end;
    }

    /**
     * Given a node to replace and a node to add instead this method checks which node is the one given, if the
     * startnode is the replace node we set the startnode to be the newDrawableAreaNode, and vice versa for endDrawableAreaNode
     * @param replace
     * DrawableAreaNode to replace
     * @param newDrawableAreaNode
     * DrawableAreaNode to replace with
     */
    public void replaceNode(DrawableAreaNode replace, DrawableAreaNode newDrawableAreaNode){
        if (startDrawableAreaNode == replace) {
            startDrawableAreaNode = newDrawableAreaNode;
        } else if(endDrawableAreaNode == replace) {
            endDrawableAreaNode = newDrawableAreaNode;
        }
    }

    @Override
    public EDGE_TYPE getType() {
        return this.type;
    }

    @Override
    public Node getFrom() {
        if (startDrawableAreaNode == null)
            return start;
        return startDrawableAreaNode;
    }

    @Override
    public Node getTo() {
        if (endDrawableAreaNode == null)
            return end;
        return endDrawableAreaNode;
    }

    //TODO: This method should not return anything at all wtf.
    public Shape setEndNode(DrawableSubnode subnode) {
        this.end = subnode;
        this.end.addEdge(this);
        this.updateNodes();
        return null; //makeArrow();
    }
}
