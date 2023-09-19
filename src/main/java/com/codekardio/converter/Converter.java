package com.codekardio.converter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class Converter {
    public static void csvToXml(String pathToCsvFile, String pathToXmlFile) {
        try {
            File csvFile = new File(pathToCsvFile);
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));

            File xmlFile = new File(pathToXmlFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile));

            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writer.write("<resources>\n");

            String line;
            while ((line = reader.readLine()) != null) {
                int firstCommaIndex = line.indexOf(",");
                if (firstCommaIndex != -1) {
                    String key = line.substring(0, firstCommaIndex).trim();
                    String value = line.substring(firstCommaIndex + 1).trim();

                    if (key.endsWith("_plural")) {
                        String baseKey = key.replace("_plural", "");
                        writer.write("    <plurals name=\"" + baseKey + "\">\n");
                        writer.write("        <item quantity=\"other\">" + value + "</item>\n");
                        writer.write("    </plurals>\n");
                    } else if (key.endsWith("_string-array")) {
                        String baseKey = key.replace("_string-array", "");
                        writer.write("    <string-array name=\"" + baseKey + "\">\n");
                        String[] arrayValues = value.split(",");
                        for (String arrayValue : arrayValues) {
                            writer.write("        <item>" + arrayValue + "</item>\n");
                        }
                        writer.write("    </string-array>\n");
                    } else {
                        writer.write("    <string name=\"" + key + "\">" + value + "</string>\n");
                    }
                }
            }

            writer.write("</resources>\n");

            reader.close();
            writer.close();
            System.out.println("XML file generated: " + pathToXmlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void xmlToCsv(String pathToXmlFile, String pathToCsvFile) {
        try {
            File inputFile = new File(pathToXmlFile); // Replace with the path to your strings.xml file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);

            Map<String, String> keyValuePair = new HashMap<>();

            NodeList stringNodes = document.getElementsByTagName("string");
            for (int i = 0; i < stringNodes.getLength(); i++) {
                Node node = stringNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String key = element.getAttribute("name");
                    String value = element.getTextContent();
                    keyValuePair.put(key, value);
                }
            }

            NodeList pluralNodes = document.getElementsByTagName("plurals");
            for (int i = 0; i < pluralNodes.getLength(); i++) {
                Node node = pluralNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String pluralName = element.getAttribute("name");
                    NodeList itemNodes = element.getElementsByTagName("item");
                    for (int j = 0; j < itemNodes.getLength(); j++) {
                        Element itemElement = (Element) itemNodes.item(j);
                        String quantity = itemElement.getAttribute("quantity");
                        String key = pluralName + "_" + "plurals" + "_" + quantity;
                        String value = itemElement.getTextContent();
                        keyValuePair.put(key, value);
                    }
                }
            }

            NodeList arrayNodes = document.getElementsByTagName("string-array");
            for (int i = 0; i < arrayNodes.getLength(); i++) {
                Node node = arrayNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String arrayName = element.getAttribute("name");
                    NodeList itemNodes = element.getElementsByTagName("item");
                    for (int j = 0; j < itemNodes.getLength(); j++) {
                        Element itemElement = (Element) itemNodes.item(j);
                        String key = arrayName + "_" + "string-array" + "_" + j;
                        String value = itemElement.getTextContent();
                        keyValuePair.put(key, value);
                    }
                }
            }

            // Define the output file path
            File outputFile = new File(pathToCsvFile);

            // Write the key-value pairs to the output file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                for (Map.Entry<String, String> entry : keyValuePair.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue() + "\n");
                }
            }
            System.out.println("CSV written to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
