package rule_editor.controller;


import model.*;
import utils.Log;

import java.util.ArrayList;
import java.util.Iterator;
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
    private static NodeController instance = null;
    public static DrawableSubnode currentSubNode;

    private NodeController(Controller controller) {
        currentDrawableEdge = null;
        dragging = false;
        this.controller = controller;
        this.edgeController = EdgeController.getInstance();
        this.drawableAreaNodes = new ArrayList<>();
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

    public void handlePressNode(DrawableAreaNode node) {
        if (Controller.activeTool == DELETE) {
            Log.tmpPrint("NodeController: Nodes before deleting node 1 "+drawableAreaNodes, Log.LEVEL.DEBUG);
            List<DrawableEdge> edges = node.getDrawableEdges();
            for (DrawableEdge e : edges) {
                if (node.equals(e.getTo())) {
                    ((DrawableAreaNode) e.getFrom()).removeEdge(e);
                } else {
                    ((DrawableAreaNode) e.getTo()).removeEdge(e);
                }
            }
            //TODO: Figure out a better way to remove nodes when deleted.
            controller.removeFromLevel(node);
            Log.print("NodeController: Nodes after deleting node 1 "+drawableAreaNodes, Log.LEVEL.DEBUG);
            controller.updateDisplayedGraph();
        } else if (Controller.activeTool == EDGE) {
            if (currentDrawableEdge == null) {
                currentDrawableEdge = edgeController.addEdge(node, null);
            } else {
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge.setEndDrawableAreaNode(node));
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge);
                currentDrawableEdge = null;
            }
        } else if (Controller.activeTool == LOCKED_EDGE){
            if (currentDrawableEdge == null){
                currentDrawableEdge = edgeController.addEdge(node, null, EDGE_TYPE.LOCKED);
            } else {
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge.setEndNode(node));
                Controller.getActiveCanvas().getChildren().add(currentDrawableEdge);
                currentDrawableEdge = null;
            }
        } else if (Controller.activeTool == SELECT) {
            dragging = true;
            controller.setActiveNode(node);
        } else if (Controller.activeTool == SUBNODE) {
            DrawableSubnode subnode = new DrawableSubnode(node.getCenterX(), node.getCenterY(), (OBJECT_TYPE) Controller.activeType);
            if (node.addObject(subnode)) {
                subnode.setOnMouseClicked(mouseEvent -> handlePressSubnode(subnode, node));
                Controller.getActiveCanvas().getChildren().add(subnode);
                //Controller.getActiveCanvas().getChildren().add(subnode.drawEdges());
                Controller.getActiveCanvas().getChildren().add(subnode.text);
            }
        }
    }

    private void handlePressSubnode(DrawableSubnode subnode, DrawableAreaNode node) {
        Log.tmpPrint("NodeController: Subnode pressed: "+subnode.getNodeId(), Log.LEVEL.DEBUG);

        if (Controller.activeTool == SUBEDGE) {
            if (currentDrawableEdge == null) {
                currentDrawableEdge = edgeController.addEdge(subnode, null);
            } else {
                if (subnode.getNodeId() == currentDrawableEdge.getFrom().getNodeId()) {
                    return;
                }
                //TODO: setEndNode should not return the arrow, it should have a separate function.
                currentDrawableEdge.setEndNode(subnode);
                Controller.activeCanvas.getChildren().add(currentDrawableEdge);
                currentDrawableEdge = null;
            }
        } else if (Controller.activeTool == SELECT) {
            controller.setActiveNode(subnode);
        } else if (Controller.activeTool == DELETE) {
            //TODO: something fucks up if any other subnode than the one with lowest ID is pressed.
            List<DrawableEdge> edges = subnode.getDrawableEdges();
            for (Iterator<DrawableEdge> iterator = edges.iterator(); iterator.hasNext();) {
                DrawableEdge edge = iterator.next();
                if (subnode.equals(edge.getFrom())) {
                    ((DrawableSubnode) edge.getTo()).removeEdge(edge);
                } else {
                    ((DrawableSubnode) edge.getFrom()).removeEdge(edge);
                }
                iterator.remove();
            }
            node.removeSubnode(subnode);
            //TODO: the display should of course be updated after this action,
            //TODO: but that is not possible from inside this method. Manual update button added for now.
        }
    }

    public void addNode(DrawableAreaNode node) {
        setDraggable(node);
        drawableAreaNodes.add(node);
    }

    private void setDraggable(DrawableAreaNode node) {
        node.setOnMousePressed(mouseEvent -> handlePressNode(node));

        for (DrawableSubnode subnode : node.getDrawableSubnodes()) {
            subnode.setOnMouseClicked(event -> handlePressSubnode(subnode, node));
        }

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
        ArrayList<DrawableAreaNode> nodes = new ArrayList<>();
        nodes.addAll(drawableAreaNodes);
        return nodes;
    }

    public EdgeController getEdgeController(){
        return edgeController;
    }

    public void setNodes(List<DrawableAreaNode> nodes) {
        this.drawableAreaNodes.clear();

        for (DrawableAreaNode node : nodes) {
            node.updateEdges();
            node.updateSubnodes();
            addNode(node);
        }
    }
}
