package com.commander4j.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
//import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.commander4j.sys.Common;

public class ViewRenderer extends JPanel implements TreeCellRenderer
{
	private static final long serialVersionUID = 1L;

	private Font NAME_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	private Font VALUE_FONT = new Font(Font.MONOSPACED, Font.ITALIC, 12);
	private Font ATTRIB_FONT = new Font(Font.MONOSPACED, Font.ITALIC, 12);

	private Color NAME_COLOR_ACTIVE = new Color(0, 0, 0);
	private Color NAME_COLOR = new Color(0, 0, 0);
	private Color NAME_COLOR_SELECTED = new Color(255, 255, 255);

	private Color VALUE_COLOR_ACTIVE = new Color(204, 0, 0);
	private Color VALUE_COLOR = new Color(204, 0, 0);
	private Color VALUE_COLOR_SELECTED = new Color(255, 51, 51);

	private Color ATTRIB_COLOR_ACTIVE = new Color(0, 153, 0);
	private Color ATTRIB_COLOR = new Color(0, 153, 0);
	private Color ATTRIB_COLOR_SELECTED = new Color(0, 204, 0);

	private ConcurrentHashMap<String, Icon> iconCache = new ConcurrentHashMap<>();
	private final int iconSizePx;

	private Border emptyLabel = new EmptyBorder(0, 5, 0, 5);
	private Border emptyPanel = new EmptyBorder(3, 0, 0, 0);

	private FlowLayout layout = new FlowLayout(FlowLayout.LEADING, 0, 0);

	public ViewRenderer(int iconSizePx)
	{
		this.iconSizePx = Math.max(0, iconSizePx);

		layout.setHgap(0);
		layout.setVgap(0);
		setLayout(layout);

		setOpaque(false);

		setBorder(emptyPanel);

		setAlignmentY(JPanel.BOTTOM_ALIGNMENT);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Object userObj = value;

		if (value instanceof DefaultMutableTreeNode node)
			userObj = node.getUserObject();

		if (selected)
		{
			NAME_COLOR_ACTIVE = NAME_COLOR_SELECTED;
			VALUE_COLOR_ACTIVE = VALUE_COLOR_SELECTED;
			ATTRIB_COLOR_ACTIVE = ATTRIB_COLOR_SELECTED;
		}
		else
		{
			NAME_COLOR_ACTIVE = NAME_COLOR;
			VALUE_COLOR_ACTIVE = VALUE_COLOR;
			ATTRIB_COLOR_ACTIVE = ATTRIB_COLOR;
		}

		removeAll();

		if (userObj instanceof ViewPanel rd)
		{
			LinkedList<ViewField> fields = rd.getFields();

			// Lock each column to its preferred width

			for (int col = 0; col < fields.size(); col++)
			{

				ViewField field = fields.get(col);
				String fn = getIconFilename(field);

				JLabel lblName = new JLabel();
				lblName.setFont(NAME_FONT);
				lblName.setForeground(NAME_COLOR_ACTIVE);
				lblName.setText(getDisplayName(field));
				lblName.setBorder(emptyLabel);

				JLabel lblValue = new JLabel();
				lblValue.setForeground(VALUE_COLOR_ACTIVE);
				lblValue.setFont(VALUE_FONT);
				lblValue.setText(getDisplayValue(field));
				lblValue.setBorder(emptyLabel);

				JLabel lblAttribs = new JLabel();
				lblAttribs.setFont(ATTRIB_FONT);
				lblAttribs.setForeground(ATTRIB_COLOR_ACTIVE);
				lblAttribs.setText(field.getAttributes());
				lblAttribs.setBorder(emptyLabel);


				if (!getIconFilename(field).isEmpty())
				{
					Icon icon = getIconFor(fn);
					if (icon != null)
					{
						lblName.setIcon((fn == null || fn.isBlank()) ? null : getIconFor(fn));
					}

				}

				add(lblName);


				add(lblValue);

				add(lblAttribs);

			}
		}

		return this;
	}

	private Icon getIconFor(String filename)
	{

		Icon cached = iconCache.get(filename);
		if (cached != null)
			return cached;

		Icon loaded = loadIconFromImagesFolder(filename);

		if (loaded != null)
		{
			iconCache.put(filename, loaded);
		}
		return loaded;

	}

	private Icon loadIconFromImagesFolder(String filename)
	{
		try
		{
			Path iconPath = Path.of(Common.xmlIconPath + filename);

			if (!iconPath.startsWith(Common.xmlIconPath))
				return null;
			if (!Files.isRegularFile(iconPath))
				return null;

			ImageIcon raw = new ImageIcon(iconPath.toString());
			if (iconSizePx <= 0)
				return raw;

			Image img = raw.getImage();
			if (img == null)
				return raw;

			Image scaled = img.getScaledInstance(iconSizePx, iconSizePx, Image.SCALE_SMOOTH);
			return new ImageIcon(scaled);
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	public boolean isIconRequired()
	{
		if (ViewTree.viewIcons.isSelected())
		{
			return true;
		}
		else
		{
			return false;
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

	public String getDisplayName(ViewField field)
	{
		String result = field.getName();

		if (ViewTree.xmlTranslations.containsKey("element name:" + result) && isTranslationRequired())
		{
			result = ViewTree.xmlTranslations.get("element name:" + result);
		}

		return result;
	}

	public String getDisplayValue(ViewField field)
	{

		String result = field.getValue().trim();

		if (ViewTree.xmlTranslations.containsKey("element value:" + result) && isTranslationRequired())
		{
			result = getOpenBracketsElement() + ViewTree.xmlTranslations.get("element value:" + result) + getCloseBracketsElement();
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

	public String getIconFilename(ViewField field)
	{
		String result = field.getFilename();

		if (isIconRequired())
		{

			if (field.getValue().equals("true") || (field.getValue().equals("false")))
			{
				if (field.getValue().equals("true"))
				{
					result = "true.png";
				}
				else
				{
					result = "false.png";
				}
			}
			else
			{
				if (result == null)
					result = field.getName() + ".png";

				if (result.isBlank())
					result = field.getName() + ".png";

				if (result.isEmpty())
					result = field.getName() + ".png";
			}
		}
		else
		{
			result = "";
		}

		return result;
	}

}
