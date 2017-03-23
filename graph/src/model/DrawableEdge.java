package model;


import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Created by vilddjur on 1/24/17.
 */
public class DrawableEdge extends Line implements Edge{
    private DrawableAreaNode startDrawableAreaNode;
    private DrawableAreaNode endDrawableAreaNode;
    private Path arrowHead;
    public static final double STROKE_WIDTH = 3;
    /**
     * Takes a start DrawableAreaNode and an end DrawableAreaNode, draws a line between the center of the two.
     * @param startDrawableAreaNode
     * @param endDrawableAreaNode
     */
    public DrawableEdge(DrawableAreaNode startDrawableAreaNode, DrawableAreaNode endDrawableAreaNode) {
        arrowHead = new Path();
        this.arrowHead.setStrokeWidth(STROKE_WIDTH);
        this.setStartDrawableAreaNode(startDrawableAreaNode);
        if(endDrawableAreaNode != null){
            this.setEndNode(endDrawableAreaNode);
        }
        this.setFill(new Color(0,0,0,0));
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(STROKE_WIDTH);

    }

    /**
     * Sets the startDrawableAreaNode to the given DrawableAreaNode
     * @param startDrawableAreaNode
     */
    public void setStartDrawableAreaNode(DrawableAreaNode startDrawableAreaNode) {
        this.setStartX(startDrawableAreaNode.getCenterX());
        this.setStartY(startDrawableAreaNode.getCenterY());
        this.startDrawableAreaNode = startDrawableAreaNode;
        startDrawableAreaNode.addEdge(this);
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
        this.endDrawableAreaNode.addEdge(this);

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
        return arrowHead;
    }

    public DrawableAreaNode getEndDrawableAreaNode() {
        return endDrawableAreaNode;
    }

    public void updateNodes() {
        if(startDrawableAreaNode != null){
            this.setStartX(startDrawableAreaNode.getCenterX());
            this.setStartY(startDrawableAreaNode.getCenterY());
        }
        if(endDrawableAreaNode != null) {
            this.setEndX(endDrawableAreaNode.getCenterX());
            this.setEndY(endDrawableAreaNode.getCenterY());
        }
        makeArrow();
    }
    public Shape getArrow(){
        return arrowHead;
    }

    public void delete() {
        getStartDrawableAreaNode().removeEdge(this);
        getEndDrawableAreaNode().removeEdge(this);
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
        if(startDrawableAreaNode == replace){
            startDrawableAreaNode = newDrawableAreaNode;
        }else if(endDrawableAreaNode == replace){
            endDrawableAreaNode = newDrawableAreaNode;
        }
    }

    @Override
    public Node getFrom() {
        return startDrawableAreaNode;
    }

    @Override
    public Node getTo() {
        return endDrawableAreaNode;
    }
}
