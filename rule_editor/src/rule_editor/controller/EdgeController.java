package rule_editor.controller;

import rule_editor.model.Edge;
import rule_editor.model.Node;

/**
 * Created by vilddjur on 1/25/17.
 */
public class EdgeController {
    private boolean dragging;
    public EdgeController(){
        dragging = false;
    }

    public Edge addEdge(Node c, Node s) {
        Edge e = new Edge(c, s);

        e.setOnMousePressed(mouseEvent -> handlePress(mouseEvent, e));
        e.setOnMouseReleased(event -> {
            dragging = false;
        });

        e.setOnMouseDragged(event -> {
                if(dragging){
                    e.makeArrow();
                }
        });

        return e;
    }

    private void handlePress(javafx.scene.input.MouseEvent mouseEvent, Edge e) {
        if(Controller.activeTool == Controller.tools.DELETE){
            e.delete();
            Controller.getActiveCanvas().getChildren().removeAll(e, e.getArrow());
        }else if(Controller.activeTool == Controller.tools.MOVE){
            dragging = true;
        }
    }

    public Edge addEdge(Edge e) {
        e.setOnMousePressed(mouseEvent -> dragging = true);
        e.setOnMouseReleased(event -> {
            dragging = false;
        });

        e.setOnMouseDragged(event -> {
            if(dragging){
                e.makeArrow();
            }
        });

        return e;
    }
}
