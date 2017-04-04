package rule_editor.controller;


import model.*;
import utils.Log;
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
    private static NodeController instance = null;

    private NodeController(Controller controller) {
        currentDrawableEdge = null;
        dragging = false;
        this.controller = controller;
        this.edgeController = EdgeController.getInstance();
        this.drawableAreaNodes = new ArrayList<DrawableAreaNode>();
    }

    public static NodeController getInstance(Controller controller) {
        if (instance == null) {
            instance = new NodeController(controller);
        }
        return instance;
    }

    /**
     * Removes all Nodes and Edges
     */
    public void clear() {
        drawableAreaNodes.clear();
    }

    public void handlePressNode(DrawableAreaNode c) {
        if (Controller.activeTool == DELETE) {
            Controller.getActiveCanvas().getChildren().remove(c);
            Controller.getActiveCanvas().getChildren().removeAll(c.getDrawableEdges());
            for (DrawableEdge e: c.getDrawableEdges()) {
                Controller.getActiveCanvas().getChildren().removeAll(e.getArrow());
            }
        } else if (Controller.activeTool == EDGE) {
            if (currentDrawableEdge == null) {
                currentDrawableEdge = edgeController.addEdge(c, null);
            } else {
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge.setEndNode(c));
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge);
                currentDrawableEdge = null;
            }
        } else if (Controller.activeTool == MOVE) {
            dragging = true;
        } else if (Controller.activeTool == SELECT) {
            controller.setActiveDrawableAreaNode(c);
        } else if (Controller.activeTool == SUBNODE) {
            DrawableSubnode node = new DrawableSubnode(c.getCenterX(), c.getCenterY(), (OBJECT_TYPE) Controller.activeType);
            if (c.addObject(node)) {
                node.setOnMouseClicked(mouseEvent -> handlePressSubnode(node));
                Controller.getActiveCanvas().getChildren().add(node);
                Controller.getActiveCanvas().getChildren().add(node.text);
            }
        }
    }

    private void handlePressSubnode(DrawableSubnode node) {
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

    public void addNode(DrawableAreaNode node) {
        setDraggable(node);
        drawableAreaNodes.add(node);
    }

    private void setDraggable(DrawableAreaNode node) {
        node.setOnMousePressed(mouseEvent -> handlePressNode(node));
        node.setOnMouseReleased(event -> {
            dragging = false;
        });

        node.setOnMouseDragged(event -> {
            if (dragging) {
                node.setPos(event.getX(),event.getY());
                node.updateEdges();
                node.updateSubnodes();
            }
        });
    }

    public  ArrayList<DrawableAreaNode> getDrawableAreaNodes(){
        return drawableAreaNodes;
    }

    public  EdgeController getEdgeController(){
        return edgeController;
    }
}
