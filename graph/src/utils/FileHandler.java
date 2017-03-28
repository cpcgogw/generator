package utils;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is to save and/or store drawableAreaNodes & edges
 *
 *
 */

public class FileHandler {

    public static void saveNodes(ArrayList<? extends AreaNode> areaNodes, String path) {

        try {

            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            Element elemNodes = doc.createElement("Nodes");

            doc.appendChild(elemNodes);

            insertNodesInto(areaNodes, elemNodes, doc);
            saveDocAt(doc, path);

        } catch (TransformerException ex) {
            System.out.println("Error outputting document");

        } catch (ParserConfigurationException ex) {
            System.out.println("Error building document");
        }
    }

    private static void saveDocAt(Document doc, String path) throws TransformerConfigurationException {
        /* Saves File at specific directory
            */
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        //formatting for human readability.
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        try {
            FileWriter fileWriter = new FileWriter(path);
            StreamResult streamResult = new StreamResult(fileWriter);
            transformer.transform(source, streamResult);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Pair<ArrayList<DrawableAreaNode>,ArrayList<DrawableEdge>> loadNodes(File file){
        HashMap<Integer, model.Node> nodeMap = new HashMap<>();
        ArrayList<DrawableEdge> drawableEdges = new ArrayList<>();

        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize(); //normalizes document


            //Defines all drawableAreaNodes...

            NodeList xnodeList = doc.getElementsByTagName("AreaNode"); //grab all "AreaNode" from XML-file
            nodeMap = extractNodes(xnodeList);



            //Defines all drawableEdges...
            HashMap<Integer,ArrayList<Integer>> edgeMap = new HashMap<>(); //HashMap used for easy fix of duplicate drawableEdges.
            xnodeList = doc.getElementsByTagName("Edge");//grab all "Edge" from XML-file
            edgeMap = extractEdges(xnodeList, nodeMap, drawableEdges);


        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfigurationException: ");
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("SAXException: ");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException: ");
            e.printStackTrace();
        }

        //Returns the entries as an ArrayList
        Pair pair = new Pair(new ArrayList<>(nodeMap.values()), drawableEdges);
        return pair;
    }

    private static HashMap<Integer, ArrayList<Integer>> extractEdges(NodeList xnodeList, HashMap<Integer, model.Node> nodeMap, ArrayList<DrawableEdge> drawableEdges) {
        HashMap<Integer,ArrayList<Integer>> edgeMap = new HashMap<>(); //HashMap used for easy fix of duplicate drawableEdges.
        for(int i = 0; i < xnodeList.getLength(); i++){
            Node xNode = xnodeList.item(i);

            if (xNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) xNode;

                //Extract elements of node
                int startID = Integer.parseInt(element.getElementsByTagName("StartID").item(0).getTextContent());
                int endID =  Integer.parseInt(element.getElementsByTagName("EndID").item(0).getTextContent());
                if(edgeMap.get(startID)==null) {
                    edgeMap.put(startID, new ArrayList<Integer>());
                }
                if (!edgeMap.get(startID).contains(endID)) {
                    //Store the extracted DrawableEdge
                    model.Node startNode = nodeMap.get(startID);
                    model.Node endNode = nodeMap.get(endID);
                    if(startNode instanceof DrawableAreaNode && endNode instanceof DrawableAreaNode){
                        drawableEdges.add(new DrawableEdge((DrawableAreaNode) startNode, (DrawableAreaNode) endNode));
                    }
                    //TODO: add object edges and shiz here somewhere

                    edgeMap.get(startID).add(endID);
                }
            }
        }
        return edgeMap;
    }

    public static ArrayList<Pair<ArrayList<DrawableAreaNode>, ArrayList<DrawableEdge>>> loadTranslations(File file) {
        ArrayList<Pair<ArrayList<DrawableAreaNode>, ArrayList<DrawableEdge>>> translations = new ArrayList<>();
        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize(); //normalizes document


            ArrayList<NodeList> translationNodes = extractTranslations(doc);
            //foreach translation
            for (NodeList list : translationNodes) {
                //insert into translations
                HashMap<Integer, model.Node> nodeMap = new HashMap<>();
                ArrayList<DrawableEdge> drawableEdges = new ArrayList<>();


                NodeList xnodeList = ((Element) list).getElementsByTagName("AreaNode"); //grab all "AreaNode" from matchingDrawablePattern
                nodeMap = extractNodes(xnodeList);

                //Defines all drawableEdges...
                HashMap<Integer,ArrayList<Integer>> edgeMap = new HashMap<>(); //HashMap used for easy fix of duplicate drawableEdges.
                xnodeList = ((Element) list).getElementsByTagName("Edge");//grab all "Edge" from XML-file
                edgeMap = extractEdges(xnodeList, nodeMap, drawableEdges);

                Pair pair = new Pair(new ArrayList<>(nodeMap.values()), drawableEdges);
                translations.add(pair);
            }
        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfigurationException: ");
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("SAXException: ");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException: ");
            e.printStackTrace();
        }
        return translations;
    }

    private static ArrayList<NodeList> extractTranslations(Document doc) {
        ArrayList<NodeList> nodeListList = new ArrayList<NodeList>();
        Element posTrans = (Element) doc.getElementsByTagName("PossibleTranslations").item(0);
        NodeList patterns = posTrans.getElementsByTagName("Pattern");
        for (int i = 0; i < patterns.getLength(); i++) {
            nodeListList.add((NodeList) patterns.item(i));
        }
        return nodeListList;
    }

    public static Pair<ArrayList<DrawableAreaNode>, ArrayList<DrawableEdge>> loadMatchingPattern(File file) {
        HashMap<Integer, model.Node> nodeMap = new HashMap<>();
        ArrayList<DrawableEdge> drawableEdges = new ArrayList<>();

        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize(); //normalizes document

            //Defines all drawableAreaNodes...
            Node pattern = doc.getElementsByTagName("MatchingPattern").item(0);
            NodeList xnodeList = ((Element) pattern).getElementsByTagName("AreaNode"); //grab all "AreaNode" from matchingDrawablePattern
            nodeMap = extractNodes(xnodeList);

            //Defines all drawableEdges...
            HashMap<Integer,ArrayList<Integer>> edgeMap = new HashMap<>(); //HashMap used for easy fix of duplicate drawableEdges.
            xnodeList = ((Element) pattern).getElementsByTagName("Edge");//grab all "Edge" from XML-file
            edgeMap = extractEdges(xnodeList, nodeMap, drawableEdges);


        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfigurationException: ");
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("SAXException: ");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException: ");
            e.printStackTrace();
        }

        //Returns the entries as an ArrayList
        Pair pair = new Pair(new ArrayList<>(nodeMap.values()), drawableEdges);
        return pair;
    }

    private static HashMap<Integer, model.Node> extractNodes(NodeList xnodeList) {
        HashMap<Integer, model.Node> nodeMap = new HashMap<>();
        for(int i = 0; i < xnodeList.getLength(); i++){
            Node xNode = xnodeList.item(i);

            if (xNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) xNode;

                //Extract elements of drawableAreaNode
                int id = Integer.parseInt(element.getElementsByTagName("ID").item(0).getTextContent());
                AREA_TYPE type = AREA_TYPE.valueOf(element.getElementsByTagName("Tag").item(0).getTextContent());
                double x = Double.parseDouble(element.getElementsByTagName("X").item(0).getTextContent());
                double y = Double.parseDouble(element.getElementsByTagName("Y").item(0).getTextContent());
                //Store the extracted DrawableAreaNode
                DrawableAreaNode drawableAreaNode = new DrawableAreaNode(id,x,y, DrawableAreaNode.DEFAULT_RADIUS, Color.RED, type);

                nodeMap.put(id, drawableAreaNode);
                //extract subnodes
                NodeList subNodeList = ((Element) xNode).getElementsByTagName("SubNode");
                for (int j = 0; j < subNodeList.getLength(); j++) {
                    Node sNode = subNodeList.item(j);
                    int nodeId = Integer.parseInt(sNode.getAttributes().getNamedItem("id").getNodeValue());
                    OBJECT_TYPE objType = OBJECT_TYPE.valueOf(sNode.getAttributes().getNamedItem("type").getNodeValue());
                    SubNode node = new SubNode(nodeId, objType);
                    drawableAreaNode.addObject(node);
                    nodeMap.put(nodeId, node);
                }
            }
        }
        return nodeMap;
    }
    public static void saveRule(Rule rule, String path){
        try {
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            //<Rule></Rule>
            Element elemRule = doc.createElement("Rule");
            doc.appendChild(elemRule);

            //<Rule><MatchingPattern></MatchingPattern></Rule>
            Element elemMatchingPattern = doc.createElement("MatchingPattern");
            elemRule.appendChild(elemMatchingPattern);
            //<Rule><MatchingPattern><DrawablePattern></DrawablePattern></MatchingPattern></Rule>
            Element elemNodes = doc.createElement("Pattern");
            elemMatchingPattern.appendChild(elemNodes);
            //<Rule><MatchingPattern><DrawablePattern>...</DrawablePattern></MatchingPattern></Rule>
            insertNodesInto(rule.matchingDrawablePattern.getNodes(), elemNodes, doc);


            //<Rule><PossibleTranslations></PossibleTranslations></Rule>
            Element elemPosTranslations = doc.createElement("PossibleTranslations");
            elemRule.appendChild(elemPosTranslations);
            //<Rule><PossibleTranslations><DrawablePattern></DrawablePattern>....</PossibleTranslations></Rule>
            for (DrawablePattern p : rule.possibleTranslations) {
                Element elemSinglePattern = doc.createElement("Pattern");
                elemPosTranslations.appendChild(elemSinglePattern);
                //<Rule><PossibleTranslations><DrawablePattern>...</DrawablePattern>[..]</PossibleTranslations></Rule>
                insertNodesInto(p.getNodes(), elemSinglePattern, doc);
            }

            /* Saves File at specific directory
                */

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            //formatting for human readability.
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            try {
                File saves = new File("saves");
                File rules = new File("saves/rules");
                File levels = new File("saves/levels");
                if (!saves.exists())
                    saves.mkdir();
                if (!rules.exists())
                    rules.mkdir();
                if (!levels.exists())
                    levels.mkdir();

                FileWriter fileWriter = new FileWriter(path);
                StreamResult streamResult = new StreamResult(fileWriter);
                transformer.transform(source, streamResult);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (TransformerException ex) {
            Log.print("Error outputting document", Log.LEVEL.ERROR);
        } catch (ParserConfigurationException ex) {
            Log.print("Error building document", Log.LEVEL.ERROR);
        } catch (Exception e) {
            Log.print("Rule::save(String): There was an error saving the Rule: ", Log.LEVEL.ERROR);
            e.printStackTrace();
        }
    }
    private static void insertSubNodesInto(List<ObjectNode> objectNodes, Element elemObjects, Document doc) {
        for (ObjectNode obj : objectNodes) {
            Element elemObj = doc.createElement("SubNode");
            elemObj.setAttribute("type", obj.getType().toString());
            elemObj.setAttribute("id", ""+obj.getNodeId());
            Element elemEdges = doc.createElement("Edges");
            insertEdgesInto(obj.getEdges(), elemEdges, doc);
            elemObjects.appendChild(elemObj);
        }
    }

    private static void insertNodesInto(ArrayList<? extends AreaNode> areaNodes, Element elemNodes, Document doc) {
        //<DrawableAreaNode><ID>id</ID><Tag>tag</Tag><X>x</X><Y>y</Y></DrawableAreaNode>
        for (AreaNode areaNode : areaNodes) {
            Element elemNode = doc.createElement("AreaNode");
            elemNodes.appendChild(elemNode);

            Element elemObjects = doc.createElement("SubNodes");
            elemNode.appendChild(elemObjects);
            insertSubNodesInto(areaNode.getObjects(), elemObjects, doc);

            Element elemId = doc.createElement("ID");
            elemId.appendChild(doc.createTextNode(String.valueOf(areaNode.getNodeId())));
            elemNode.appendChild(elemId);

            Element elemTag = doc.createElement("Tag");
            elemTag.appendChild(doc.createTextNode(String.valueOf(areaNode.getType())));
            elemNode.appendChild(elemTag);

            int x = 0, y = 0;
            if(areaNode instanceof DrawableAreaNode){
                x = (int) ((DrawableAreaNode) areaNode).getCenterX();
                y = (int) ((DrawableAreaNode) areaNode).getCenterY();
            }
            Element elemX = doc.createElement("X");
            elemX.appendChild(doc.createTextNode(""+x));
            elemNode.appendChild(elemX);

            Element elemY = doc.createElement("Y");
            elemY.appendChild(doc.createTextNode(""+y));
            elemNode.appendChild(elemY);

            Element elemEdges = doc.createElement("Edges");
            elemNode.appendChild(elemEdges);

            insertEdgesInto(areaNode.getEdges(), elemEdges, doc);

        }
    }

    private static void insertEdgesInto(List<Edge> edges, Element elemEdges, Document doc) {
        for (Edge edge : edges) {
            Element elemEdge = doc.createElement("Edge");
            elemEdges.appendChild(elemEdge);

            Element elemStart = doc.createElement("StartID");
            elemStart.appendChild(doc.createTextNode(String.valueOf(edge.getFrom().getNodeId())));
            elemEdge.appendChild(elemStart);

            Element elemEnd = doc.createElement("EndID");
            elemEnd.appendChild(doc.createTextNode(String.valueOf(edge.getTo().getNodeId())));
            elemEdge.appendChild(elemEnd);
        }
    }

}

