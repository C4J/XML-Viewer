package com.commander4j.view;

import com.commander4j.sys.Common;

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

		if (isTranslationRequired())
		{
			result = Common.viewConfig.translations.getTranslation(Common.viewConfig.getLanguage(), "attribute name", result,result);
		}

		return result;
	}

	public String getAttributeDisplayValue()
	{

		String result = getAttributeValue().trim();

		if (isTranslationRequired())
		{
			result = getOpenBracketsElement() + Common.viewConfig.translations.getTranslation(Common.viewConfig.getLanguage(), "attribute value", result,result)+ getCloseBracketsElement();
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
