package rule_editor.controller;


import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.DrawableEdge;
import model.DrawableAreaNode;

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
    private ArrayList<DrawableAreaNode> drawableAreaNodes;

    /**
     *
     */
    public NodeController(Controller controller){
        currentDrawableEdge = null;
        dragging = false;
        this.controller = controller;
        this.edgeController = new EdgeController();
        this.drawableAreaNodes = new ArrayList<DrawableAreaNode>();
    }

    /**
     * Removes all Nodes and Edges
     */
    public void clear() {
        drawableAreaNodes.clear();
    }

    private void handlePressNode(MouseEvent event, DrawableAreaNode c) {
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
            controller.setActiveDrawableAreaNode(c);
        }
    }
    public DrawableAreaNode addNode(DrawableAreaNode c) {
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
        drawableAreaNodes.add(c);
        return c;
    }
    public DrawableAreaNode addNode(double x, double y, int radius, Color color) {
        DrawableAreaNode c = new DrawableAreaNode(x,y,radius,color, Controller.activeType);
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
        drawableAreaNodes.add(c);
        return c;
    }

    public  ArrayList<DrawableAreaNode> getDrawableAreaNodes(){
        return drawableAreaNodes;
    }

    public  EdgeController getEdgeController(){
        return edgeController;
    }
}
