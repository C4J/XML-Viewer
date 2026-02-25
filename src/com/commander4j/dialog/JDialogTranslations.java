package com.commander4j.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.commander4j.config.JPanelTransData;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JRadioButton4j;
import com.commander4j.sys.Common;
import com.commander4j.util.Utility;
import com.commander4j.view.ViewTree;

public class JDialogTranslations extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private JFrame parent;
	private JPanel definedPanel;
	private JPanel undefinedPanel;
	private Utility util = new Utility();
	private JScrollPane definedScrollPanel = new JScrollPane();
	private JScrollPane undefinedScrollPanel = new JScrollPane();
	private JRadioButton4j JRadioButton_ElementName = new JRadioButton4j("Element Name");
	private JRadioButton4j JRadioButton_ElementValue = new JRadioButton4j("Element Value");
	private JRadioButton4j JRadioButton_AttributeName = new JRadioButton4j("Attribute Name");
	private JRadioButton4j JRadioButton_AttributeValue = new JRadioButton4j("Attribute Value");
	private JButton4j btnUp;
	private JButton4j btnDown;
	private String xmlKey = "";
	private String translationFilename = "";

	public JDialogTranslations(JFrame parent, String translation)
	{
		super(parent);

		translationFilename=translation;
		setModalityType(ModalityType.DOCUMENT_MODAL);

		this.parent = parent;

		setTitle("Translations [" + translation + "]");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setSize(new Dimension(1100, 680));

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		System.setProperty("apple.laf.useScreenMenuBar", "true");
		util.setLookAndFeel("Nimbus");
		contentPanel.setLayout(null);

		JRadioButton_ElementName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				xmlKey = JRadioButton_ElementName.getText().toLowerCase();
				populateLists();
			}
		});
		JRadioButton_ElementName.setBounds(304, 6, 107, 23);
		JRadioButton_ElementName.setSelected(true);
		contentPanel.add(JRadioButton_ElementName);

		JRadioButton_ElementValue.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				xmlKey = JRadioButton_ElementValue.getText().toLowerCase();
				populateLists();
			}
		});
		JRadioButton_ElementValue.setBounds(423, 6, 107, 23);
		contentPanel.add(JRadioButton_ElementValue);

		JRadioButton_AttributeName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				xmlKey = JRadioButton_AttributeName.getText().toLowerCase();
				populateLists();
			}
		});
		JRadioButton_AttributeName.setBounds(542, 6, 107, 23);
		contentPanel.add(JRadioButton_AttributeName);

		JRadioButton_AttributeValue.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				xmlKey = JRadioButton_AttributeValue.getText().toLowerCase();
				populateLists();
			}
		});
		JRadioButton_AttributeValue.setBounds(661, 6, 107, 23);
		contentPanel.add(JRadioButton_AttributeValue);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(JRadioButton_ElementName);
		buttonGroup.add(JRadioButton_ElementValue);
		buttonGroup.add(JRadioButton_AttributeName);
		buttonGroup.add(JRadioButton_AttributeValue);

		btnUp = new JButton4j(Common.app_icon_up);
		btnUp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				transferUP();
			}
		});
		btnUp.setSize(ViewTree.buttonSize);
		btnUp.setPreferredSize(ViewTree.buttonSize);
		btnUp.setMinimumSize(ViewTree.buttonSize);
		btnUp.setMaximumSize(ViewTree.buttonSize);
		btnUp.setFocusable(false);
		btnUp.setLocation(513, 334);
		;
		contentPanel.add(btnUp);

		btnDown = new JButton4j(Common.app_icon_down);
		btnDown.setSize(ViewTree.buttonSize);
		btnDown.setPreferredSize(ViewTree.buttonSize);
		btnDown.setMinimumSize(ViewTree.buttonSize);
		btnDown.setMaximumSize(ViewTree.buttonSize);
		btnDown.setFocusable(false);
		btnDown.setLocation(553, 334);
		btnDown.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				transferDOWN();
			}
		});
		contentPanel.add(btnDown);

		definedPanel = new JPanel();
		definedPanel.setLayout(new BoxLayout(definedPanel, BoxLayout.Y_AXIS));
		definedPanel.setBackground(Color.WHITE);

		definedScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		definedScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		definedScrollPanel.setBounds(0, 35, 1080, 295);

		contentPanel.add(definedScrollPanel);

		undefinedPanel = new JPanel();
		undefinedPanel.setLayout(new BoxLayout(undefinedPanel, BoxLayout.Y_AXIS));
		undefinedPanel.setBackground(Color.WHITE);

		undefinedScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		undefinedScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		undefinedScrollPanel.setBounds(0, 370, 1080, 240);

		contentPanel.add(undefinedScrollPanel);

		xmlKey = JRadioButton_ElementName.getText().toLowerCase();
		populateLists();

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton4j okButton = new JButton4j(Common.icon_ok);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				saveDefined();
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton4j cancelButton = new JButton4j(Common.icon_cancel);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		int widthadjustment = util.getOSWidthAdjustment();
		int heightadjustment = util.getOSHeightAdjustment();

		GraphicsDevice gd = util.getGraphicsDevice();

		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		Rectangle screenBounds = gc.getBounds();

		setBounds(screenBounds.x + ((screenBounds.width - JDialogTranslations.this.getWidth()) / 2), screenBounds.y + ((screenBounds.height - JDialogTranslations.this.getHeight()) / 2), JDialogTranslations.this.getWidth() + widthadjustment,
				JDialogTranslations.this.getHeight() + heightadjustment);
	}

	private void populateDefinedMatrix(JScrollPane sp, JPanel panel)
	{
		JPanelTransData one = new JPanelTransData(this.parent);
		panel.removeAll();

		for (String key : Common.viewConfig.translations.translationMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey).keySet())
		{
			one = new JPanelTransData(this.parent);
			one.fld_Name.setText(key);
			one.fld_Value.setText(Common.viewConfig.translations.translationMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey).get(key));
			panel.add(one);
		}

		sp.setViewportView(panel);
	}

	private void populateUnDefinedMatrix(TreeMap<String, String> found, JScrollPane sp, JPanel panel)
	{
		JPanelTransData one = new JPanelTransData(this.parent);
		panel.removeAll();

		if (found != null)
		{
			for (String key : found.keySet())
			{
				if (Common.viewConfig.translations.translationMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey).containsKey(key) == false)
				{
					one = new JPanelTransData(this.parent);
					one.fld_Name.setText(key);
					one.fld_Value.setText(found.get(key));

					panel.add(one);
				}
			}
		}

		sp.setViewportView(panel);
	}

	private void populateLists()
	{
		populateDefinedMatrix(definedScrollPanel, definedPanel);
		populateUnDefinedMatrix(Common.viewConfig.translations.xmlMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey), undefinedScrollPanel, undefinedPanel);
	}

	private void transferUP()
	{
		transfer(undefinedPanel, Common.viewConfig.translations.xmlMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey), definedPanel, Common.viewConfig.translations.translationMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey));
		populateLists();
	}

	private void transferDOWN()
	{
		transfer(definedPanel, Common.viewConfig.translations.translationMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey), undefinedPanel, Common.viewConfig.translations.xmlMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey));
		populateLists();
	}

	private void transfer(JPanel fromPanel, TreeMap<String, String> fromList, JPanel toPanel, TreeMap<String, String> toList)
	{
		Component[] pans = fromPanel.getComponents();

		for (int x = 0; x < pans.length; x++)
		{
			JPanelTransData data = (JPanelTransData) pans[x];

			if (data.getSelected() == true)
			{
				String transFrom = data.getName();
				String transTo = data.getValue();
				toList.put(transFrom, transTo);
				fromList.remove(transFrom);
			}
		}
	}

	private void saveDefined()
	{

		save(Common.viewConfig.translations.translationMatrix.get(Common.viewConfig.getLanguage()).get(xmlKey));

		Common.viewConfig.translations.save("./xml/translations/"+translationFilename);

	}

	private void save(TreeMap<String, String> toList)
	{
		toList.clear();

		Component[] pans = definedPanel.getComponents();

		for (int x = 0; x < pans.length; x++)
		{
			JPanelTransData data = (JPanelTransData) pans[x];

			String transFrom = data.getName();
			String transTo = data.getValue();
			toList.put(transFrom, transTo);
		}
	}
}
