package model.implementations;

import javafx.scene.shape.Circle;
import model.interfaces.AreaNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 4/25/17.
 */
public class Quest extends Circle {
    public static int idCounter = 0;
    private int id;

    private AreaNode parent;

    private List<Quest> prerequisites;
    private List<Objective> objectives;

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
        this.update();
    }

    public void addPrerequisite(Quest quest) {
        prerequisites.add(quest);
    }

    public List<Quest> getPrerequisites() {
        return prerequisites;
    }

    public void addObjective(Objective objective) {
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

    public void update() {
        if(parent != null && parent instanceof DrawableAreaNode){
            this.setCenterX(((DrawableAreaNode) parent).getCenterX() + 40);
            this.setCenterY(((DrawableAreaNode) parent).getCenterY() + 40);
        }
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
        s += "\n";
        for (Objective objective : objectives) {
            s += objective.getType() + ", ";
        }

        return s;
    }
}
