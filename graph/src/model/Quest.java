package model;

import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 4/25/17.
 */
public class Quest extends Circle {
    public static int idCounter = 0;
    private int id;

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
        this.id = id;
        this.prerequisites = prerequisites;
        this.objectives = objectives;
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

}
