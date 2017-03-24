package rule_editor.controller;


import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.*;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

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
    public NodeController(Controller controller) {
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
        /*if (Controller.activeTool == NODE) {
            Controller.activeTool = SELECT;
        }*/

        if(Controller.activeTool == DELETE){
            Controller.getActiveCanvas().getChildren().remove(c);
            Controller.getActiveCanvas().getChildren().removeAll(c.getDrawableEdges());
            for (DrawableEdge e: c.getDrawableEdges()) {
                Controller.getActiveCanvas().getChildren().removeAll(e.getArrow());
            }
        }else if(Controller.activeTool == EDGE) {
            if (currentDrawableEdge == null) {
                currentDrawableEdge = edgeController.addEdge(c, null);
            } else {
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge.setEndNode(c));
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge);
                currentDrawableEdge = null;
            }
        }else if(Controller.activeTool == MOVE){
            dragging = true;
        }else if(Controller.activeTool == SELECT) {
            controller.setActiveDrawableAreaNode(c);
        } else if (Controller.activeTool == SUBNODE) {
            DrawableObjectNode node = new DrawableObjectNode(c.getCenterX(), c.getCenterY(), 10, (OBJECT_TYPE) Controller.activeType);
            if (c.addObject(node)) {
                node.setOnMouseClicked(mouseEvent -> handlePressSubnode(node));
                Controller.getActiveCanvas().getChildren().add(node);
                Controller.getActiveCanvas().getChildren().add(node.text);
            }
        }
    }

    private void handlePressSubnode(DrawableObjectNode node) {
        Log.level = Log.LEVEL.DEBUG;
        Log.print("NodeController: Subnode pressed: "+node.getType(), Log.LEVEL.DEBUG);
        if (Controller.activeTool == SUBEDGE) {
            if (currentDrawableEdge == null) {
                Log.print("NodeController: No current edge adding new.", Log.LEVEL.DEBUG);
                currentDrawableEdge = new DrawableEdge(node, null);
                node.addEdge(currentDrawableEdge);
            } else {
                Log.print("NodeController: Setting end node for current edge.", Log.LEVEL.DEBUG);
                currentDrawableEdge.setEndNode(node);
                node.addEdge(currentDrawableEdge);
                Log.print("NodeController: Adding edge to canvas "+currentDrawableEdge, Log.LEVEL.DEBUG);
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge);
                currentDrawableEdge = null;
            }
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
                c.updateSubnodes();
            }
        });

        drawableAreaNodes.add(c);
        return c;
    }

    public DrawableAreaNode addNode(double x, double y, int radius, Color color, AREA_TYPE type) {
        DrawableAreaNode c = new DrawableAreaNode(x,y,radius,color, type);
        return addNode(c);
    }

    public  ArrayList<DrawableAreaNode> getDrawableAreaNodes(){
        return drawableAreaNodes;
    }

    public  EdgeController getEdgeController(){
        return edgeController;
    }
}
