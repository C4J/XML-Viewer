package com.commander4j.view;

import java.util.LinkedList;

public class ViewPanel
{
	private final LinkedList<ViewField> fields = new LinkedList<>();

	public ViewPanel addField(ViewField field)
	{
		if (field != null)
			fields.add(field);
		return this;
	}

	public LinkedList<ViewField> getFields()
	{
		return fields;
	}

	@Override
	public String toString()
	{
		// Only used if no renderer is applied; keep something sensible.
		return fields.isEmpty() ? "" : fields.getFirst().getName();
	}
}
