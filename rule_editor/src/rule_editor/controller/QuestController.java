package rule_editor.controller;

import graph_generator.controller.QuestGenerator;
import model.implementations.DrawableAreaNode;
import model.implementations.Quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 4/25/17.
 */
public class QuestController {
    private List<Quest> questList;

    private static QuestController instance = null;
    private QuestGenerator questGenerator;

    public static QuestController getInstance() {
        if(instance == null){
            instance = new QuestController();
        }
        return instance;
    }

    private QuestController() {
        questList = new ArrayList<>();
        questGenerator = QuestGenerator.getInstance();
    }

    public void addQuest(Quest quest){
        questList.add(quest);
    }

    public void update(){
        for (Quest quest : questList) {
            quest.update();
        }
    }

    public void generate(List<DrawableAreaNode> nodes) {
        List<Quest> quests = questGenerator.generate(nodes);
    }
}
