package rule_editor.controller;


import model.DrawableEdge;
import model.DrawableNode;

/**
 * Created by vilddjur on 1/25/17.
 */
public class EdgeController {
    private boolean dragging;
    public EdgeController(){
        dragging = false;
    }

    public DrawableEdge addEdge(DrawableNode c, DrawableNode s) {
        DrawableEdge e = new DrawableEdge(c, s);

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

    private void handlePress(javafx.scene.input.MouseEvent mouseEvent, DrawableEdge e) {
        if(Controller.activeTool == Controller.tools.DELETE){
            e.delete();
            Controller.getActiveCanvas().getChildren().removeAll(e, e.getArrow());
        }else if(Controller.activeTool == Controller.tools.MOVE){
            dragging = true;
        }
    }

    public DrawableEdge addEdge(DrawableEdge e) {
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
