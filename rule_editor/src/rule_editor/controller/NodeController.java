package rule_editor.controller;


import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.DrawableEdge;
import model.DrawableNode;

import java.util.ArrayList;

import static rule_editor.controller.Controller.tools.*;

/**
 * Created by vilddjur on 1/24/17.
 */
public class NodeController {

    private DrawableEdge currentDrawableEdge;
    private EdgeController edgeController;
    private Controller controller;
    private boolean dragging;
    private ArrayList<DrawableNode> drawableNodes;

    /**
     *
     */
    public NodeController(Controller controller){
        currentDrawableEdge = null;
        dragging = false;
        this.controller = controller;
        this.edgeController = new EdgeController();
        this.drawableNodes = new ArrayList<DrawableNode>();
    }

    /**
     * Removes all Nodes and Edges
     */
    public void clear() {
        drawableNodes.clear();
    }

    private void handlePressNode(MouseEvent event, DrawableNode c) {
        if(Controller.activeTool == DELETE){
            Controller.getActiveCanvas().getChildren().remove(c);
            Controller.getActiveCanvas().getChildren().removeAll(c.getDrawableEdges());
            for (DrawableEdge e: c.getDrawableEdges()) {
                Controller.getActiveCanvas().getChildren().removeAll(e.getArrow());
            }
        }else if(Controller.activeTool == EDGE){
            if(currentDrawableEdge == null){
                currentDrawableEdge = edgeController.addEdge(c, null);
            }else{
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge.setEndNode(c));
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge);
                currentDrawableEdge = null;
            }
        }else if(Controller.activeTool == MOVE){
            dragging = true;
        }else if(Controller.activeTool == SELECT){
            controller.setActiveDrawableNode(c);
        }
    }
    public DrawableNode addNode(DrawableNode c) {
        c.setOnMousePressed(mouseEvent -> handlePressNode(mouseEvent, c));
        c.setOnMouseReleased(event -> {
            dragging = false;
        });
        c.setOnMouseDragged(event -> {
            if(dragging){
                c.setPos(event.getX(),event.getY());
                c.updateEdges();
            }
        });
        drawableNodes.add(c);
        return c;
    }
    public DrawableNode addNode(double x, double y, int radius, Color color) {
        DrawableNode c = new DrawableNode(x,y,radius,color, Controller.activeType);
        c.setOnMousePressed(mouseEvent -> handlePressNode(mouseEvent, c));
        c.setOnMouseReleased(event -> {
            dragging = false;
        });
        c.setOnMouseDragged(event -> {
            if(dragging){
                c.setPos(event.getX(),event.getY());
                c.updateEdges();
            }
        });
        drawableNodes.add(c);
        return c;
    }

    public  ArrayList<DrawableNode> getDrawableNodes(){
        return drawableNodes;
    }

    public  EdgeController getEdgeController(){
        return edgeController;
    }
}
