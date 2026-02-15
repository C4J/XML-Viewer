package com.commander4j.view;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ViewTranslations {

	private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ViewTranslations.class);

    public ConcurrentHashMap<String, String> loadTranslations(String xmlFilePath, String languageCode) {


    	logger.debug("loadTranslations xmlFilePath="+xmlFilePath+"languageCode="+languageCode);

    	ConcurrentHashMap<String, String> translations = new ConcurrentHashMap<>();

        try {
            // Create a DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Load the XML file
            Document document = builder.parse(new File(xmlFilePath));
            document.getDocumentElement().normalize();

            // Locate the language element based on the provided code
            NodeList languageNodes = document.getElementsByTagName("language");
            for (int i = 0; i < languageNodes.getLength(); i++) {
                Element languageElement = (Element) languageNodes.item(i);

                // Check if the language code matches
                if (languageElement.getAttribute("code").equals(languageCode)) {
                    NodeList translationNodes = languageElement.getElementsByTagName("translation");

                    // Iterate through each translation and add to HashMap
                    for (int j = 0; j < translationNodes.getLength(); j++) {
                        Element translationElement = (Element) translationNodes.item(j);
                        String from = translationElement.getAttribute("from");
                        String to = translationElement.getAttribute("to");



                        translations.put(from, to);
                    }
                    break; // Exit once the correct language is found
                }
            }
        } catch (Exception e) {
           logger.debug(e.getMessage()); // Handle exceptions appropriately
        }


        logger.debug("translations loaded ["+translations.size()+"]");

        return translations;
    }
}