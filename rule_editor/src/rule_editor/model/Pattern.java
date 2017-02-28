package rule_editor.model;

import javafx.util.Pair;
import rule_editor.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vilddjur on 1/28/17.
 */
public class Pattern {
    public ArrayList<Node> nodes;
    private Random random;
    public Pattern(){
        nodes = new ArrayList<Node>();
        random = new Random();
    }
    public Pattern(Pair<ArrayList<Node>,ArrayList<Edge>> pair){
        nodes = new ArrayList<>();
        nodes.addAll(pair.getKey());
        random = new Random();
    }
    @Override
    public int hashCode() {
        return super.hashCode()+nodes.hashCode()*3;
    }

    @Override
    public boolean equals(Object o) {
            if(o instanceof Pattern){
                Pattern tmp = (Pattern) o;
                return nodes.equals(tmp.nodes); // maybe sort lists
            }else{
                return false;
            }
    }
    public void findAndReplaceNTimes(ArrayList<Rule> rules, int n){
        for (int i = 0; i < n; i++) {
            findAndReplace(rules);
        }
    }

    public void findAndReplace(ArrayList<Rule> rules) {

        ArrayList<Pair<Rule, Pattern>> rulePatternList = new ArrayList<>();
        for (Rule r : rules) {
            for (int i = 0; i < nodes.size(); i++) {

                Log.print("checking subpattern:", Log.LEVEL.DEBUG);
                Log.print(" Type: " + nodes.get(i).getType() + ", id:" + nodes.get(i).getNodeId() + ", #edges: " + nodes.get(i).getEdges().size(), Log.LEVEL.DEBUG);
                Log.print("vs", Log.LEVEL.DEBUG);
                for (Node n : r.matchingPattern.nodes) {
                    Log.print(" Type: " + n.getType() + ", id:" + n.getNodeId() + ", #edges: " + n.getEdges().size(), Log.LEVEL.DEBUG);
                }

                Pattern p = new Pattern();
                boolean result = r.nodeContainsSubPattern(nodes.get(i), new ArrayList<Node>(), p);

                if(result){
                    rulePatternList.add(new Pair<>(r,p));
                }

                Log.print(""+result, Log.LEVEL.DEBUG);
                Log.print("found: ", Log.LEVEL.DEBUG);
                for (Node n : p.nodes) {
                    Log.print(" Type: " + n.getType() + ", id:" + n.getNodeId() + ", #edges: " + n.getEdges().size(), Log.LEVEL.DEBUG);
                }
            }
        }

        if(rulePatternList.size()>0){
            Pair<Rule,Pattern> pair = rulePatternList.get(random.nextInt(rulePatternList.size()));
            Rule r = pair.getKey();
            Pattern p = pair.getValue();
            this.resetIds();
            r.execute(this,p);
        }
    }

    public void resetIds() {
        for (Node node :
                nodes) {
            node.setNodeId((node.getNodeId()+3)* 2);
        }
    }
}
