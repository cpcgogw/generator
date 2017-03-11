package graph_generator.parser;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import model.DrawablePattern;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import utils.Log;


/**
 * Used to parse a cookbook file.
 *
 * Created by time on 3/3/17.
 */
public class CookbookParser {
    private Document document = null;
    private RecipeParser recipeParser = new RecipeParser();

    public boolean parseCookbook(String file, DrawablePattern graph) {
        String tmp = Log.prefix;
        Log.prefix = "CookbookParser: ";

        file = "generator_main/resources/" +file;
        Log.print("Parsing cookbook located at "+file, Log.LEVEL.INFO);

        try {
            File fxmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            document = dBuilder.parse(fxmlFile);

            document.getDocumentElement().normalize();
            Log.print("Root element: " + document.getDocumentElement().getNodeName(), Log.LEVEL.DEBUG);
            NodeList recipes = document.getElementsByTagName("recipe");

            Log.print("Parsing and executing recipes.", Log.LEVEL.INFO);
            Log.print("Number of recipes: "+recipes.getLength(), Log.LEVEL.DEBUG);
            for (int i=0; i<recipes.getLength(); i++) {
                Log.print("Recipe"+(i+1)+": "+recipes.item(i).getTextContent(), Log.LEVEL.DEBUG);
                if (!recipeParser.parseRecipe(recipes.item(i).getTextContent(), graph)) {
                    Log.print("Recipe: "+recipes.item(i).getTextContent()+" failed to parse.", Log.LEVEL.ERROR);
                    Log.prefix = tmp;
                    return false;
                } else {
                    Log.print("Recipe: "+recipes.item(i).getTextContent()+" was applied successfully.", Log.LEVEL.INFO);
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
}
