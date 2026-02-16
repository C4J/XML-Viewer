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
			// Create a DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Load the XML file
			Document document = builder.parse(new File(xmlFilePath));
			document.getDocumentElement().normalize();

			// Locate the language element based on the provided code
			NodeList languageNodes = document.getElementsByTagName("language");
			for (int i = 0; i < languageNodes.getLength(); i++)
			{
				Element languageElement = (Element) languageNodes.item(i);

				// Check if the language code matches
				if (languageElement.getAttribute("code").equals(languageCode))
				{

					NodeList typeNodes = document.getElementsByTagName("type");

					for (int t = 0; t < typeNodes.getLength(); t++)
					{
						Element typeElement = (Element) typeNodes.item(t);

						String type = typeElement.getAttribute("id");

						NodeList translationNodes = typeElement.getElementsByTagName("translation");


						for (int j = 0; j < translationNodes.getLength(); j++)
						{
							Element translationElement = (Element) translationNodes.item(j);
							String from = translationElement.getAttribute("from");
							String to = translationElement.getAttribute("to");

							translations.put(type+":"+from, to);

						}

					}
					break; // Exit once the correct language is found
				}
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage()); // Handle exceptions appropriately
		}

		return translations;
	}
}