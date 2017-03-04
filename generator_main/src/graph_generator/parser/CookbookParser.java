package graph_generator.parser;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
 * Used to parse a cookbook file.
 *
 * Created by time on 3/3/17.
 */
public class CookbookParser {
    private Document document = null;

    public void parseCookbook(String file) {
        try {
            File fxmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            document = dBuilder.parse(fxmlFile);

            document.getDocumentElement().normalize();
            System.out.println("Root element: " + document.getDocumentElement().getNodeName());
            NodeList nl = document.getElementsByTagName("command");

            for (int i=0; i<nl.getLength() && tryExecute(nl.item(i).getTextContent()); i++) {
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public boolean tryExecute(String text) {

        return false;
    }

    public static void main(String[] args) {
        CookbookParser cbp = new CookbookParser();
        cbp.parseCookbook("generator_main/resources/sample_cookbook.cb");
    }
}
