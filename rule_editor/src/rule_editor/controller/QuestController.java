package rule_editor.controller;

import graph_generator.controller.QuestGenerator;
import javafx.scene.shape.Line;
import model.implementations.DrawableAreaNode;
import model.implementations.Objective;
import model.implementations.Quest;
import utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vilddjur on 4/25/17.
 */
public class QuestController {
    private List<Quest> quests;

    private static QuestController instance = null;
    private QuestGenerator questGenerator;

    public static QuestController getInstance() {
        if (instance == null) {
            instance = new QuestController();
        }
        return instance;
    }

    private QuestController() {
        quests = new ArrayList<>();
        questGenerator = QuestGenerator.getInstance();
    }

    public void addQuest(Quest quest){
        quests.add(quest);
    }

    //TODO: Discuss if possibly better to do this somewhere else
    public void update() {
        Map<Integer, Integer> placedQuests = new HashMap<>();
        Map<Integer, Integer> placedObjectives = new HashMap<>();
        for (Quest quest : quests) {
            // If no quest has been placed at parent node before, set placed to 0
            if (!placedQuests.containsKey(quest.getParentNode().getNodeId()))
                placedQuests.put(quest.getParentNode().getNodeId(), 0);

            // Increment number of placed quests for node
            int i = placedQuests.get(quest.getParentNode().getNodeId());
            placedQuests.put(quest.getParentNode().getNodeId(), i+1);

            //TODO: Perform better matching of edges/discuss how edges should be handled.
            double x = quest.getCenterX();
            double y = quest.getCenterY();

            // Place quest
            quest.update(placedQuests.get(quest.getParentNode().getNodeId()));

            // Update the objectives for this quest in a similar manner
            for (Objective objective : quest.getObjectives()) {
                double dx = objective.getCenterX();
                double dy = objective.getCenterY();
                Line connection = null;

                //TODO: Should be handled in a better way.
                //NOTE: This can fail if two or more quest-objective pairs overlap.
                // Match the line connecting the quest and the respective objective.
                for (Line line : quest.getConnections()) {
                    if (line.getStartX() == x &&
                            line.getStartY() == y &&
                            line.getEndX() == dx &&
                            line.getEndY() == dy) {
                        connection = line;
                    }
                }

                // If no objective has been placed at parent node before, set placed to 0
                if (!placedObjectives.containsKey(objective.getParentNode().getNodeId()))
                    placedObjectives.put(objective.getParentNode().getNodeId(), 0);

                // Increment number of placed objectives for node
                int j = placedObjectives.get(objective.getParentNode().getNodeId());
                placedObjectives.put(objective.getParentNode().getNodeId(), i+1);
                objective.update(placedObjectives.get(objective.getParentNode().getNodeId()));

                // Correct quest-objective connections
                connection.setStartX(quest.getCenterX());
                connection.setStartY(quest.getCenterY());
                connection.setEndX(objective.getCenterX());
                connection.setEndY(objective.getCenterY());
            }
        }
    }

    public void generate(List<DrawableAreaNode> nodes) {
        quests = questGenerator.generate(nodes);
    }

    public List<Quest> getQuests() {
        return quests;
    }
}
