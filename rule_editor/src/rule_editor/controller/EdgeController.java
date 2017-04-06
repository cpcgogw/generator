package rule_editor.controller;

import model.DrawableEdge;
import model.DrawableAreaNode;
import model.EDGE_TYPE;

/**
 * Created by vilddjur on 1/25/17.
 */
public class EdgeController {

    private boolean dragging;
    private static EdgeController instance = null;

    public EdgeController(){
        dragging = false;
    }

    public static EdgeController getInstance() {
        if (instance == null) {
            instance = new EdgeController();
        }
        return instance;
    }

    public DrawableEdge addEdge(DrawableAreaNode from, DrawableAreaNode to) {
        return addEdge(from, to, EDGE_TYPE.NORMAL);
    }

    public void setDraggable(DrawableEdge edge) {
        edge.setOnMousePressed(mouseEvent -> handlePress(edge));
        edge.setOnMouseReleased(event -> {
            dragging = false;
        });

        edge.setOnMouseDragged(event -> {
            if(dragging){
                edge.makeArrow();
            }
        });
    }

    private void handlePress(DrawableEdge edge) {
        if (Controller.activeTool == Controller.tools.DELETE) {
            edge.delete();
            Controller.getActiveCanvas().getChildren().removeAll(edge, edge.getArrow());
        } else if (Controller.activeTool == Controller.tools.SELECT) {
            dragging = true;
        }else if (Controller.activeTool == Controller.tools.SUBEDGE){
            if(NodeController.currentSubNode != null){
                NodeController.currentSubNode.addEdge(edge);
                NodeController.currentSubNode.drawEdges();
            }
        }
    }

    public DrawableEdge addEdge(DrawableAreaNode from, DrawableAreaNode to, EDGE_TYPE type) {
        DrawableEdge edge = new DrawableEdge(from, to, type);
        setDraggable(edge);
        return edge;
    }
}
