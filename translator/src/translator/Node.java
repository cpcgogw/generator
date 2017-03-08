package translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by time on 3/8/17.
 */
public class Node {
    public static int idCounter = 1;
    private int id;
    public List<Edge> edges = new ArrayList<>();

    public Node() {
        id = idCounter;
        idCounter++;
    }

    public int getId() {
        return id;
    }
}
