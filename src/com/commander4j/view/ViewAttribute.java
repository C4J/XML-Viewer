package com.commander4j.view;

public class ViewAttribute
{
	private String attributeName="";
	private String attributeValue="";

	public ViewAttribute(String name,String Value)
	{
		setAttributeName(name);
		setAttributeValue(Value);
	}

	public String getAttributeName()
	{
		return attributeName;
	}
	public void setAttributeName(String name)
	{
		this.attributeName = name;
	}
	public String getAttributeValue()
	{
		return attributeValue;
	}
	public void setAttributeValue(String value)
	{
		this.attributeValue = value;
	}

	public String getAttributeDisplayName()
	{
		String result = getAttributeName();

		if (ViewTree.xmlTranslations.containsKey("attribute name:" + result) && isTranslationRequired())
		{
			result = ViewTree.xmlTranslations.get("attribute name:" + result);
		}

		return result;
	}

	public String getAttributeDisplayValue()
	{

		String result = getAttributeValue().trim();

		if (ViewTree.xmlTranslations.containsKey("attribute value:" + result) && isTranslationRequired())
		{
			result = getOpenBracketsElement() + ViewTree.xmlTranslations.get("attribute value:" + result) + getCloseBracketsElement();
		}
		else
		{
			if (result.equals("") == false)
			{
				result = getOpenBracketsElement() + result + getCloseBracketsElement();
			}
		}

		return result;
	}

	public String getOpenBracketsElement()
	{
		if (ViewTree.viewBrackets.isSelected())
		{
			return "[";
		}
		else
		{
			return "";
		}
	}

	public String getCloseBracketsElement()
	{
		if (ViewTree.viewBrackets.isSelected())
		{
			return "]";
		}
		else
		{
			return "";
		}
	}

	public boolean isTranslationRequired()
	{
		if (ViewTree.viewTrans.isSelected())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
