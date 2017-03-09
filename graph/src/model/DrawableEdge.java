package model;


import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Created by vilddjur on 1/24/17.
 */
public class DrawableEdge extends Line{
    private DrawableNode startDrawableNode;
    private DrawableNode endDrawableNode;
    private Path arrowHead;
    public static final double STROKE_WIDTH = 3;
    /**
     * Takes a start DrawableNode and an end DrawableNode, draws a line between the center of the two.
     * @param startDrawableNode
     * @param endDrawableNode
     */
    public DrawableEdge(DrawableNode startDrawableNode, DrawableNode endDrawableNode) {
        arrowHead = new Path();
        this.arrowHead.setStrokeWidth(STROKE_WIDTH);
        this.setStartDrawableNode(startDrawableNode);
        if(endDrawableNode != null){
            this.setEndNode(endDrawableNode);
        }
        this.setFill(new Color(0,0,0,0));
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(STROKE_WIDTH);

    }

    /**
     * Sets the startDrawableNode to the given DrawableNode
     * @param startDrawableNode
     */
    public void setStartDrawableNode(DrawableNode startDrawableNode) {
        this.setStartX(startDrawableNode.getCenterX());
        this.setStartY(startDrawableNode.getCenterY());
        this.startDrawableNode = startDrawableNode;
        startDrawableNode.addEdge(this);
    }

    public DrawableNode getStartDrawableNode() {
        return startDrawableNode;
    }

    /**
     * Sets the endDrawableNode to the given DrawableNode
     * @param endDrawableNode
     */
    public Shape setEndNode(DrawableNode endDrawableNode) {
        this.setEndX(endDrawableNode.getCenterX());
        this.setEndY(endDrawableNode.getCenterY());
        this.endDrawableNode = endDrawableNode;
        this.endDrawableNode.addEdge(this);

        return makeArrow();
    }

    public Shape makeArrow() {
        double deltaY = (startDrawableNode.getCenterY() - endDrawableNode.getCenterY());
        double deltaX = (startDrawableNode.getCenterX() - endDrawableNode.getCenterX());
        double angle = Math.atan2(deltaY,deltaX);
        double x = endDrawableNode.getCenterX() + Math.cos(angle)* DrawableNode.DEFAULT_RADIUS;
        double y = endDrawableNode.getCenterY() + Math.sin(angle)* DrawableNode.DEFAULT_RADIUS;
        arrowHead.getElements().clear();
        arrowHead.getElements().add(new MoveTo(x, y));
        arrowHead.getElements().add(new LineTo(x + Math.cos(angle+Math.toRadians(45))*(DrawableNode.DEFAULT_RADIUS/2),y + Math.sin(angle+Math.toRadians(45))*(DrawableNode.DEFAULT_RADIUS/2)));
        arrowHead.getElements().add(new LineTo(x + Math.cos(angle+Math.toRadians(-45))*(DrawableNode.DEFAULT_RADIUS/2),y + Math.sin(angle+Math.toRadians(-45))*(DrawableNode.DEFAULT_RADIUS/2)));
        arrowHead.getElements().add(new LineTo(x,y));
        return arrowHead;
    }

    public DrawableNode getEndDrawableNode() {
        return endDrawableNode;
    }

    public void updateNodes() {
        if(startDrawableNode != null){
            this.setStartX(startDrawableNode.getCenterX());
            this.setStartY(startDrawableNode.getCenterY());
        }
        if(endDrawableNode != null) {
            this.setEndX(endDrawableNode.getCenterX());
            this.setEndY(endDrawableNode.getCenterY());
        }
        makeArrow();
    }
    public Shape getArrow(){
        return arrowHead;
    }

    public void delete() {
        getStartDrawableNode().removeEdge(this);
        getEndDrawableNode().removeEdge(this);
    }

    /**
     * Given a node to replace and a node to add instead this method checks which node is the one given, if the
     * startnode is the replace node we set the startnode to be the newDrawableNode, and vice versa for endDrawableNode
     * @param replace
     * DrawableNode to replace
     * @param newDrawableNode
     * DrawableNode to replace with
     */
    public void replaceNode(DrawableNode replace, DrawableNode newDrawableNode){
        if(startDrawableNode == replace){
            startDrawableNode = newDrawableNode;
        }else if(endDrawableNode == replace){
            endDrawableNode = newDrawableNode;
        }
    }
}
