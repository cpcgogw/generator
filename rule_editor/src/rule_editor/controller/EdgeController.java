package rule_editor.controller;

import model.DrawableEdge;
import model.DrawableAreaNode;

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
        DrawableEdge edge = new DrawableEdge(from, to);
        setDraggable(edge);
        return edge;
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
        } else if (Controller.activeTool == Controller.tools.MOVE) {
            dragging = true;
        }
    }
}
