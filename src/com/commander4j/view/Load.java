package com.commander4j.view;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Load
{
	public static int Mode_Standard = 1;
	public static int Mode_Flat = 2;

	public boolean showElementTextBrackets = true;
	public boolean showAttributeValueBrackets = true;
	private  String rootElementName = "default";

	public String getRootElementName()
	{
		return rootElementName;
	}

	public void setRootElementName(String name)
	{
		rootElementName = name;
	}

	public DefaultTreeModel getEmptyTreeModel()
	{
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();

		DefaultTreeModel result = new DefaultTreeModel(treeNode);

		return result;
	}

	public DefaultTreeModel getTreeModel(File inputFile, int mode)
	{
		DefaultMutableTreeNode treeNode = read(inputFile, mode);

		DefaultTreeModel result = new DefaultTreeModel(treeNode);

		return result;
	}

	public DefaultMutableTreeNode read(File inputFile, int mode)

	{
		DefaultMutableTreeNode treeNode = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();

		if (inputFile != null)
		{

			try
			{

				DocumentBuilder builder = factory.newDocumentBuilder();

				Document document = builder.parse(inputFile);

				document.getDocumentElement().normalize();

				Element rootElement = document.getDocumentElement();

				setRootElementName(rootElement.getNodeName());


				if (mode == Mode_Standard)
				{
					treeNode = recurse_Standard(1, rootNode, rootElement);
				}

				if (mode == Mode_Flat)
				{
					treeNode = recurse_Flat(1, rootNode, rootElement);
				}

			}
			catch (Exception ParserConfigurationException)
			{

			}

			factory = null;
		}
		return treeNode;
	}

	public String getRootName(Element element)
	{
		String result = "";

		NodeList nodeList = element.getChildNodes();

		int nodeCount = nodeList.getLength();

		for (int x = 0; x < nodeCount; x++)
		{
			Node childNode = nodeList.item(x);

			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				result = childNode.getNodeName();

				if (result != null)
					break;
			}
		}

		return result;
	}

	public DefaultMutableTreeNode recurse_Standard(int level, DefaultMutableTreeNode treeNode, Element element)
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode();

		String elementText = directText(element);

		String elementAttributes = getAttributes(element);

		ViewPanel vp = new ViewPanel();

		ViewField vf = new ViewField(element.getNodeName(), elementText, elementAttributes, "");

		vp.addField(vf);

		System.out.println(level + padSpaces(level * 3) + vf);

		node.setUserObject(vp);

		treeNode.add(node);

		NodeList nodeList = element.getChildNodes();

		int nodeCount = nodeList.getLength();

		for (int x = 0; x < nodeCount; x++)
		{
			Node childNode = nodeList.item(x);

			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElement = (Element) childNode;

				recurse_Standard(level + 1, node, childElement);
			}
		}

		return node;
	}

	public DefaultMutableTreeNode recurse_Flat(int level, DefaultMutableTreeNode treeNode, Element element)
	{

		DefaultMutableTreeNode node = new DefaultMutableTreeNode();

		String elementText = directText(element);

		String elementAttributes = getAttributes(element);

		String data = element.getNodeName() + " " + elementText + " " + elementAttributes;

		System.out.println(level + padSpaces(level * 3) + data);

		ViewPanel vp = new ViewPanel();

		ViewField vf = new ViewField(element.getNodeName(), elementText, elementAttributes, "");

		vp.addField(vf);

		node.setUserObject(vp);

		treeNode.add(node);

		NodeList nodeList = element.getChildNodes();

		String sameLevel = "";

		int nodeCount = nodeList.getLength();

		for (int x = 0; x < nodeCount; x++)
		{
			Node childNode = nodeList.item(x);

			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElement = (Element) childNode;

				if (hasChildElements(childElement) == false)
				{

					String elementTextChild = directText(childElement);

					String elementAttributesChild = getAttributes(childElement);

					String dataChild = childElement.getNodeName() + " " + elementTextChild + " " + elementAttributesChild;

					sameLevel = sameLevel + " " + dataChild;

					ViewField vf2 = new ViewField(childElement.getNodeName(), elementTextChild, elementAttributesChild, "");

					vp.addField(vf2);

					System.out.println(sameLevel);

					node.setUserObject(vp);

				}
			}
		}

		treeNode.add(node);

		for (int x = 0; x < nodeCount; x++)
		{
			Node childNode = nodeList.item(x);

			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElement = (Element) childNode;

				if (hasChildElements(childElement) == true)
				{

					treeNode.add(node);

					recurse_Flat(level + 1, node, childElement);

				}
			}
		}

		treeNode.add(node);

		return node;
	}

	private boolean hasChildElements(Element e)
	{
		boolean result = false;

		NodeList children = e.getChildNodes();

		for (int i = 0; i < children.getLength(); i++)
		{
			Node c = children.item(i);

			if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				result = true;
				break;
			}

		}

		return result;
	}

	private String getAttributes(Element elem)
	{
		String result = "";

		NamedNodeMap attrs = elem.getAttributes();

		for (int x = 0; x < attrs.getLength(); x++)
		{

			result = result + attrs.item(x).getNodeName() + "='" + attrs.item(x).getNodeValue() + "' ";
		}

		result = result.trim();

		return result;
	}

	public String directText(Element e)
	{
		String result = "";

		if (e != null)
		{
			NodeList children = e.getChildNodes();

			for (int i = 0; i < children.getLength(); i++)
			{
				Node n = children.item(i);

				short t = n.getNodeType();

				if (t == Node.TEXT_NODE || t == Node.CDATA_SECTION_NODE)
				{
					result = result + n.getNodeValue();
				}
			}
		}

		result = result.trim();

		return result;
	}


	private String padSpaces(int value)
	{
		String result = "";
		for (int x = 0; x < value; x++)
		{
			result = result + " ";
		}
		return result;
	}
}
