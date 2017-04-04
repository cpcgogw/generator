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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.*;
import translator.Translator;
import translator.model.NodeGrid;
import utils.FileHandler;
import utils.Log;

import javax.swing.*;
import java.io.File;
import java.util.*;

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
    private Button level_to_grid_button;
    @FXML
    private Button export_button;

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
    private Button grass_node_button;
    @FXML
    private Button desert_node_button;
    @FXML
    private Button subedge_button;
    @FXML
    private Button enemy_subnode;


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

    @FXML
    public static Pane node_inspector_pane;
    @FXML
    public TextField active_node_id_field;
    @FXML
    private Button active_node_save_button;

    private HashMap<Pane, DrawablePattern> scenarios;

    private DrawablePattern currentLevel;
    private Rule activeRule;

    private FileChooser fileChooser;
    private NodeController nodeController;
    private GraphController graphController;

    public static tools activeTool;
    public static TYPE activeType;
    public static Pane activeCanvas;
    public DrawableAreaNode activeDrawableAreaNode;

    /**
     * Used to keep track of which tool is active
     */
    public enum tools {
        EDGE, NODE, SUBNODE, DELETE, MOVE, SELECT, SUBEDGE
    }

    public void initialize(){
        Log.level = Log.LEVEL.INFO;

        fileChooser = new FileChooser();
        graphController = GraphController.getInstance();
        nodeController = NodeController.getInstance(this);
        scenarios = new HashMap<>();
        activeType = AREA_TYPE.TOWN;
        activeTool = NODE;
        activeCanvas = rule_canvas;
        currentLevel = new DrawablePattern();

        // Init editing buttons
        delete_button.setOnMouseClicked(mouseEvent -> activeTool = DELETE);
        move_button.setOnMouseClicked(mouseEvent -> activeTool = MOVE);
        select_node_button.setOnMouseClicked(mouseEvent -> activeTool = SELECT);
        level_to_grid_button.setOnMouseClicked(mouseEvent -> currentLevelToGrid());
        export_button.setOnMouseClicked(mouseEvent -> exportCurrentLevel());
        gen_button.setOnMouseClicked(event -> generateLevel());

        // Init subnode buttons
        start_node_button.setOnMouseClicked(mouseEvent -> activateType(OBJECT_TYPE.START));
        end_node_button.setOnMouseClicked(mouseEvent -> activateType(OBJECT_TYPE.END));
        key_node_button.setOnMouseClicked(mouseEvent -> activateType(OBJECT_TYPE.KEY));
        enemy_subnode.setOnMouseClicked(mouseEvent -> activateType(OBJECT_TYPE.MONSTER));
        lock_node_button.setOnMouseClicked(mouseEvent -> activateType(OBJECT_TYPE.LOCK)); //SHOULD BE EDGE

        // Init node buttons
        room_node_button.setOnMouseClicked(mouseEvent -> activateType(AREA_TYPE.TOWN));
        grass_node_button.setOnMouseClicked(mouseEvent -> activateType(AREA_TYPE.GRASSFIELD));
        desert_node_button.setOnMouseClicked(mouseEvent -> activateType(AREA_TYPE.DESERT));

        // Init edge buttons
        subedge_button.setOnMouseClicked(mouseEvent -> activeTool = SUBEDGE);
        edge_button.setOnMouseClicked(mouseEvent -> activeTool = EDGE);

        // Init top menu
        save_button.setOnAction(actionEvent -> prepareSave());
        load_level_button.setOnAction(actionEvent -> prepareLoadLevel());
        load_rule_button.setOnAction(actionEvent -> prepareLoadRule());
        rule_menu_item.setOnAction(actionEvent -> showRules());
        level_menu_item.setOnAction(actionEvent -> showLevel());
        close_button.setOnAction(actionEvent -> Platform.exit());
        apply_cookbook.setOnAction(actionEvent -> loadCookbook());
        new_button.setOnAction(actionEvent -> {nodeController.clear(); canvas.getChildren().clear(); currentLevel = new DrawablePattern();});

        // Init level canvas
        canvas.setOnMouseClicked(mouseEvent -> handlePress(mouseEvent, canvas));
        canvas.setOnMouseEntered(event -> requestFocus(canvas));

        // Init rule canvas
        rule_canvas.setOnMouseClicked(mouseEvent -> handlePress(mouseEvent, rule_canvas));
        rule_canvas.setOnMouseEntered(mouseEvent -> requestFocus(rule_canvas));
        new_scenario_button.setOnMouseClicked(mouseEvent -> addTab("new tab"));

        // init inspector pane
        active_node_save_button.setOnMouseClicked(mouseEvent -> saveActiveNode());

        activeRule = new Rule(new DrawablePattern());
        showLevel();
    }

    private void exportCurrentLevel() {
        String path = JOptionPane.showInputDialog("Save","newfile");

        if (path == "" || path == null) {
            path = "newfile";
        }
        Translator.exportLevel(currentLevel, path);
    }

    public void setActiveDrawableAreaNode(DrawableAreaNode activeDrawableAreaNode) {
        this.activeDrawableAreaNode = activeDrawableAreaNode;
        active_node_id_field.setText(String.valueOf(activeDrawableAreaNode.getNodeId()));
    }

    public tools getActiveTool() {
        return activeTool;
    }

    public TYPE getActiveType(){
        return activeType;
    }

    /**
     * Returns the activeCanvas which is set when the mouse enters a pane we want to draw our graphs in.
     * @return The latest activated canvas.
     */
    public static Pane getActiveCanvas() {
        return activeCanvas;
    }

    private void activateType(OBJECT_TYPE type) {
        activeTool = SUBNODE;
        activeType = type;
        Log.print("Controller: Setting type to "+type, Log.LEVEL.DEBUG);
    }

    private void currentLevelToGrid() {
        NodeGrid grid = new NodeGrid(currentLevel.drawableAreaNodes.size());

        Log.print(currentLevel, Log.LEVEL.DEBUG);

        Translator.placeGraphOnGrid(currentLevel, grid);
        for (int x = 0; x < grid.size(); x++) {
            for (int y = 0; y < grid.size(); y++) {
                Node n = (Node) grid.getTile(x,y);
                if(n != null){
                    DrawableAreaNode node = (DrawableAreaNode) n;
                    node.setCenterX(x*150+50);
                    node.setCenterY(y*150+50);
                    node.updateEdges();
                }
            }
        }
    }

    private void saveActiveNode() {
        activeDrawableAreaNode.setNodeId(Integer.parseInt(active_node_id_field.getText()));
    }

    /**
     * Generates a level based of current level canvas content and displays it in the level canvas
     * looks for scenarios/rules in ./saves/rules
     */
    private void generateLevel() {
        List<Rule> rules = loadAllRules();

        Log.level = Log.LEVEL.DEBUG;

        canvas.getChildren().clear();

        translateLevel(currentLevel, rules);

        Log.print("Dumping generated level: ", Log.LEVEL.DEBUG);
        for (DrawableAreaNode n : currentLevel.drawableAreaNodes) {
            Log.print("  node: Type: " + n.getType() + ", id:" + n.getNodeId() + ", #edges: " + n.getDrawableEdges().size(), Log.LEVEL.DEBUG);
        }

        showLevel();
    }

    /**
     * Updates the currentLevel.
     */
    private void updateDisplayedGraph() {
        Set<DrawableEdge> edges = new HashSet<>();
        for (DrawableAreaNode node : currentLevel.drawableAreaNodes) {
            nodeController.addNode(node);
            canvas.getChildren().add(node);
            node.updateSubnodes();

            for (DrawableEdge edge : node.getDrawableEdges()) {
                edges.add(edge);
            }

            for (DrawableSubnode subnode : node.getDrawableSubnodes()) {
                canvas.getChildren().add(subnode);
                canvas.getChildren().add(subnode.text);

                for (DrawableEdge edge : subnode.getDrawableEdges()) {
                    edges.add(edge);
                }
            }
        }

        for (DrawableEdge edge : edges) {
            canvas.getChildren().add(edge);
        }
    }

    private List<Rule> loadAllRules() {
        File folder = new File("saves/rules");
        List<Rule> rules = new ArrayList<>();

        for (File f : folder.listFiles()) {
            if(!f.isDirectory()){
                //TODO: Refactor so FileHandler is used correctly
                DrawablePattern match = new DrawablePattern((ArrayList<DrawableAreaNode>) FileHandler.loadMatch(f));
                ArrayList<DrawablePattern> p = new ArrayList<>();
                for (List<DrawableAreaNode> list : FileHandler.loadTranslations(f)) {
                    p.add(new DrawablePattern((ArrayList<DrawableAreaNode>) list));
                }

                rules.add(new Rule(match, p));
            }
        }

        return rules;
    }

    private void translateLevel(DrawablePattern drawablePattern, List<Rule> rules) {
        graphController.applyRandomRule(rules, drawablePattern);
    }

    private void showRules() {
        canvas.setVisible(false);
        rule_pane.setVisible(true);
        activeCanvas = rule_canvas;

        addToCanvas(activeRule.matchingDrawablePattern.drawableAreaNodes);

        for (DrawablePattern p : activeRule.possibleTranslations) {
            addToCanvas(p.drawableAreaNodes);
        }
    }

    private void showLevel() {
        canvas.setVisible(true);
        rule_pane.setVisible(false);

        activeCanvas = canvas;

        updateDisplayedGraph();
    }

    private void activateType(AREA_TYPE type) {
        activeType = type;
        activeTool = NODE;
    }

    private void requestFocus(Pane p){
        activeCanvas = p;
    }

    /**
     * This will be used to add things to canvas at a later time when all refactoring is done.
     *
     * @param nodes
     */
    private void addToCanvas(List<DrawableAreaNode> nodes) {
        Set<DrawableEdge> edgeSet = new HashSet<>();

        for (DrawableAreaNode node : nodes) {
            Log.print("Controller: Loaded node "+node, Log.LEVEL.DEBUG);
            activeCanvas.getChildren().add(node);
            nodeController.addNode(node);
            for (DrawableSubnode subnode : node.getDrawableSubnodes()) {
                Log.print("Controller: Loaded rule subnode "+subnode.getNodeId(), Log.LEVEL.DEBUG);
                activeCanvas.getChildren().add(subnode);
                activeCanvas.getChildren().add(subnode.text);

                for (DrawableEdge edge : subnode.getDrawableEdges()) {
                    Log.print("Controller: Loaded subedge ("+edge.getFrom().getNodeId()+", "+edge.getTo().getNodeId()+")", Log.LEVEL.DEBUG);
                    edgeSet.add(edge);
                }
            }

            for (DrawableEdge edge : node.getDrawableEdges()) {
                Log.print("Controller: Loaded edge "+edge, Log.LEVEL.DEBUG);
                edgeSet.add(edge);
            }
        }

        for (DrawableEdge edge : edgeSet) {
            activeCanvas.getChildren().add(edge);
        }
    }

    //TODO: Should likely change name to something more descriptive
    private File loadRule() {
        File file;
        Stage stage;

        fileChooser.setTitle("Explorer");
        stage = (Stage) window.getScene().getWindow();
        fileChooser.setInitialDirectory(new File("./saves/rules/"));
        file = fileChooser.showOpenDialog(stage);

        return file;
    }

    //TODO: Should likely change name to something more descriptive
    private File loadLevel() {
        File file;
        Stage stage;

        fileChooser.setTitle("Explorer");
        stage = (Stage) window.getScene().getWindow();
        fileChooser.setInitialDirectory(new File("./saves/levels/"));
        file = fileChooser.showOpenDialog(stage);

        return file;
    }

    private void prepareLoadRule() {
        File file = loadRule();

        //No file selected, don't do anything
        if (file == null) {return;}

        //Clear canavases before loading in elements
        clearCanvases();

        //new rule
        activeRule = new Rule(new DrawablePattern());

        // Extract matching pattern and possible translations
        List<DrawableAreaNode> match = FileHandler.loadMatch(file);
        List<List<DrawableAreaNode>> translations = FileHandler.loadTranslations(file);

        DrawablePattern p = new DrawablePattern((ArrayList<DrawableAreaNode>) match);
        activeRule.matchingDrawablePattern = p;

        // Conversion to DrawablePattern
        ArrayList<DrawablePattern> translationPatterns = new ArrayList<>();
        for (List<DrawableAreaNode> translation : translations) {
            translationPatterns.add(new DrawablePattern((ArrayList<DrawableAreaNode>) translation));
        }

        activeRule.possibleTranslations = translationPatterns;

        showRules();
    }

    private void clearCanvases() {
        rule_canvas.getChildren().clear();
        rule_tab_pane.getTabs().clear();
        scenarios.clear();
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
    //TODO: Rewrite
    private void prepareLoadLevel() {
        File file = loadLevel();
        List<DrawableAreaNode> nodes;

        //No file selected, don't do anything
        if (file == null) {return;}

        //Clear before loading in elements
        nodeController.clear();
        canvas.getChildren().clear();

        nodes = FileHandler.loadLevel(file);
        currentLevel.drawableAreaNodes = nodes;

        showLevel();
    }

    /**
     * Saves level state in file
     */
    private void prepareSave() {
        String path = JOptionPane.showInputDialog("Save","newfile");

        if (path == "" || path == null) {
            path = "newfile";
        }

        if (rule_pane.isVisible()) {
            FileHandler.saveRule(activeRule,"saves/rules/"+path);
        } else {
            FileHandler.saveNodes(nodeController.getDrawableAreaNodes(), "saves/levels/" + path);
        }
    }

    private void handlePress(MouseEvent event, Pane c) {
        if (activeTool == NODE) {
            DrawableAreaNode node = new DrawableAreaNode(event.getX(), event.getY(), (AREA_TYPE) activeType);
            nodeController.addNode(node);
            c.getChildren().add(node);

            if (c == rule_canvas) {
                activeRule.matchingDrawablePattern.drawableAreaNodes.add(node);
            } else if (c == canvas) {
                currentLevel.drawableAreaNodes.add(node);
            } else {
                scenarios.get(c).drawableAreaNodes.add(node);
            }
        }
    }

    private Pair<Pane, DrawablePattern> addTab(String s) {
        Tab tab = new Tab(s);
        Pane c = new Pane();
        DrawablePattern p = new DrawablePattern();

        c.setOnMouseClicked(mouseEvent -> {
            activeCanvas = c;
            handlePress(mouseEvent, c);
        });

        c.setOnMouseEntered(event -> requestFocus(c));
        tab.setContent(c);
        rule_tab_pane.getTabs().add(tab);
        activeRule.possibleTranslations.add(p);
        scenarios.put(c, p);

        return new Pair<>(c, p);
    }
}
