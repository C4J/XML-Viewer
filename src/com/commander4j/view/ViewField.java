package com.commander4j.view;

import com.commander4j.util.Utility;

public class ViewField
{
	private String name = "";
	private String filename = ""; // blank/null => no icon
	private String value = "";
	private String attributes = "";
	private Utility util = new Utility();

	public ViewField(String name, String value, String attribs, String filename)
	{
		this.name = (name == null) ? "" : name;
		this.filename = filename;
		this.value = value;
		this.attributes = attribs;
	}

	public String getAttributes()
	{
		return util.replaceNullStringwithBlank(attributes);
	}

	public void setAttributes(String attributes)
	{
		this.attributes = attributes;
	}

	public String getName()
	{

		return util.replaceNullStringwithBlank(name);

	}

	public String getValue()
	{
		return util.replaceNullStringwithBlank(value);
	}

	public String getFilename()
	{
		return util.replaceNullStringwithBlank(filename);
	}

}
