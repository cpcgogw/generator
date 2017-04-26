package model.implementations;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.interfaces.AreaNode;
import utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by vilddjur on 4/25/17.
 */
public class Quest extends Circle {
    public static int idCounter = 0;
    private int id;

    private AreaNode parent;

    private List<Quest> prerequisites;
    private List<Objective> objectives;
    private List<Line> objevtiveConnections = new ArrayList<>();

    public Quest() {
        this(idCounter++);
    }

    public Quest(int id) {
        this(id, new ArrayList<>());
    }

    public Quest(int id, List<Quest> prerequisites) {
        this(id, prerequisites, new ArrayList<>());
    }

    public Quest(int id, List<Quest> prerequisites, List<Objective> objectives) {
        this(id, prerequisites, objectives, null);
    }

    public Quest(int id, List<Quest> prerequisites, List<Objective> objectives, AreaNode parent) {
        this.id = id;
        this.prerequisites = prerequisites;
        this.objectives = objectives;
        this.parent = parent;

        this.setRadius(20);
        // Because poop, he he
        this.setFill(Color.CHOCOLATE);
    }

    public void addPrerequisite(Quest quest) {
        prerequisites.add(quest);
    }

    public List<Quest> getPrerequisites() {
        return prerequisites;
    }

    public void addObjective(Objective objective) {
        Line line = new Line();
        line.setFill(Color.BLACK);
        line.setStrokeWidth(2);
        line.setStartX(getCenterX());
        line.setStartY(getCenterY());
        line.setEndX(objective.getCenterX());
        line.setEndY(objective.getCenterY());
        objevtiveConnections.add(line);
        objectives.add(objective);
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    public int getQuestId() {
        return id;
    }

    public AreaNode getParentNode() {
        return parent;
    }

    public void setParentNode(AreaNode parent) {
        this.parent = parent;
    }

    public void update(Integer placed) {
        if (parent != null && parent instanceof DrawableAreaNode) {
            this.setCenterX(((DrawableAreaNode) parent).getCenterX() + Math.cos(Math.toRadians(50*placed))*60);
            this.setCenterY(((DrawableAreaNode) parent).getCenterY() + Math.sin(Math.toRadians(50*placed))*60);
        }
    }

    public List<Line> getConnections() {
        return objevtiveConnections;
    }

    @Override
    public String toString() {
        String s = "";
        s += "ID: " + id + "\n";
        s += "Parent: " + parent.getNodeId() + "\n";
        s += "Prerequisites: ";
        for (Quest quest : prerequisites) {
            s += quest.getQuestId() + ", ";
        }
        s += "\nObjectives: ";
        for (Objective objective : objectives) {
            s += objective + ", ";
        }
        s += "\n";

        return s;
    }
}
