package rule_editor.controller;

import graph_generator.controller.GraphController;
import graph_generator.parser.CookbookParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.DrawableEdge;
import model.DrawableNode;
import model.Pattern;
import model.Rule;
import rule_editor.FileHandler;
import utils.Log;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static model.DrawableNode.DEFAULT_RADIUS;
import static rule_editor.controller.Controller.tools.*;

public class Controller {
    /**
     * Buttons for handling active tools
     */

    @FXML
    private AnchorPane window;

    @FXML
    private Button edge_button;
    @FXML
    private Button delete_button;
    @FXML
    private Button move_button;
    @FXML
    private Button select_node_button;
    @FXML
    private Button gen_button;


    @FXML
    private Button start_node_button;
    @FXML
    private Button end_node_button;
    @FXML
    private Button key_node_button;
    @FXML
    private Button lock_node_button;
    @FXML
    private Button room_node_button;
    @FXML
    private Button any_node_button;


    @FXML
    private Pane canvas;

    @FXML
    private Pane rule_canvas;
    @FXML
    private GridPane rule_pane;
    @FXML
    private TabPane rule_tab_pane;
    @FXML
    private Button new_scenario_button;
    private Rule activeRule;


    @FXML
    private MenuItem save_button;

    @FXML
    private MenuItem load_level_button;
    @FXML
    private MenuItem load_rule_button;

    @FXML
    private MenuItem rule_menu_item;

    @FXML
    private MenuItem close_button;

    @FXML
    private MenuItem new_button;

    @FXML
    private MenuItem level_menu_item;

    @FXML
    private MenuItem apply_cookbook;

    /**
     * inspector pane
     */
    @FXML
    public static Pane node_inspector_pane;
    @FXML
    public TextField active_node_id_field;
    @FXML
    private Button active_node_save_button;
    public DrawableNode activeDrawableNode;

    private HashMap<Pane, Pattern> scenarios;
    private Pattern matchingPattern;
    private Pattern currentLevel;

    public void setActiveDrawableNode(DrawableNode activeDrawableNode) {
        this.activeDrawableNode = activeDrawableNode;
        active_node_id_field.setText(String.valueOf(activeDrawableNode.getNodeId()));
    }


    public tools getActiveTool() {
        return activeTool;
    }

    public DrawableNode.NodeType getActiveType(){
        return activeType;
    }
    private GraphController graphController;
    /**
     * Returns the activeCanvas which is set when the mouse enters a pane we want to draw our graphs in.
     * @return The latest activated canvas.
     */
    public static Pane getActiveCanvas() {
        return activeCanvas;
    }

    /**
     * Enum to keep track of which tool is active
     */
    public enum tools {
        EDGE, NODE, DELETE, MOVE, SELECT
    }
    private FileChooser fileChooser = new FileChooser();
    public static tools activeTool;
    public static DrawableNode.NodeType activeType;

    public static Pane activeCanvas;

    private NodeController nodeController;
    public void initialize(){
        GraphController graphController = new GraphController();
        scenarios = new HashMap<Pane, Pattern>();
        activeType = DrawableNode.NodeType.START;
        activeTool = NODE;
        activeCanvas = rule_canvas;
        currentLevel = new Pattern();

        edge_button.setOnMouseClicked(mouseEvent -> activeTool = EDGE);
        // init editing buttons
        delete_button.setOnMouseClicked(mouseEvent -> activeTool = DELETE);
        move_button.setOnMouseClicked(mouseEvent -> activeTool = MOVE);
        select_node_button.setOnMouseClicked(mouseEvent -> activeTool = SELECT);
        // init node buttons
        start_node_button.setOnMouseClicked(mouseEvent -> activateType(DrawableNode.NodeType.START));
        end_node_button.setOnMouseClicked(mouseEvent -> activateType(DrawableNode.NodeType.END));
        key_node_button.setOnMouseClicked(mouseEvent -> activateType(DrawableNode.NodeType.KEY));
        lock_node_button.setOnMouseClicked(mouseEvent -> activateType(DrawableNode.NodeType.LOCK));
        room_node_button.setOnMouseClicked(mouseEvent -> activateType(DrawableNode.NodeType.ROOM));
        any_node_button.setOnMouseClicked(mouseEvent -> activateType(DrawableNode.NodeType.ANY));
        // init top menu
        save_button.setOnAction(actionEvent -> PrepareSave());
        load_level_button.setOnAction(actionEvent -> PrepareLoadLevel());
        load_rule_button.setOnAction(actionEvent -> PrepareLoadRule());
        rule_menu_item.setOnAction(actionEvent -> showRules());
        level_menu_item.setOnAction(actionEvent -> showLevel());
        close_button.setOnAction(actionEvent -> Platform.exit());
        //Test of cookbook
        apply_cookbook.setOnAction(actionEvent -> loadCookbook());

        //init level canvas
        canvas.setOnMouseClicked(mouseEvent -> handlePress(mouseEvent, canvas));
        canvas.setOnMouseEntered(event -> requestFocus(canvas));

        gen_button.setOnMouseClicked(event -> generateLevel());

        //init rule canvas
        rule_canvas.setOnMouseClicked(mouseEvent -> handlePress(mouseEvent, rule_canvas));
        rule_canvas.setOnMouseEntered(mouseEvent -> requestFocus(rule_canvas));
        new_scenario_button.setOnMouseClicked(mouseEvent -> addTab("new tab"));
        nodeController = new NodeController(this);

        // init inspector pane
        active_node_save_button.setOnMouseClicked(mouseEvent -> saveActiveNode());

        new_button.setOnAction(actionEvent -> {nodeController.clear(); canvas.getChildren().clear(); currentLevel = new Pattern();});

        // initialize currentRule;
        matchingPattern = new Pattern();
        activeRule = new Rule(matchingPattern);
    }

    private void saveActiveNode() {
        activeDrawableNode.setNodeId(Integer.parseInt(active_node_id_field.getText()));
    }


    /**
     * Generates a level based of current level canvas content and displays it in the level canvas
     * looks for scenarios/rules in ./saves/rules
     */
    private void generateLevel() {
        List<Rule> rules = loadAllRules();

        canvas.getChildren().clear();

        //match to current level
        translateLevel(currentLevel, rules);

        Log.print("Dumping generated level: ", Log.LEVEL.DEBUG);
        for (DrawableNode n : currentLevel.drawableNodes) {
            Log.print("  node: Type: " + n.getType() + ", id:" + n.getNodeId() + ", #edges: " + n.getDrawableEdges().size(), Log.LEVEL.DEBUG);
        }

        //display
        updateDisplayedGraph();
    }

    /**
     * Updates the currentLevel.
     */
    private void updateDisplayedGraph() {
        for(DrawableNode n : currentLevel.drawableNodes){
            nodeController.addNode(n);
            canvas.getChildren().add(n);
            for(DrawableEdge e : n.getDrawableEdges()){
                DrawableEdge c = nodeController.getEdgeController().addEdge(e);
                if(!canvas.getChildren().contains(c.getArrow()))
                    canvas.getChildren().add(c.getArrow());
                if(!canvas.getChildren().contains(c))
                    canvas.getChildren().add(c);
                e.updateNodes();
            }
        }
    }

    /**
     * Loads all available rules.
     *
     * @return
     * List of all rules.
     */
    private List<Rule> loadAllRules() {
        File folder = new File("saves/rules");
        List<Rule> rules = new ArrayList<>();

        for (File f : folder.listFiles()) {
            if(!f.isDirectory()){
                Pattern match = new Pattern(FileHandler.LoadMatchingPattern(f));
                rules.add(new Rule(match, FileHandler.LoadTranslations(f)));
            }
        }

        return rules;
    }

    private void translateLevel(Pattern pattern, List<Rule> rules) {
        if(graphController == null){
            graphController = new GraphController();
        }
        graphController.applyRandomRule(rules, pattern);
    }

    private void showRules() {
        canvas.setVisible(false);
        rule_pane.setVisible(true);

        activeCanvas = rule_canvas;

        Log.print("Dumping current rule: \n MatchingPattern: ", Log.LEVEL.INFO);
        for (DrawableNode n : activeRule.matchingPattern.drawableNodes) {
            Log.print("  node: " + n.getType().toString(), Log.LEVEL.INFO);
        }
        Log.print(" possibleOutcomes: ", Log.LEVEL.INFO);
        for (Pattern p : activeRule.possibleTranslations){
            Log.print("  outcome: ", Log.LEVEL.INFO);
            for (DrawableNode n : p.drawableNodes) {
                Log.print("   node: " + n.getType().toString(), Log.LEVEL.INFO);
            }
        }
    }

    private void showLevel() {
        canvas.setVisible(true);
        rule_pane.setVisible(false);

        activeCanvas = canvas;
    }

    private void activateType(DrawableNode.NodeType type) {
        activeType = type;
        activeTool = NODE;
    }
    private void requestFocus(Pane p){
        activeCanvas = p;
    }

    private void PrepareLoadRule() {
        File file;
        Stage stage;

        fileChooser.setTitle("Explorer");
        stage = (Stage) window.getScene().getWindow();
        fileChooser.setInitialDirectory(new File("."));
        file = fileChooser.showOpenDialog(stage);

        //No file selected, don't do anything
        if (file == null) {return;}

        //Clear canavases before loading in elements
        rule_canvas.getChildren().clear();
        rule_tab_pane.getTabs().clear();
        scenarios.clear();

        //new rule
        matchingPattern = new Pattern();
        activeRule = new Rule(matchingPattern);


        Pair<ArrayList<DrawableNode>,ArrayList<DrawableEdge>> pair = FileHandler.LoadMatchingPattern(file);
        ArrayList<Pair<ArrayList<DrawableNode>,ArrayList<DrawableEdge>>> translations = FileHandler.LoadTranslations(file);
        activeCanvas = rule_canvas;
        insertIntoCanvasAndList(rule_canvas, matchingPattern.drawableNodes, pair);

        for (Pair<ArrayList<DrawableNode>, ArrayList<DrawableEdge>> p : translations){

            Pair<Pane, Pattern> panePatternPair = addTab("saved tab");
            insertIntoCanvasAndList(panePatternPair.getKey(), panePatternPair.getValue().drawableNodes, p);
        }
    }

    private void insertIntoCanvasAndList(Pane canvas, ArrayList<DrawableNode> drawableNodes, Pair<ArrayList<DrawableNode>, ArrayList<DrawableEdge>> pair) {
        for(DrawableNode drawableNode : pair.getKey()){
            drawableNodes.add(drawableNode);
            canvas.getChildren().add(drawableNode);
            nodeController.addNode(drawableNode);
        }
        for(DrawableEdge e : pair.getValue()){
            DrawableEdge c = nodeController.getEdgeController().addEdge(e);
            canvas.getChildren().add(c.getArrow());
            canvas.getChildren().add(c);
        }
    }

    /**
     * Loads cookbook and applies it to the current graph.
     */
    private void loadCookbook() {
        CookbookParser cookbookParser = new CookbookParser();
        String file = JOptionPane.showInputDialog("Cookbook","sample_cookbook.cb");

        Log.print("Controller: Loading cookbook...", Log.LEVEL.INFO);

        if (file == null || file.equals("")) {
            Log.print("Controller: No cookbook selected.", Log.LEVEL.WARNING);
            return;
        }

        canvas.getChildren().clear();

        if (!cookbookParser.parseCookbook(file, currentLevel)) {
            Log.print("Controller: Cookbook \""+file+"\" failed to apply.", Log.LEVEL.ERROR);
            Platform.exit();
            return;
        }

        updateDisplayedGraph();
        Log.print("Controller: Cookbook "+file+" was applied successfully.", Log.LEVEL.INFO);
    }

    /**
     * Loads file and appends elements to canvas
     */
    private void PrepareLoadLevel() {
        File file;
        Stage stage;

        fileChooser.setTitle("Explorer");
        stage = (Stage) window.getScene().getWindow();
        fileChooser.setInitialDirectory(new File("."));
        file = fileChooser.showOpenDialog(stage);

        //No file selected, don't do anything
        if (file == null) {return;}

        //Clear before loading in elements
        nodeController.clear();
        canvas.getChildren().clear();


        Pair<ArrayList<DrawableNode>,ArrayList<DrawableEdge>> pair = FileHandler.LoadNodes(file);
        for(DrawableNode drawableNode : pair.getKey()){
            DrawableNode c = nodeController.addNode(drawableNode);
            canvas.getChildren().add(c);
        }
        for(DrawableEdge e : pair.getValue()){
            DrawableEdge c = nodeController.getEdgeController().addEdge(e);
            canvas.getChildren().add(c.getArrow());
            canvas.getChildren().add(c);
        }
    }

    /**
     * Saves level state in file
     */
    private void PrepareSave() {
        String path = JOptionPane.showInputDialog("Save","newfile");
        if (path == "" || path == null) {
            path = "newfile";
        }
        if(rule_pane.isVisible()){
            FileHandler.saveRule(activeRule,"saves/rules/"+path);
        }else {
            FileHandler.SaveNodes(nodeController.getDrawableNodes(), "saves/levels/" + path);
        }
    }

    private void handlePress(MouseEvent event, Pane c) {
        if(activeTool == NODE){
            DrawableNode drawableNode = nodeController.addNode(event.getX(), event.getY(), DEFAULT_RADIUS, Color.BLUE);

            c.getChildren().add(drawableNode);
            if(c == rule_canvas){
                matchingPattern.drawableNodes.add(drawableNode);
            }else if(c == canvas){
                currentLevel.drawableNodes.add(drawableNode);
            } else {
                scenarios.get(c).drawableNodes.add(drawableNode);
            }
        }
    }
    private Pair<Pane, Pattern> addTab(String s){
        Tab tab = new Tab(s);
        Pane c = new Pane();
        c.setOnMouseClicked(mouseEvent -> {
            activeCanvas = c;
            handlePress(mouseEvent, c);
        });
        c.setOnMouseEntered(event -> requestFocus(c));
        tab.setContent(c);
        rule_tab_pane.getTabs().add(tab);
        Pattern p = new Pattern();
        activeRule.possibleTranslations.add(p);
        scenarios.put(c, p);
        return new Pair<>(c, p);
    }
}
