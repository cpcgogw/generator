package graph_generator;

import model.implementations.Quest;
import model.interfaces.AreaNode;

import java.util.List;

/**
 * Created by vilddjur on 4/25/17.
 */
public interface QuestGenerationStrategy {
    List<Quest> generate(List<? extends AreaNode> nodes);
}
