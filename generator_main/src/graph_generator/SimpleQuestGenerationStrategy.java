package graph_generator;

import model.implementations.Quest;
import model.interfaces.AreaNode;
import model.interfaces.Edge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vilddjur on 4/25/17.
 */
public class SimpleQuestGenerationStrategy implements QuestGenerationStrategy {

    @Override
    public List<Quest> generate(List<? extends AreaNode> nodes) {
        List<Quest> quests = new ArrayList<>();

        Quest quest = placeQuest(null);
        if (quest != null)
            quests.add(quest);
        getAllCycles(nodes);

        return quests;
    }

    private Quest placeQuest(List<? extends AreaNode> nodes) {
        return null;
    }

    private List<List<? extends AreaNode>> getAllCycles(List<? extends AreaNode> nodes) {
        List<List<? extends AreaNode>> cycles = new ArrayList<>();
        int maxJumps = 10;
        int minJumps = 2;
        for (AreaNode node : nodes) {
            List<List<? extends AreaNode>> nodeCycles = getAllCycles(node, new ArrayList<>(), 0, minJumps, maxJumps);
            if (nodeCycles != null)
                cycles.addAll(nodeCycles);
        }
        return cycles;
    }

    private List<List<? extends AreaNode>> getAllCycles(AreaNode node,
                                                        ArrayList<AreaNode> checkedNodes,
                                                        int jumps,
                                                        int maxJumps,
                                                        int minJumps) {
        List<List<? extends AreaNode>> cycles = new ArrayList<>();

        if (jumps > maxJumps) {
            return null;
        }

        if (!checkedNodes.isEmpty() && node == checkedNodes.get(0)) {
            if (jumps < minJumps) {
                return null;
            }

            cycles.add(checkedNodes);
            return cycles;
        }

        if (checkedNodes.contains(node)) {
            return null;
        }

        jumps++;
        checkedNodes.add(node);

        for (Edge edge : node.getEdges()) {
            if (node == edge.getFrom()) { // node is to
                ArrayList<AreaNode> copiedCheckedNodes = (ArrayList<AreaNode>) checkedNodes.clone();
                List<List<? extends AreaNode>> nextCycles = getAllCycles((AreaNode) edge.getTo(), copiedCheckedNodes, jumps, maxJumps, minJumps);
                if (nextCycles != null)
                    cycles.addAll(nextCycles);
            } else { // node is from
                ArrayList<AreaNode> copiedCheckedNodes = (ArrayList<AreaNode>) checkedNodes.clone();
                List<List<? extends AreaNode>> nextCycles = getAllCycles((AreaNode) edge.getFrom(), copiedCheckedNodes, jumps, maxJumps, minJumps);
                if (nextCycles != null)
                    cycles.addAll(nextCycles);
            }
        }

        return cycles;
    }
}
