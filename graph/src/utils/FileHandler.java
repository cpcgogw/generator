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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is to save and/or store drawableAreaNodes & edges
 *
 *
 */

public class FileHandler {

    public static void SaveNodes(ArrayList<DrawableAreaNode> drawableAreaNodes, String path) {

        try {

            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            Element elemNodes = doc.createElement("Nodes");




            doc.appendChild(elemNodes);




            for (DrawableAreaNode drawableAreaNode : drawableAreaNodes) {
                Element elemNode = doc.createElement("DrawableAreaNode");
                elemNodes.appendChild(elemNode);

                Element elemId = doc.createElement("ID");
                elemId.appendChild(doc.createTextNode(String.valueOf(drawableAreaNode.getNodeId())));
                elemNode.appendChild(elemId);

                Element elemTag = doc.createElement("Tag");
                elemTag.appendChild(doc.createTextNode(String.valueOf(drawableAreaNode.getType())));
                elemNode.appendChild(elemTag);

                Element elemX = doc.createElement("X");
                elemX.appendChild(doc.createTextNode(String.valueOf(drawableAreaNode.getCenterX())));
                elemNode.appendChild(elemX);

                Element elemY = doc.createElement("Y");
                elemY.appendChild(doc.createTextNode(String.valueOf(drawableAreaNode.getCenterY())));
                elemNode.appendChild(elemY);

                Element elemEdges = doc.createElement("Edges");
                elemNode.appendChild(elemEdges);
                for (DrawableEdge drawableEdge : drawableAreaNode.getDrawableEdges()) {
                    Element elemEdge = doc.createElement("DrawableEdge");
                    elemEdges.appendChild(elemEdge);

                    Element elemStart = doc.createElement("StartID");
                    elemStart.appendChild(doc.createTextNode(String.valueOf(drawableEdge.getStartDrawableAreaNode().getNodeId())));
                    elemEdge.appendChild(elemStart);

                    Element elemEnd = doc.createElement("EndID");
                    elemEnd.appendChild(doc.createTextNode(String.valueOf(drawableEdge.getEndDrawableAreaNode().getNodeId())));
                    elemEdge.appendChild(elemEnd);
                }
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
                FileWriter fileWriter = new FileWriter(path);
                StreamResult streamResult = new StreamResult(fileWriter);
                transformer.transform(source, streamResult);

            } catch (Exception e){
                e.printStackTrace();
            }

        } catch (TransformerException ex) {
            System.out.println("Error outputting document");

        } catch (ParserConfigurationException ex) {
            System.out.println("Error building document");
        }
    }
    public static Pair<ArrayList<DrawableAreaNode>,ArrayList<DrawableEdge>> LoadNodes(File file){
        HashMap<Integer, DrawableAreaNode> nodeMap = new HashMap<>();
        ArrayList<DrawableEdge> drawableEdges = new ArrayList<>();

        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize(); //normalizes document


            //Defines all drawableAreaNodes...

            NodeList xnodeList = doc.getElementsByTagName("DrawableAreaNode"); //grab all "DrawableAreaNode" from XML-file
            nodeMap = extractNodes(xnodeList);



            //Defines all drawableEdges...
            HashMap<Integer,ArrayList<Integer>> edgeMap = new HashMap<>(); //HashMap used for easy fix of duplicate drawableEdges.
            xnodeList = doc.getElementsByTagName("DrawableEdge");//grab all "DrawableEdge" from XML-file
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

    private static HashMap<Integer, ArrayList<Integer>> extractEdges(NodeList xnodeList, HashMap<Integer, DrawableAreaNode> nodeMap, ArrayList<DrawableEdge> drawableEdges) {
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
                    DrawableAreaNode startDrawableAreaNode = nodeMap.get(startID);
                    DrawableAreaNode endDrawableAreaNode = nodeMap.get(endID);

                    drawableEdges.add(new DrawableEdge(startDrawableAreaNode, endDrawableAreaNode));

                    edgeMap.get(startID).add(endID);
                }
            }
        }
        return edgeMap;
    }

    public static String[] LoadTags(String path) {
        String[] tags = null;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = documentBuilder.parse(new File(path));
            doc.getDocumentElement().normalize(); //normalizes document


            //Defines all drawableAreaNodes...
/*
            NodeList xnodeList = doc.getElementsByTagName("DrawableAreaNode"); //grab all "DrawableAreaNode" from XML-file
            for (int i = 0; i < xnodeList.getLength(); i++) {
                DrawableAreaNode xNode = xnodeList.item(i);


                if (xNode.getNodeType() == DrawableAreaNode.ELEMENT_NODE) {
                    Element element = (Element) xNode;

                    //Extract elements of node
                    int id = Integer.parseInt(element.getElementsByTagName("ID").item(0).getTextContent());
                    model.DrawableAreaNode.NodeType type = model.DrawableAreaNode.NodeType.valueOf(element.getElementsByTagName("Tag").item(0).getTextContent());
                    double x = Double.parseDouble(element.getElementsByTagName("X").item(0).getTextContent());
                    double y = Double.parseDouble(element.getElementsByTagName("Y").item(0).getTextContent());
                    //Store the extracted DrawableAreaNode

                    model.DrawableAreaNode node = new model.DrawableAreaNode(id, x, y, model.DrawableAreaNode.DEFAULT_RADIUS, Color.RED, type);

                }

            }
*/
            NodeList tagNode = doc.getElementsByTagName("Tag");
            int size = tagNode.getLength();
            tags = new String[size];
            for(int i = 0;i < size;i++){
                Node item = tagNode.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) item;
                    tags[i] = element.getTextContent();

                }
            }

        }
        catch (Exception e){e.printStackTrace();}
        return tags;
    }

    public static void SaveTags(String[] tags, String path){
        try {
            DocumentBuilderFactory documentBuilderFactoryFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = documentBuilderFactoryFact.newDocumentBuilder();
            Document doc = build.newDocument();


            Element elemTags = doc.createElement("Tags");
            for(int i = 0; i < tags.length; i++) {
                Element elemTag = doc.createElement("Tag");
                elemTag.appendChild(doc.createTextNode(tags[i]));
                elemTags.appendChild(elemTag);
            }

            doc.appendChild(elemTags);


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
        catch (Exception e){e.printStackTrace();}
    }

    public static ArrayList<Pair<ArrayList<DrawableAreaNode>, ArrayList<DrawableEdge>>> LoadTranslations(File file) {
        ArrayList<Pair<ArrayList<DrawableAreaNode>, ArrayList<DrawableEdge>>> translations = new ArrayList<>();
        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize(); //normalizes document


            ArrayList<NodeList> translationNodes = extractTranslations(doc);
            //foreach translation
            for (NodeList list : translationNodes) {
                //insert into translations
                HashMap<Integer, DrawableAreaNode> nodeMap = new HashMap<>();
                ArrayList<DrawableEdge> drawableEdges = new ArrayList<>();


                NodeList xnodeList = ((Element) list).getElementsByTagName("DrawableAreaNode"); //grab all "DrawableAreaNode" from matchingDrawablePattern
                nodeMap = extractNodes(xnodeList);

                //Defines all drawableEdges...
                HashMap<Integer,ArrayList<Integer>> edgeMap = new HashMap<>(); //HashMap used for easy fix of duplicate drawableEdges.
                xnodeList = ((Element) list).getElementsByTagName("DrawableEdge");//grab all "DrawableEdge" from XML-file
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
        NodeList patterns = posTrans.getElementsByTagName("DrawablePattern");
        for (int i = 0; i < patterns.getLength(); i++) {
            nodeListList.add((NodeList) patterns.item(i));
        }
        return nodeListList;
    }

    public static Pair<ArrayList<DrawableAreaNode>, ArrayList<DrawableEdge>> LoadMatchingPattern(File file) {
        HashMap<Integer, DrawableAreaNode> nodeMap = new HashMap<>();
        ArrayList<DrawableEdge> drawableEdges = new ArrayList<>();

        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize(); //normalizes document


            //Defines all drawableAreaNodes...
            Node pattern = doc.getElementsByTagName("MatchingPattern").item(0);
            NodeList xnodeList = ((Element) pattern).getElementsByTagName("DrawableAreaNode"); //grab all "DrawableAreaNode" from matchingDrawablePattern
            nodeMap = extractNodes(xnodeList);




            //Defines all drawableEdges...
            HashMap<Integer,ArrayList<Integer>> edgeMap = new HashMap<>(); //HashMap used for easy fix of duplicate drawableEdges.
            xnodeList = ((Element) pattern).getElementsByTagName("DrawableEdge");//grab all "DrawableEdge" from XML-file
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

    private static HashMap<Integer, DrawableAreaNode> extractNodes(NodeList xnodeList) {
        HashMap<Integer, DrawableAreaNode> nodeMap = new HashMap<>();
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
            Element elemNodes = doc.createElement("DrawablePattern");
            elemMatchingPattern.appendChild(elemNodes);
            //<Rule><MatchingPattern><DrawablePattern>...</DrawablePattern></MatchingPattern></Rule>
            insertNodesInto(rule.matchingDrawablePattern.drawableAreaNodes, elemNodes, doc);


            //<Rule><PossibleTranslations></PossibleTranslations></Rule>
            Element elemPosTranslations = doc.createElement("PossibleTranslations");
            elemRule.appendChild(elemPosTranslations);
            //<Rule><PossibleTranslations><DrawablePattern></DrawablePattern>....</PossibleTranslations></Rule>
            for (DrawablePattern p : rule.possibleTranslations) {
                Element elemSinglePattern = doc.createElement("DrawablePattern");
                elemPosTranslations.appendChild(elemSinglePattern);
                //<Rule><PossibleTranslations><DrawablePattern>...</DrawablePattern>[..]</PossibleTranslations></Rule>
                insertNodesInto(p.drawableAreaNodes, elemSinglePattern, doc);
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

    private static void insertNodesInto(ArrayList<DrawableAreaNode> drawableAreaNodes, Element elemNodes, Document doc) {
        //<DrawableAreaNode><ID>id</ID><Tag>tag</Tag><X>x</X><Y>y</Y></DrawableAreaNode>
        for (DrawableAreaNode drawableAreaNode : drawableAreaNodes) {
            Element elemNode = doc.createElement("DrawableAreaNode");
            elemNodes.appendChild(elemNode);

            Element elemId = doc.createElement("ID");
            elemId.appendChild(doc.createTextNode(String.valueOf(drawableAreaNode.getNodeId())));
            elemNode.appendChild(elemId);

            Element elemTag = doc.createElement("Tag");
            elemTag.appendChild(doc.createTextNode(String.valueOf(drawableAreaNode.getType())));
            elemNode.appendChild(elemTag);

            Element elemX = doc.createElement("X");
            elemX.appendChild(doc.createTextNode(String.valueOf(drawableAreaNode.getCenterX())));
            elemNode.appendChild(elemX);

            Element elemY = doc.createElement("Y");
            elemY.appendChild(doc.createTextNode(String.valueOf(drawableAreaNode.getCenterY())));
            elemNode.appendChild(elemY);

            Element elemEdges = doc.createElement("Edges");
            elemNode.appendChild(elemEdges);
            for (DrawableEdge drawableEdge : drawableAreaNode.getDrawableEdges()) {
                Element elemEdge = doc.createElement("DrawableEdge");
                elemEdges.appendChild(elemEdge);

                Element elemStart = doc.createElement("StartID");
                elemStart.appendChild(doc.createTextNode(String.valueOf(drawableEdge.getStartDrawableAreaNode().getNodeId())));
                elemEdge.appendChild(elemStart);

                Element elemEnd = doc.createElement("EndID");
                elemEnd.appendChild(doc.createTextNode(String.valueOf(drawableEdge.getEndDrawableAreaNode().getNodeId())));
                elemEdge.appendChild(elemEnd);
            }

        }
    }

}

