package rule_editor.controller;

import graph_generator.controller.QuestGenerator;
import model.implementations.DrawableAreaNode;
import model.implementations.Objective;
import model.implementations.Quest;

import java.util.ArrayList;
import java.util.List;

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

    public void update(){
        for (Quest quest : quests) {
            quest.update();
            for (Objective objective : quest.getObjectives()) {
                objective.update();
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
