package graph_generator.controller;

import graph_generator.QuestGenerationStrategy;
import graph_generator.SimpleQuestGenerationStrategy;
import model.implementations.Quest;
import model.interfaces.AreaNode;

import java.util.List;

/**
 * Created by vilddjur on 4/25/17.
 */
public class QuestGenerator {
    private static QuestGenerator instance;
    private QuestGenerationStrategy strategy;
    public static QuestGenerator getInstance() {
        if (instance == null) {
            instance = new QuestGenerator();
        }
        return instance;
    }

    private QuestGenerator(){
        strategy = new SimpleQuestGenerationStrategy();
    }

    public List<Quest> generate(List<? extends AreaNode> nodes) {
        return strategy.generate(nodes);
    }
}
