package translator;

import model.interfaces.Subnode;
import model.interfaces.Tile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import translator.model.PopulatedTileGrid;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by vilddjur on 3/22/17.
 */
public class ExportLevelHandler {
    /**
     * saves the given grid as 'level.tmx' as path
     * @param grid
     * @param path
     */
    public static void saveGridAsTMX(PopulatedTileGrid grid, String path) {
        try{
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            //<map></map>
            Element elemMap = doc.createElement("map");
            elemMap.setAttribute("width", ""+grid.size());
            elemMap.setAttribute("height", ""+grid.size());

            doc.appendChild(elemMap);

            //<map><ground></ground></map>
            Element elemLayerGround = doc.createElement("ground");
            elemLayerGround.setAttribute("name", "ground");
            elemLayerGround.setAttribute("width", ""+grid.size());
            elemLayerGround.setAttribute("height", ""+grid.size());
            elemMap.appendChild(elemLayerGround);

            //Monsters, traps, locks, keys
            Element elemLayerInteractables = doc.createElement("interactables");
            elemLayerInteractables.setAttribute("name", "interactables");
            elemLayerInteractables.setAttribute("width", ""+grid.size());
            elemLayerInteractables.setAttribute("height", ""+grid.size());
            elemMap.appendChild(elemLayerInteractables);
            //<map><ground><tile /> <tile /> .. </ground></map>
            for (int y = 0; y < grid.size(); y++) {
                for (int x = 0; x < grid.size(); x++) {
                    Tile tile = grid.getTile(x,y);
                    Element elemTile = doc.createElement("tile");

                    elemTile.setAttribute("type", tile.getTILE_TYPE().toString());
                    elemTile.setAttribute("x", ""+x);
                    elemTile.setAttribute("y", ""+y);
                    elemLayerGround.appendChild(elemTile);

                    Subnode object = grid.getObject(x,y);
                    if(object != null){
                        Element elemObj = doc.createElement("tile");
                        elemObj.setAttribute("type", object.getType().toString());
                        elemObj.setAttribute("x", ""+x);
                        elemObj.setAttribute("y", ""+y);
                        elemLayerInteractables.appendChild(elemObj);
                    }

                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            //formatting for human readability.
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Result output = new StreamResult(new File(path));
            Source input = new DOMSource(doc);

            transformer.transform(input, output);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
