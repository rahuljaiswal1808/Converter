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
            File csvFile = new File(pathToCsvFile); // Replace with the path to your CSV file
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));

            Map<String, String> keyValuePairs = new LinkedHashMap<>();
            Map<String, Map<String, String>> pluralsMap = new LinkedHashMap<>();
            Map<String, List<String>> stringArraysMap = new LinkedHashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into key and value
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    if (key.endsWith("_plural")) {
                        String baseKey = key.replace("_plural", "");
                        pluralsMap.computeIfAbsent(baseKey, k -> new LinkedHashMap<>()).put(key, value);
                    } else if (key.endsWith("_string-array")) {
                        String baseKey = key.replace("_string-array", "");
                        stringArraysMap.computeIfAbsent(baseKey, k -> new ArrayList<>()).add(value);
                    } else {
                        keyValuePairs.put(key, value);
                    }
                }
            }

            // Define the output XML file path
            File xmlFile = new File(pathToXmlFile);

            // Write the XML header and opening <resources> tag
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile))) {
                writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                writer.write("<resources>\n");

                // Write the key-value pairs as <string> elements
                for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
                    writer.write("    <string name=\"" + entry.getKey() + "\">" + entry.getValue() + "</string>\n");
                }

                // Write plurals
                for (Map.Entry<String, Map<String, String>> pluralsEntry : pluralsMap.entrySet()) {
                    writer.write("    <plurals name=\"" + pluralsEntry.getKey() + "\">\n");
                    Map<String, String> pluralValues = pluralsEntry.getValue();
                    for (Map.Entry<String, String> pluralValue : pluralValues.entrySet()) {
                        writer.write("        <item quantity=\"" + pluralValue.getKey() + "\">" + pluralValue.getValue() + "</item>\n");
                    }
                    writer.write("    </plurals>\n");
                }

                // Write string arrays
                for (Map.Entry<String, List<String>> arrayEntry : stringArraysMap.entrySet()) {
                    writer.write("    <string-array name=\"" + arrayEntry.getKey() + "\">\n");
                    List<String> arrayValues = arrayEntry.getValue();
                    for (String arrayValue : arrayValues) {
                        writer.write("        <item>" + arrayValue + "</item>\n");
                    }
                    writer.write("    </string-array>\n");
                }

                // Close the <resources> tag
                writer.write("</resources>\n");
            }

            System.out.println("XML file generated: " + xmlFile.getAbsolutePath());
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
                    writer.write(entry.getKey() + "==>" + entry.getValue() + "\n");
                }
            }
            System.out.println("CSV written to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
