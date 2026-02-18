package com.commander4j.config;

import java.io.File;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.commander4j.xml.DomText;

public class ViewConfig
{

	private String configFile = "./xml/config/config.xml";
	private File file = null;
	private String translation = "default.xml";
	private int treeExpansion = 2;
	private String language = "en";
	private LinkedList<String> availableLanguages = new LinkedList<String>();

	public File getFile()
	{
		return file;
	}

	public String getFileAsString()
	{
		if (file == null)
		{
			return "";
		}
		else
		{
			return getFile().getPath();
		}
	}

	public File getFolder()
	{
		return file.getParentFile();
	}

	public String getLanguage()
	{
		if (language.equals(""))
			language = "en";
		return language;
	}

	public String getTranslation()
	{
		if (translation.equals(""))
		{
			translation = "default.xml";
		}
		return translation;
	}

	public int getTreeExpansion()
	{
		return treeExpansion;
	}

	public int increaseTreeExpansion(JLabel label)
	{
		treeExpansion++;
		updateLabel(label);
		return treeExpansion;
	}

	public void load()
	{

		try
		{
			DomText dom = new DomText();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			File input = new File(configFile);

			Document doc = dBuilder.parse(input);
			doc.getDocumentElement().normalize();

			NodeList nodes = doc.getElementsByTagName("inputPath");
			Element element = (Element) nodes.item(0);
			String value = dom.directText(element);
			setFile(new File(value));

			nodes = doc.getElementsByTagName("translation");
			element = (Element) nodes.item(0);
			value = dom.directText(element);
			setTranslation(value);

			nodes = doc.getElementsByTagName("treeExpansion");
			element = (Element) nodes.item(0);
			value = dom.directText(element);
			if (value.equals(""))
				value = "2";
			setTreeExpansion(Integer.valueOf(value));

			nodes = doc.getElementsByTagName("language");
			element = (Element) nodes.item(0);
			value = dom.directText(element);
			if (value.equals(""))
				value = "en";
			setLanguage(value);

			availableLanguages.clear();

			NodeList optionsList = doc.getDocumentElement().getElementsByTagName("languages");

			if (optionsList.getLength() > 0)
			{
				Element options = (Element) optionsList.item(0);

				NodeList languageNodes = options.getElementsByTagName("language");

				for (int i = 0; i < languageNodes.getLength(); i++)
				{
					String v = languageNodes.item(i).getTextContent();
					if (v != null)
					{
						v = v.trim();
						if (!v.isEmpty())
							availableLanguages.add(v);
					}
				}
			}
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}

	}

	public int reduceTreeExpansion(JLabel label)
	{
		treeExpansion--;
		if (treeExpansion < 0)
			treeExpansion = 0;
		updateLabel(label);
		return treeExpansion;
	}

	public boolean save()
	{

		boolean result = false;

		try
		{

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element config = doc.createElement("config");
			Element defaults = doc.createElement("defaults");
			Element inputPath = doc.createElement("inputPath");
			Element translation = doc.createElement("translation");
			Element treeExpansion = doc.createElement("treeExpansion");
			Element language = doc.createElement("language");
			Element options = doc.createElement("options");
			Element languages = doc.createElement("languages");

			doc.appendChild(config);

			inputPath.setTextContent(getFileAsString());
			translation.setTextContent(getTranslation());
			treeExpansion.setTextContent(String.valueOf(getTreeExpansion()));
			language.setTextContent(getLanguage());

			defaults.appendChild(inputPath);
			defaults.appendChild(translation);
			defaults.appendChild(treeExpansion);
			defaults.appendChild(language);

			for (int x = 0; x < availableLanguages.size(); x++)
			{
				Element language2 = doc.createElement("language");
				language2.setTextContent(availableLanguages.get(x));
				languages.appendChild(language2);
			}

			options.appendChild(languages);

			config.appendChild(defaults);
			config.appendChild(options);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			File output = new File(configFile);

			DOMSource source = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(output);

			transformer.transform(source, streamResult);

			result = true;
		}
		catch (Exception ex)
		{

		}

		return result;

	}

	public void setFile(File selectedFile)
	{
		file = selectedFile;
	}

	public void setLanguage(String lang)
	{
		language = lang;
	}

	public void setTranslation(String trans)
	{
		translation = trans;
	}

	public void setTreeExpansion(int level)
	{
		treeExpansion = level;
		if (treeExpansion < 0)
			treeExpansion = 0;

	}

	public void setTreeExpansion(int level, JLabel label)
	{
		treeExpansion = level;
		if (treeExpansion < 0)
			treeExpansion = 0;
		updateLabel(label);
	}

	private void updateLabel(JLabel label)
	{
		label.setText(String.valueOf(treeExpansion));
	}

	public DefaultComboBoxModel<String> getLanguageOptions()
	{
		DefaultComboBoxModel<String> DefComboBoxMod = new DefaultComboBoxModel<String>();

		DefComboBoxMod.addAll(availableLanguages);

		DefComboBoxMod.setSelectedItem(language);

		return DefComboBoxMod;
	}
}
