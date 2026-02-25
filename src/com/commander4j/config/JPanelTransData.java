package com.commander4j.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.commander4j.gui.JCheckBox4j;
import com.commander4j.gui.JTextField4j;
import com.commander4j.util.Utility;

public class JPanelTransData extends JPanel
{
	private static final long serialVersionUID = 1L;

	public JCheckBox4j selected = new JCheckBox4j();
	public JTextField4j fld_Name = new JTextField4j();
	public JTextField4j fld_Value = new JTextField4j();

	public int rowheight = 25;
	public int namewidth = 520;
	public int valuewidth = 520;

	public int totalwidth = namewidth+valuewidth+30;

	private Utility utils = new Utility();

	JFrame parent;

	public JPanelTransData(JFrame parent)
	{
		this.parent = parent;

		setBackground(new Color(255, 255, 255));
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setPreferredSize(new Dimension(totalwidth,rowheight));

		selected = new JCheckBox4j();
		selected.setSelected(false);
		selected.setEnabled(true);
		selected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selected.isSelected())
				{
					fld_Value.requestFocus();
				}
			}
		});
		add(selected);


		fld_Name.setHorizontalAlignment(SwingConstants.LEADING);
		fld_Name.setEnabled(false);
		fld_Name.setPreferredSize(new Dimension(namewidth,rowheight));
		fld_Name.setMaximumSize(new Dimension(namewidth,rowheight));

		add(fld_Name);


		fld_Value.setHorizontalAlignment(SwingConstants.LEADING);
		fld_Value.setEnabled(true);
		fld_Value.setEditable(true);
		fld_Value.setPreferredSize(new Dimension(valuewidth,rowheight));
		fld_Value.setMaximumSize(new Dimension(valuewidth,rowheight));

		add(fld_Value);
	}

	public String getName()
	{
		String result = "";
		if (fld_Name != null)
		{
			result = utils.replaceNullStringwithBlank(fld_Name.getText());
		}
		return result;
	}

	public String getValue()
	{
		String result = "";

		if (fld_Value != null)
		{
			result = utils.replaceNullStringwithBlank(fld_Value.getText());
		}
		return result;
	}

	public boolean getSelected()
	{
		boolean result = false;
		if (selected != null)
		{
			result = 	selected.isSelected();
		}

		return result;
	}
}
