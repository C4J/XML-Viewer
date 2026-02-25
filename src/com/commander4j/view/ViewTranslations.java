package com.commander4j.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ViewTranslations
{
	public TreeMap<String, TreeMap<String, TreeMap<String, String>>> translationMatrix = new TreeMap<String, TreeMap<String, TreeMap<String, String>>>();

	public TreeMap<String, TreeMap<String, TreeMap<String, String>>> xmlMatrix = new TreeMap<String, TreeMap<String, TreeMap<String, String>>>();

	private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ViewTranslations.class);

	public DefaultComboBoxModel<String> populateFiles(String current)
	{
		String defaultpath = "." + File.separator + "xml" + File.separator + "translations";

		TreeSet<String> fileList = new TreeSet<String>();

		DefaultComboBoxModel<String> DefComboBoxMod = new DefaultComboBoxModel<String>();

		File dir = new File(defaultpath);

		List<File> filenames = (List<File>) FileUtils.listFiles(dir, new String[]
		{ "xml", "XML" }, false);

		String selected = null;

		if (filenames.size() > 0)
		{
			for (int x = 0; x < filenames.size(); x++)
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

	public void loadTranslationMatrix(String xmlFilePath)
	{
		try
		{

			translationMatrix.clear();

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
				if (langNode.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Element languageElement = (Element) langNode;
				String lang = languageElement.getAttribute("code");
				if (lang == null || lang.isBlank())
					continue;

				NodeList typeNodes = languageElement.getElementsByTagName("type");

				for (int t = 0; t < typeNodes.getLength(); t++)
				{
					Node typeNode = typeNodes.item(t);
					if (typeNode.getNodeType() != Node.ELEMENT_NODE)
						continue;

					Element typeElement = (Element) typeNode;
					String type = typeElement.getAttribute("id");
					if (type == null || type.isBlank())
						continue;

					NodeList translationNodes = typeElement.getElementsByTagName("translation");

					for (int j = 0; j < translationNodes.getLength(); j++)
					{
						Node trNode = translationNodes.item(j);
						if (trNode.getNodeType() != Node.ELEMENT_NODE)
							continue;

						Element tr = (Element) trNode;
						String from = tr.getAttribute("from");
						String to = tr.getAttribute("to");

						if (from == null || from.isBlank())
							continue;

						// Level 1: language -> (type -> (from -> to))
						TreeMap<String, TreeMap<String, String>> byType = translationMatrix.get(lang);
						if (byType == null)
						{
							byType = new TreeMap<>();
							translationMatrix.put(lang, byType);
						}

						// Level 2: type -> (from -> to)
						TreeMap<String, String> byFrom = byType.get(type);
						if (byFrom == null)
						{
							byFrom = new TreeMap<>();
							byType.put(type, byFrom);
						}

						byFrom.put(from, to);
					}
				}
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage(), e);
		}
	}

	public void addMissingTranslation(String lang, String type, String from)
	{
		if (lang == null || lang.isBlank())
			return;
		if (type == null || type.isBlank())
			return;
		if (from == null || from.isBlank())
			return;

		String to = from;

		// Level 1: language -> (type -> (from -> to))
		TreeMap<String, TreeMap<String, String>> byType = xmlMatrix.get(lang);
		if (byType == null)
		{
			byType = new TreeMap<>();
			xmlMatrix.put(lang, byType);
		}

		// Level 2: type -> (from -> to)
		TreeMap<String, String> byFrom = byType.get(type);
		if (byFrom == null)
		{
			byFrom = new TreeMap<>();
			byType.put(type, byFrom);
		}

		byFrom.put(from, to);

	}

	public String getTranslation(String language, String type, String from,String defaultValue)
	{
		if (from == null)
			return "";
		if (language == null || language.isBlank())
			return from;
		if (type == null || type.isBlank())
			return from;
		if (from.isBlank())
			return "";

//		if (from.equals("DOCNUM"))
//			System.out.println();

		TreeMap<String, TreeMap<String, TreeMap<String, String>>> matrix = translationMatrix;
		if (matrix == null || matrix.isEmpty())
			return from;

		TreeMap<String, TreeMap<String, String>> byType = matrix.get(language);
		if (byType == null)
			return from;

		TreeMap<String, String> byFrom = byType.get(type);
		if (byFrom == null)
			return from;

		String to = byFrom.get(from);
		if (to == null)
		{
			return defaultValue;
		}
		else
		{

				return to;

		}
		//return (to == null || to.isBlank()) ? defaultValue : to;
	}

	// Writes the XML to a file (UTF-8)
	public void writeTranslationMatrixXml(Path outputFile) throws Exception
	{

		Document doc = buildTranslationMatrixDocument();
		try (OutputStream os = Files.newOutputStream(outputFile))
		{
			writeDocument(doc, os);
		}
	}

	// Returns the XML as a String (UTF-8)
	public String toTranslationMatrixXmlString() throws ParserConfigurationException, TransformerException
	{

		Document doc = buildTranslationMatrixDocument();

		ByteArrayOutputStream baos = new ByteArrayOutputStream(8 * 1024);
		writeDocument(doc, baos);
		return baos.toString(StandardCharsets.UTF_8);
	}

	private Document buildTranslationMatrixDocument() throws ParserConfigurationException
	{

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// Safe defaults (optional)
		dbf.setNamespaceAware(false);

		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element display = doc.createElement("display");
		doc.appendChild(display);

		if (translationMatrix == null)
			return doc;

		// 1) language
		for (Map.Entry<String, TreeMap<String, TreeMap<String, String>>> langEntry : translationMatrix.entrySet())
		{
			String langCode = langEntry.getKey();
			if (langCode == null)
				continue;

			Element languageEl = doc.createElement("language");
			languageEl.setAttribute("code", langCode);
			display.appendChild(languageEl);

			TreeMap<String, TreeMap<String, String>> typeMap = langEntry.getValue();
			if (typeMap == null)
				continue;

			// 2) type
			for (Map.Entry<String, TreeMap<String, String>> typeEntry : typeMap.entrySet())
			{
				String typeId = typeEntry.getKey();
				if (typeId == null)
					continue;

				Element typeEl = doc.createElement("type");
				typeEl.setAttribute("id", typeId);
				languageEl.appendChild(typeEl);

				TreeMap<String, String> translations = typeEntry.getValue();
				if (translations == null)
					continue;

				// 3) translation
				for (Map.Entry<String, String> trEntry : translations.entrySet())
				{
					String from = trEntry.getKey();
					String to = trEntry.getValue();
					if (from == null || to == null)
						continue;

					Element trEl = doc.createElement("translation");
					trEl.setAttribute("from", from);
					trEl.setAttribute("to", to);
					typeEl.appendChild(trEl);
				}
			}
		}

		return doc;
	}

	private void writeDocument(Document doc, OutputStream os) throws TransformerException
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();

		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		t.setOutputProperty(OutputKeys.METHOD, "xml");
		t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		t.transform(new DOMSource(doc), new StreamResult(os));
	}

	public void save(String filename)
	{

		try
		{
			Document doc = buildTranslationMatrixDocument();

			OutputStream out = new FileOutputStream(filename);
			writeDocument(doc,out);
		}
		catch (ParserConfigurationException | FileNotFoundException | TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}