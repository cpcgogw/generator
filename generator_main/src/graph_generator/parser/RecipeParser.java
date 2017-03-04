package graph_generator.parser;

import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.Log;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by time on 3/3/17.
 */
public class RecipeParser {
    private Document document = null;

    /**
     * Parses and executes a single recipe.
     *
     * @param file
     * Recipe to parse and execute.
     */
    public void parseRecipe(String file) {
        Log.level = Log.LEVEL.DEBUG;
        try {
            Log.print("Begin parsing recipe at "+file, Log.LEVEL.INFO);
            File fxmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(fxmlFile);
            document.getDocumentElement().normalize();
            NodeList nl = document.getElementsByTagName("command");

            Log.print("Root: " + document.getDocumentElement().getNodeName(), Log.LEVEL.DEBUG);

            for (int j=0; j<nl.getLength() && nl.item(j) instanceof Element; j++) {
                Element e = (Element)nl.item(j);
                NodeList e2 = e.getElementsByTagName("name");
                Log.print("Command: "+e2.item(0).getTextContent(), Log.LEVEL.DEBUG);

                NodeList nl2 = e.getElementsByTagName("params");
                Element e4 = ((Element)nl2.item(0));
                NodeList e3 = e4.getChildNodes();

                removeNewlines(e3);

                for (int i = 0; i < e3.getLength(); i++) {
                    Log.print("Param"+(1+i)+": "+e3.item(i).getTextContent(), Log.LEVEL.DEBUG);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Formats a NodeList for easier parsing.
     *
     * @param nl
     * NodeList to remove whitespaces and newlines from.
     */
    private void removeNewlines(NodeList nl) {
        for (int i=0; i<nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.TEXT_NODE)
                nl.item(i).getParentNode().removeChild(nl.item(i));
        }
    }

    public boolean tryExecute(String text) {

        return false;
    }

    public static void main(String[] args) {
        RecipeParser rp = new RecipeParser();
        rp.parseRecipe("generator_main/resources/sample_recipe.r");
    }
}
