package graph_generator.utils;

import model.Node;
import model.Pattern;

/**
 * Created by vilddjur on 3/2/17.
 */
public class GraphLogger {
    public static String patternToString(Pattern pattern){
        String ret = "Pattern : \n";
        for (Node n :
                pattern.nodes) {
            ret += " node; " + nodeToString(n) + "\n";
        }
        return ret;
    }
    public static String nodeToString(Node node) {
        return "Type: " + node.getType() + ", id:" + node.getNodeId() + ", #edges: " + node.getEdges().size();
    }
}
