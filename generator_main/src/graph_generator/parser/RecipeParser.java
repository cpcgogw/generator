package graph_generator.parser;

import graph_generator.controller.CommandController;
import model.DrawablePattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.Log;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for parsing and applying .r xml Recipe files on a graph.
 *
 * Created by time on 3/3/17.
 */
public class RecipeParser {
    private Document document = null;
    Command command = null;
    CommandController controller = CommandController.getInstance();

    /**
     * Parses and executes a single recipe.
     *
     * @param file
     * Recipe to parse and execute.
     */
    public boolean parseRecipe(String file, DrawablePattern graph) {
        String tmp = Log.prefix;
        Log.prefix = "RecipeParser: ";

        Log.print("Begin parsing recipe at "+file, Log.LEVEL.INFO);
        file = "generator_main/resources/" +file;
        boolean result = true;

        try {
            File fxmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(fxmlFile);
            document.getDocumentElement().normalize();
            NodeList commands = document.getElementsByTagName("command");

            Log.print("Root: " + document.getDocumentElement().getNodeName(), Log.LEVEL.DEBUG);

            //For each command, try parse, then execute if parse successful
            for (int j=0; j<commands.getLength() && result; j++) {
                Element commandElement = (Element) commands.item(j);
                NodeList name = commandElement.getElementsByTagName("name");
                Log.print("Command: " + name.item(0).getTextContent(), Log.LEVEL.DEBUG);

                //Parse command from name
                Command command = CommandController.getInstance().getCommand(name.item(0).getTextContent());
                if (command == null) {
                    Log.print("Command not found. Found \"" + command + "\" instead.", Log.LEVEL.ERROR);
                    Log.prefix = tmp;
                    return false;
                }

                Log.print("Parsed command: " + command.getName(), Log.LEVEL.DEBUG);

                NodeList params = commandElement.getElementsByTagName("params");
                Element e4 = ((Element) params.item(0));
                NodeList e3 = e4.getChildNodes();
                List<String> paraList = new ArrayList<>();

                removeNewlines(e3);

                for (int i = 0; i < e3.getLength(); i++) {
                    paraList.add(e3.item(i).getTextContent());
                    Log.print("Param" + (1 + i) + ": " + e3.item(i).getTextContent(), Log.LEVEL.DEBUG);
                }

                command.setParameters(paraList);
                result = command.execute(graph);
                Log.print("Command \""+command.getName()+"\" was executed "+(result ? "successfully." : "with failure."), Log.LEVEL.DEBUG);
                if (!result) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.prefix = tmp;
            return false;
        }

        Log.prefix = tmp;
        return true;
    }

    public boolean execute() {
        Log.print("Starting execution of command: ", Log.LEVEL.INFO);
        return false;
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
}
