package com.commander4j.view;

import java.io.File;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultComboBoxModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ViewTranslations
{

	private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ViewTranslations.class);

	public DefaultComboBoxModel<String>  populateFiles(String current)
	{
		String defaultpath = "." + File.separator + "xml" + File.separator + "translations";

		TreeSet<String> fileList = new TreeSet<String>();

		DefaultComboBoxModel<String> DefComboBoxMod = new DefaultComboBoxModel<String>();

		File dir = new File(defaultpath);

		List<File> filenames = (List<File>) FileUtils.listFiles(dir, new String[] {"xml","XML"}, false);

		String selected = null;

		if (filenames.size() > 0)
		{
			for (int x = 0;x<filenames.size();x++)
			{
				fileList.add(filenames.get(x).getName());

				if (current.equals(filenames.get(x).getName()))
				{
					selected = filenames.get(x).getName();
				}
			}

			DefComboBoxMod.addAll(fileList);

			DefComboBoxMod.setSelectedItem(selected);
		}

		return DefComboBoxMod;

	}


	public ConcurrentHashMap<String, String> loadTranslations(String xmlFilePath, String languageCode)
	{
	    ConcurrentHashMap<String, String> translations = new ConcurrentHashMap<>();

	    try
	    {
	        translations.clear();

	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setIgnoringComments(true);
	        factory.setCoalescing(true);

	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document document = builder.parse(new File(xmlFilePath));
	        document.getDocumentElement().normalize();

	        NodeList languageNodes = document.getElementsByTagName("language");

	        for (int i = 0; i < languageNodes.getLength(); i++)
	        {
	            Node langNode = languageNodes.item(i);
	            if (langNode.getNodeType() != Node.ELEMENT_NODE) continue;

	            Element languageElement = (Element) langNode;

	            if (languageCode.equals(languageElement.getAttribute("code")))
	            {
	                // IMPORTANT: scope searches to the selected language element
	                NodeList typeNodes = languageElement.getElementsByTagName("type");

	                for (int t = 0; t < typeNodes.getLength(); t++)
	                {
	                    Node typeNode = typeNodes.item(t);
	                    if (typeNode.getNodeType() != Node.ELEMENT_NODE) continue;

	                    Element typeElement = (Element) typeNode;
	                    String type = typeElement.getAttribute("id");

	                    NodeList translationNodes = typeElement.getElementsByTagName("translation");

	                    for (int j = 0; j < translationNodes.getLength(); j++)
	                    {
	                        Node trNode = translationNodes.item(j);
	                        if (trNode.getNodeType() != Node.ELEMENT_NODE) continue;

	                        Element translationElement = (Element) trNode;
	                        String from = translationElement.getAttribute("from");
	                        String to = translationElement.getAttribute("to");

	                        // Optional: skip empty keys
	                        if (from != null && !from.isBlank())
	                        {
	                            translations.put(type + ":" + from, to);
	                        }
	                    }
	                }
	                break; // Exit once the correct language is found
	            }
	        }
	    }
	    catch (Exception e)
	    {
	        logger.debug(e.getMessage());
	    }

	    return translations;
	}

}