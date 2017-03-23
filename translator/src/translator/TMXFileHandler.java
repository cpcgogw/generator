package translator;

import model.ObjectNode;
import model.TILE_TYPE;
import model.Tile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import translator.model.PopulatedTileGrid;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by vilddjur on 3/22/17.
 */
public class TMXFileHandler {
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
            elemMap.setAttribute("version", "1.0");
            elemMap.setAttribute("orientation", "orthogonal");
            elemMap.setAttribute("width", ""+grid.size());
            elemMap.setAttribute("height", ""+grid.size());
            elemMap.setAttribute("tilewidth", "32");
            elemMap.setAttribute("tileheight", "32");
            doc.appendChild(elemMap);
            //<map><tileset></tileset></map>
            Element elemTileSet = doc.createElement("tileset");
            elemTileSet.setAttribute("firstgid", "1");
            elemTileSet.setAttribute("name","simple_tileset");
            elemTileSet.setAttribute("tilewidth", "32");
            elemTileSet.setAttribute("tileheight", "32");
            elemMap.appendChild(elemTileSet);

            //<map><tileset><image /></tileset></map>
            Element elemImage = doc.createElement("image");
            elemImage.setAttribute("source", "simple_tileset.png");
            elemImage.setAttribute("width", "96");
            elemImage.setAttribute("height", "32");
            elemTileSet.appendChild(elemImage);

            //<map><layer></layer></map>
            Element elemLayer = doc.createElement("layer");
            elemLayer.setAttribute("name", "ground");
            elemLayer.setAttribute("width", ""+grid.size());
            elemLayer.setAttribute("height", ""+grid.size());
            elemMap.appendChild(elemLayer);

            Element elemLayerObj = doc.createElement("layer");
            elemLayerObj.setAttribute("name", "objects");
            elemLayerObj.setAttribute("width", ""+grid.size());
            elemLayerObj.setAttribute("height", ""+grid.size());
            elemMap.appendChild(elemLayerObj);

            //<map><layer><data><tile /> <tile /> .. </data></layer></map>
            Element elemData = doc.createElement("data");
            Element elemDataObj = doc.createElement("data");
            for (int y = 0; y < grid.size(); y++) {
                for (int x = 0; x < grid.size(); x++) {
                    Tile tile = grid.getTile(x,y);
                    Element elemTile = doc.createElement("tile");
                    int gid = 0;
                    if(tile == null) {
                        gid = 0;
                    }else if(tile.getTILE_TYPE() == TILE_TYPE.ROAD){
                        gid = 2;
                    }else if(tile.getTILE_TYPE() == TILE_TYPE.TOWN){
                        gid = 3;
                    }else if(tile.getTILE_TYPE() == TILE_TYPE.MONSTER){
                        gid = 4;
                    }else if(tile.getTILE_TYPE() == TILE_TYPE.DANGER){
                        gid = 5;
                    }else if(tile.getTILE_TYPE() == TILE_TYPE.TRAP){
                        gid = 6;
                    }
                    elemTile.setAttribute("gid", ""+gid);
                    elemData.appendChild(elemTile);

                    Element elemObj = doc.createElement("tile");
                    ObjectNode object = grid.getObject(x,y);
                    if(object == null) {
                        gid = 0;
                    }else if(object.getTILE_TYPE() == TILE_TYPE.ROAD){
                        gid = 2;
                    }else if(object.getTILE_TYPE() == TILE_TYPE.TOWN){
                        gid = 3;
                    }else if(object.getTILE_TYPE() == TILE_TYPE.MONSTER){
                        gid = 4;
                    }else if(object.getTILE_TYPE() == TILE_TYPE.DANGER){
                        gid = 5;
                    }else if(object.getTILE_TYPE() == TILE_TYPE.TRAP){
                        gid = 6;
                    }
                    elemObj.setAttribute("gid", ""+gid);
                    elemDataObj.appendChild(elemObj);

                }
            }
            elemLayer.appendChild(elemData);
            elemLayerObj.appendChild(elemDataObj);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(new File(path));
            Source input = new DOMSource(doc);

            transformer.transform(input, output);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
