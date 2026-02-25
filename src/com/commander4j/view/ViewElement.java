package com.commander4j.view;

import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.commander4j.util.Utility;

public class ViewElement
{
	private String elementName = "";
	private String elementFilename = "";
	private String elementValue = "";

	private Utility util = new Utility();
	private TreeMap<String, ViewAttribute> attribs = new TreeMap<String, ViewAttribute>();

	public ViewElement(Element element)
	{
		setElementName(element.getNodeName());

		setElementValue(directText(element));

		storeAttributes(element);
	}

	public void addAttrib(String key, ViewAttribute attrib)
	{
		attribs.put(key, attrib);
	}

	public void clearAttrib()
	{
		attribs.clear();
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

	public ViewAttribute getAttrib(String key)
	{
		return attribs.get(key);
	}

	public String getAttributesDisplay()
	{

		String result = "";
		String name = "";
		String value = "";

		for (Map.Entry<String, ViewAttribute> set : attribs.entrySet())
		{
			name = set.getValue().getAttributeDisplayName();
			value = set.getValue().getAttributeDisplayValue();
			result = result+" "+name+"="+value;
		}
		return result;

	}

	public TreeMap<String, ViewAttribute> getAttributes()
	{
		return attribs;
	}

	public String getElementFilename()
	{
		return util.replaceNullStringwithBlank(elementFilename);
	}

	public String getElementName()
	{
		return util.replaceNullStringwithBlank(elementName);
	}

	public String getElementValue()
	{
		return util.replaceNullStringwithBlank(elementValue);
	}

	public void removeAttrib(String key)
	{
		attribs.remove(key);
	}

	public void setElementName(String name)
	{
		elementName = util.replaceNullStringwithBlank(name);
	}

	public void setElementValue(String value)
	{
		elementValue = util.replaceNullStringwithBlank(value);
	}

	private String storeAttributes(Element elem)
	{
		String result = "";

		NamedNodeMap attrs = elem.getAttributes();

		for (int x = 0; x < attrs.getLength(); x++)
		{

			addAttrib(attrs.item(x).getNodeName(), new ViewAttribute(attrs.item(x).getNodeName(), attrs.item(x).getNodeValue()));

			result = result + attrs.item(x).getNodeName() + "='" + attrs.item(x).getNodeValue() + "' ";
		}

		result = result.trim();

		return result;
	}
}
