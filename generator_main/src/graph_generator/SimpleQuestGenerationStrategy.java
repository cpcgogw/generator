package graph_generator;

import model.enums.OBJECTIVE_TYPE;
import model.implementations.Objective;
import model.implementations.Quest;
import model.interfaces.AreaNode;
import model.interfaces.Edge;
import model.interfaces.Node;
import utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by vilddjur on 4/25/17.
 */
public class SimpleQuestGenerationStrategy implements QuestGenerationStrategy {

    @Override
    public List<Quest> generate(List<? extends AreaNode> nodes) {
        List<Quest> quests = new ArrayList<>();

        List<List<? extends AreaNode>> cycles = getAllCycles(nodes);
        Log.print("Cycles found: "+cycles.size(), Log.LEVEL.DEBUG);

        int i = 0;
        for (List<? extends AreaNode> cycle : cycles) {
            Log.print("Cycle "+i++, Log.LEVEL.DEBUG);

            String s = "";
            for (AreaNode node : cycle) {
                s += node.getNodeId() + ", ";
            }
            if (cycle.size() > 0)
                s = s.substring(0, s.length()-2);
            Log.print(s, Log.LEVEL.DEBUG);
            Log.print("", Log.LEVEL.DEBUG);
        }

        List<Quest> questList = placeQuests(cycles);

        /*for (Quest q : questList) {
            Log.tmpPrint(q);
        }*/

        return questList;
    }

    private List<Quest> placeQuests(List<List<? extends AreaNode>> cycles) {
        final int numOfQuests = 5;
        List<Quest> quests = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numOfQuests; i++) {
            Quest quest = placeQuest(cycles.get(random.nextInt(cycles.size())));
            quests.add(quest);
            Log.tmpPrint(quest);
        }

        return quests;
    }

    private Quest placeQuest(List<? extends AreaNode> nodes) {
        Random random = new Random();
        final int maxProb = 100;
        int div = 1;
        Quest quest = new Quest();
        quest.setParentNode(nodes.get(0));

        for (AreaNode node : nodes) {
            // Add objectives randomly
            if (random.nextInt(maxProb) < maxProb/div) {
                div++;
                int rand = random.nextInt(maxProb);
                if (rand < 33) {
                    quest.addObjective(new Objective(node, OBJECTIVE_TYPE.ITEM));
                } else if (rand < 66) {
                    quest.addObjective(new Objective(node, OBJECTIVE_TYPE.MONSTER));
                } else {
                    quest.addObjective(new Objective(node, OBJECTIVE_TYPE.BOSS));
                }
            }
        }

        return quest;
    }

    private List<List<? extends AreaNode>> getAllCycles(List<? extends AreaNode> nodes) {
        List<List<? extends AreaNode>> cycles = new ArrayList<>();

        int maxJumps = 10;
        int minJumps = 2;

        for (AreaNode node : nodes) {
            Log.print("Trying to find cycles starting from node: "+node.getNodeId(), Log.LEVEL.DEBUG);
            List<List<? extends AreaNode>> nodeCycles = getAllCycles(node, new ArrayList<>(), 0, minJumps, maxJumps);
            Log.print("Number of cycles found: "+nodeCycles.size(), Log.LEVEL.DEBUG);

            if (!nodeCycles.isEmpty()) {
                cycles.addAll(nodeCycles);
            }
        }

        Log.print("Total number of cycles found: "+cycles.size(), Log.LEVEL.DEBUG);

        return cycles;
    }

    private List<List<? extends AreaNode>> getAllCycles(AreaNode node,
                                                        ArrayList<AreaNode> checkedNodes,
                                                        int jumps,
                                                        int minJumps,
                                                        int maxJumps) {
        String s = "Checked:  ";

        for (AreaNode node1 : checkedNodes) {
            s += node1.getNodeId() + ", ";
        }

        Log.print("Checking: " + node.getNodeId(), Log.LEVEL.DEBUG);
        Log.print(s, Log.LEVEL.DEBUG);
        Log.print("", Log.LEVEL.DEBUG);

        List<List<? extends AreaNode>> cycles = new ArrayList<>();

        if (jumps > maxJumps) {
            return null;
        }

        if (!checkedNodes.isEmpty() && node == checkedNodes.get(0)) {
            if (jumps < minJumps) {
                return null;
            }

            checkedNodes.add(node);
            cycles.add(checkedNodes);
            return cycles;
        }

        if (checkedNodes.contains(node)) {
            return null;
        }

        jumps++;
        checkedNodes.add(node);

        // For all neighbours check for possible cycle
        for (Edge edge : node.getEdges()) {
            if (node.getNodeId() == edge.getFrom().getNodeId()) {
                // Node is from
                ArrayList<AreaNode> copiedCheckedNodes = (ArrayList<AreaNode>) checkedNodes.clone();
                List<List<? extends AreaNode>> nextCycles = getAllCycles((AreaNode) edge.getTo(), copiedCheckedNodes, jumps, minJumps, maxJumps);
                if (nextCycles != null)
                    cycles.addAll(nextCycles);
            } else {
                // Node is to
                ArrayList<AreaNode> copiedCheckedNodes = (ArrayList<AreaNode>) checkedNodes.clone();
                List<List<? extends AreaNode>> nextCycles = getAllCycles((AreaNode) edge.getFrom(), copiedCheckedNodes, jumps, minJumps, maxJumps);
                if (nextCycles != null)
                    cycles.addAll(nextCycles);
            }
        }

        return cycles;
    }
}
