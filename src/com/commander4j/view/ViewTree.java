package com.commander4j.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import com.commander4j.dialog.JDialogAbout;
import com.commander4j.dialog.JDialogLicenses;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JToggleButton4j;
import com.commander4j.sys.Common;
import com.commander4j.util.JFileFilterXML;
import com.commander4j.util.JHelp;
import com.commander4j.util.TreeExpandUtil;
import com.commander4j.util.Utility;

public final class ViewTree extends JFrame
{
	public static String version = "1.11";

	private static final long serialVersionUID = 1L;

	public static ConcurrentHashMap<String, String> elementNameTranslations = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String> elementValueTranslations = new ConcurrentHashMap<String, String>();

	public static ConcurrentHashMap<String, String> attributeNameTranslations = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String> attributeValueTranslations = new ConcurrentHashMap<String, String>();

	private String elementTranslations = "element_names.xml";
	private String attributeTranslations = "attribute_names.xml";
	private String elementValuesTranslations = "element_values.xml";
	private String attributeValuesTranslations = "attribute_values.xml";
	private String rootNodeName = "default";

	private Utility util = new Utility();

	private JPanel contentPane;

	private JTree tree;

	private JToolBar toolBarTop = new JToolBar();
	private JToolBar toolBarSide = new JToolBar();
	private JToolBar toolBarBottom = new JToolBar();

	public static JToggleButton4j viewMode;
	public static JToggleButton4j viewIcons;
	public static JToggleButton4j viewTrans;
	public static JToggleButton4j viewBrackets;

	private JButton4j btnBlank1;
	private JButton4j btnBlank2;
	private JButton4j btnReload;
	private JButton4j btnClear;
	private JButton4j btnSettings;
	private JButton4j btnHelp;
	private JButton4j btnAbout;
	private JButton4j btnOpen;
	private JButton4j btnLicense;
	private JButton4j btnClose;
	private JButton4j btnExpandAll;
	private JButton4j btnExpand;
	private JButton4j btnCollapseAll;
	private JButton4j btnCollapse;
	private JButton4j btnLevelMinus;
	private JButton4j btnLevelPlus;

	private DefaultTreeModel treeModel;

	private JScrollPane scrollPane;

	private File loadXML;

	private JSeparator sep = new JSeparator();
	private JSeparator sep2 = new JSeparator();

	private ViewTranslations viewTranslations = new ViewTranslations();

	private Dimension buttonSize = new Dimension(32, 32);
	private Dimension blankSize = new Dimension(32, 32);
	private Dimension labelSize = new Dimension(27, 27);

	private int treeExpandLevel = 2;

	private JLabel4j_std lblLevel = new JLabel4j_std(String.valueOf(treeExpandLevel));
	private JLabel4j_std lblViewMode = new JLabel4j_std("View Mode : ");
	private JLabel4j_std lblIconMode = new JLabel4j_std("  Icon Mode : ");
	private JLabel4j_std lblTransMode = new JLabel4j_std("  Translation Mode : ");
	private JLabel4j_std lblBracketMode = new JLabel4j_std("  Bracket Mode : ");

	private JLabel4j_std lblViewMode_Status = new JLabel4j_std("");
	private JLabel4j_std lblIconMode_Status = new JLabel4j_std("");
	private JLabel4j_std lblTransMode_Status = new JLabel4j_std("");
	private JLabel4j_std lblBracketMode_Status = new JLabel4j_std("");

	private int iconSize = 25;
	private int rowHeight = iconSize + 6;

	private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ViewTree.class);

	public static void main(String[] args)
	{
		ViewTree f = new ViewTree();
		f.setVisible(true);
	}

	public ViewTree()
	{
		util.setLookAndFeel("Nimbus");
		util.initLogging("");

		btnBlank1 = new JButton4j(Common.icon_blank);
	    btnBlank2 = new JButton4j(Common.icon_blank);
		btnOpen = new JButton4j(Common.icon_file_open);
		btnReload = new JButton4j(Common.icon_reload);
		btnClear = new JButton4j(Common.icon_erase);
		btnSettings = new JButton4j(Common.icon_settings);
		btnHelp = new JButton4j(Common.icon_help);
		btnAbout = new JButton4j(Common.icon_about);
		btnLicense = new JButton4j(Common.icon_license);
		btnClose = new JButton4j(Common.icon_exit);
		btnExpandAll = new JButton4j(Common.icon_expandAll);
		btnExpand = new JButton4j(Common.icon_expandNode);
		btnCollapseAll = new JButton4j(Common.icon_collapseAll);
		btnCollapse = new JButton4j(Common.icon_collapeNode);
		btnLevelMinus = new JButton4j(Common.icon_expandMinus);
		btnLevelPlus = new JButton4j(Common.icon_expandPlus);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		loadTranslations("en");

		sep.setAlignmentX(JSeparator.CENTER_ALIGNMENT);
		sep.setOrientation(JSeparator.VERTICAL);
		sep.setMaximumSize(labelSize);

		sep2.setAlignmentX(JSeparator.CENTER_ALIGNMENT);
		sep2.setOrientation(JSeparator.VERTICAL);
		sep2.setMaximumSize(labelSize);

		contentPane = new JPanel();
		contentPane.setBorder(new CompoundBorder());
		contentPane.setLayout(new BorderLayout(0, 0));

		setContentPane(contentPane);

		toolBarTop.setFloatable(false);
		toolBarTop.setOrientation(JToolBar.HORIZONTAL);
		toolBarTop.setAlignmentX(SwingConstants.HORIZONTAL);
		toolBarTop.setBackground(Common.color_app_window);
		toolBarTop.setBorder(BorderFactory.createEmptyBorder());
		contentPane.add(toolBarTop, BorderLayout.NORTH);

		toolBarSide.setFloatable(false);
		toolBarSide.setOrientation(JToolBar.VERTICAL);
		toolBarSide.setAlignmentX(SwingConstants.HORIZONTAL);
		toolBarSide.setBackground(Common.color_app_window);
		toolBarSide.setBorder(BorderFactory.createEmptyBorder());
		contentPane.add(toolBarSide, BorderLayout.WEST);

		toolBarBottom.setFloatable(false);
		toolBarBottom.setOrientation(JToolBar.HORIZONTAL);
		toolBarBottom.setAlignmentX(SwingConstants.HORIZONTAL);
		toolBarBottom.setBackground(Common.color_app_window);
		toolBarBottom.setBorder(BorderFactory.createEmptyBorder());
		contentPane.add(toolBarBottom, BorderLayout.SOUTH);

		btnOpen.setToolTipText("Open XML Document");
		toolBarSide.add(btnOpen);
		btnOpen.setPreferredSize(buttonSize);
		btnOpen.setFocusable(false);
		btnOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				openTree();

			}
		});

		btnReload.setToolTipText("Reload XML Document");
		toolBarSide.add(btnReload);
		btnReload.setPreferredSize(buttonSize);
		btnReload.setFocusable(false);
		btnReload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				reloadTree();

			}
		});

		btnClear.setToolTipText("Clear Document");
		toolBarSide.add(btnClear);
		btnClear.setPreferredSize(buttonSize);
		btnClear.setFocusable(false);
		btnClear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearTree();

			}
		});

		viewMode = new JToggleButton4j();
		viewMode.setSelected(true);
		viewMode.setToolTipText("View Mode");
		viewMode.setPreferredSize(buttonSize);
		viewMode.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeModeChange();
				loadXML(loadXML);
			}
		});
		toolBarSide.add(viewMode);

		viewIcons = new JToggleButton4j();
		viewIcons.setSelected(true);
		viewIcons.setToolTipText("View Icons");
		viewIcons.setPreferredSize(buttonSize);
		viewIcons.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeModeChange();
				tree.setCellRenderer(new ViewRenderer(iconSize));
			}
		});
		toolBarSide.add(viewIcons);

		viewTrans = new JToggleButton4j();
		viewTrans.setSelected(true);
		viewTrans.setToolTipText("View Translations");
		viewTrans.setPreferredSize(buttonSize);
		viewTrans.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeModeChange();
				tree.setCellRenderer(new ViewRenderer(iconSize));
			}
		});
		toolBarSide.add(viewTrans);

		viewBrackets = new JToggleButton4j();
		viewBrackets.setSelected(true);
		viewBrackets.setToolTipText("View Brackets");
		viewBrackets.setPreferredSize(buttonSize);
		viewBrackets.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeModeChange();
				tree.setCellRenderer(new ViewRenderer(iconSize));
			}
		});
		toolBarSide.add(viewBrackets);

		btnSettings.setToolTipText("Settings");
		toolBarSide.add(btnSettings);
		btnSettings.setPreferredSize(buttonSize);
		btnSettings.setFocusable(false);
		btnSettings.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// settings();
			}
		});

		btnHelp.setPreferredSize(buttonSize);
		btnHelp.setFocusable(false);
		btnHelp.setToolTipText("Help");
		toolBarSide.add(btnHelp);

		final JHelp help = new JHelp();
		help.enableHelpOnButton(btnHelp, "https://wiki.commander4j.com/index.php?title=XMLViewer");

		btnAbout.setPreferredSize(buttonSize);
		btnAbout.setFocusable(false);
		btnAbout.setToolTipText("About");
		btnAbout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JDialogAbout about = new JDialogAbout();
				about.setVisible(true);
			}
		});
		toolBarSide.add(btnAbout);

		btnLicense.setPreferredSize(buttonSize);
		btnLicense.setFocusable(false);
		btnLicense.setToolTipText("Licences");
		btnLicense.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JDialogLicenses dl = new JDialogLicenses(ViewTree.this);
				dl.setVisible(true);
			}
		});

		toolBarSide.add(btnLicense);

		btnClose.setToolTipText("Exit Application");
		toolBarSide.add(btnClose);
		btnClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				confirmExit();
			}
		});
		btnClose.setPreferredSize(new Dimension(32, 32));
		btnClose.setFocusable(false);

		btnBlank1.setPreferredSize(blankSize);
		btnBlank1.setMinimumSize(blankSize);
		btnBlank1.setMaximumSize(blankSize);
		btnBlank1.setBorder(BorderFactory.createEmptyBorder());
		btnBlank1.setFocusable(false);
		btnBlank1.setEnabled(false);
		btnBlank1.setBackground(Common.color_app_window);
		toolBarTop.add(btnBlank1);

		btnBlank2.setPreferredSize(blankSize);
		btnBlank2.setMinimumSize(blankSize);
		btnBlank2.setMaximumSize(blankSize);
		btnBlank2.setBorder(BorderFactory.createEmptyBorder());
		btnBlank2.setFocusable(false);
		btnBlank2.setEnabled(false);
		btnBlank2.setBackground(Common.color_app_window);
		toolBarBottom.add(btnBlank2);


		lblViewMode.setAlignmentX(JLabel4j_std.RIGHT_ALIGNMENT);
		lblViewMode.setFont(Common.font_status_bar_label);
		lblViewMode_Status.setAlignmentX(JLabel4j_std.LEFT_ALIGNMENT);
		lblViewMode_Status.setFont(Common.font_status_bar_status);
		toolBarBottom.add(lblViewMode);
		toolBarBottom.add(lblViewMode_Status);

		lblIconMode.setAlignmentX(JLabel4j_std.RIGHT_ALIGNMENT);
		lblIconMode.setFont(Common.font_status_bar_label);
		lblIconMode_Status.setAlignmentX(JLabel4j_std.LEFT_ALIGNMENT);
		lblIconMode_Status.setFont(Common.font_status_bar_status);
		toolBarBottom.add(lblIconMode);
		toolBarBottom.add(lblIconMode_Status);

		lblTransMode.setAlignmentX(JLabel4j_std.RIGHT_ALIGNMENT);
		lblTransMode.setFont(Common.font_status_bar_label);
		lblTransMode_Status.setAlignmentX(JLabel4j_std.LEFT_ALIGNMENT);
		lblTransMode_Status.setFont(Common.font_status_bar_status);
		toolBarBottom.add(lblIconMode);
		toolBarBottom.add(lblIconMode_Status);

		lblBracketMode.setAlignmentX(JLabel4j_std.RIGHT_ALIGNMENT);
		lblBracketMode.setFont(Common.font_status_bar_label);
		lblBracketMode_Status.setAlignmentX(JLabel4j_std.LEFT_ALIGNMENT);
		lblBracketMode_Status.setFont(Common.font_status_bar_status);
		toolBarBottom.add(lblTransMode);
		toolBarBottom.add(lblTransMode_Status);

		lblBracketMode.setAlignmentX(JLabel4j_std.RIGHT_ALIGNMENT);
		lblBracketMode.setFont(Common.font_status_bar_label);
		lblBracketMode_Status.setAlignmentX(JLabel4j_std.LEFT_ALIGNMENT);
		lblBracketMode_Status.setFont(Common.font_status_bar_status);
		toolBarBottom.add(lblBracketMode);
		toolBarBottom.add(lblBracketMode_Status);

		btnExpandAll.setToolTipText("Expand all branches");
		btnExpandAll.setPreferredSize(buttonSize);
		btnExpandAll.setFocusable(false);
		btnExpandAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeExpandUtil.expandAll(tree);
			}
		});
		toolBarTop.add(btnExpandAll);

		btnExpand.setPreferredSize(buttonSize);
		btnExpand.setToolTipText("Expand selected branch");
		btnExpand.setFocusable(false);
		btnExpand.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				TreeExpandUtil.expandSelectedPath(tree);
			}
		});
		toolBarTop.add(btnExpand);

		btnLevelPlus.setPreferredSize(buttonSize);
		btnLevelPlus.setToolTipText("Increase Branch Expansion Level");
		btnLevelPlus.setSize(buttonSize);
		btnLevelPlus.setFocusable(false);
		btnLevelPlus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setExpansionLevel(treeExpandLevel + 1);
				TreeExpandUtil.expandToLevelAndCollapseDeeper(tree, treeExpandLevel);
			}
		});
		toolBarTop.add(btnLevelPlus);

		toolBarTop.add(sep);

		btnCollapseAll.setPreferredSize(buttonSize);
		btnCollapseAll.setToolTipText("Collapse all branches");
		btnCollapseAll.setFocusable(false);
		btnCollapseAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setExpansionLevel(0);
				TreeExpandUtil.collapseAll(tree);
			}
		});
		toolBarTop.add(btnCollapseAll);

		btnCollapse.setPreferredSize(buttonSize);
		btnCollapse.setToolTipText("Collapse selected branch");
		btnCollapse.setFocusable(false);
		btnCollapse.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeExpandUtil.collapseSelectedPath(tree);
			}
		});
		toolBarTop.add(btnCollapse);


		btnLevelMinus.setPreferredSize(buttonSize);
		btnLevelMinus.setToolTipText("Reduce Branch Expansion Level");
		btnLevelMinus.setFocusable(false);
		btnLevelMinus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setExpansionLevel(treeExpandLevel - 1);
				TreeExpandUtil.expandToLevelAndCollapseDeeper(tree, treeExpandLevel);
			}
		});
		toolBarTop.add(btnLevelMinus);

		toolBarTop.add(sep2);

		lblLevel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		lblLevel.setToolTipText("Branch Expansion Level");
		lblLevel.setHorizontalAlignment(JLabel4j_std.CENTER);
		lblLevel.setSize(labelSize);
		lblLevel.setPreferredSize(labelSize);
		lblLevel.setMinimumSize(labelSize);
		lblLevel.setMaximumSize(labelSize);
		toolBarTop.add(lblLevel);



		tree = new JTree(new DefaultMutableTreeNode("No document"));
		tree.setBackground(Common.color_app_window);
		tree.setShowsRootHandles(true);

		tree.setRowHeight(30);
		tree.setCellRenderer(new ViewRenderer(iconSize));
		tree.setFont(new Font("Terminal", Font.PLAIN, 14));

		scrollPane = new JScrollPane(tree);

		scrollPane.setPreferredSize(new Dimension(1200, 800));

		contentPane.add(scrollPane, BorderLayout.CENTER);

		pack();

		loadXML(loadXML);

		TreeModeChange();

		SwingUtilities.updateComponentTreeUI(tree);

		setLocationRelativeTo(null);

		int widthadjustment = util.getOSWidthAdjustment();
		int heightadjustment = util.getOSHeightAdjustment();

		GraphicsDevice gd = util.getGraphicsDevice();

		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		Rectangle screenBounds = gc.getBounds();

		setBounds(screenBounds.x + ((screenBounds.width - ViewTree.this.getWidth()) / 2), screenBounds.y + ((screenBounds.height - ViewTree.this.getHeight()) / 2), ViewTree.this.getWidth() + widthadjustment,

				ViewTree.this.getHeight() + heightadjustment);

	}

	private void bringToFront()
	{
		ViewTree.this.setVisible(true);
		ViewTree.this.setState(Frame.NORMAL);
		ViewTree.this.toFront();
	}

	private void confirmExit()
	{
		bringToFront();

		int question = JOptionPane.showConfirmDialog(ViewTree.this, "Exit application ?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Common.app_icon);
		if (question == 0)
		{
			System.exit(0);
		}
	}

	public int getNumberOfNodes(TreeModel model)
	{
		return getNumberOfNodes(model, model.getRoot());
	}

	private int getNumberOfNodes(TreeModel model, Object node)
	{
		int count = 1;
		int nChildren = model.getChildCount(node);
		for (int i = 0; i < nChildren; i++)
		{
			count += getNumberOfNodes(model, model.getChild(node, i));
		}
		return count;
	}

	private void setExpansionLevel(int level)
	{
		if (level < 0)
			level = 0;
		treeExpandLevel = level;
		lblLevel.setText(String.valueOf(level));
	}

	private String getTranslationPath(String filename)
	{
		String result = "";

		result = "." + File.separator + "xml" + File.separator + "documents" + File.separator + "rootNode" + File.separator + getRootNodeName() + File.separator + "translations" + File.separator + filename;

		return result;
	}

	private void loadTranslations(String language)
	{

		String transpath = "." + File.separator + "xml" + File.separator + "documents" + File.separator + "rootNode" + File.separator + getRootNodeName() + File.separator + "translations";
		String iconpath = "." + File.separator + "xml" + File.separator + "documents" + File.separator + "rootNode" + File.separator + getRootNodeName() + File.separator + "icons";

		String defaulttranspath = "." + File.separator + "xml" + File.separator + "documents" + File.separator + "rootNode" + File.separator + "default" + File.separator + "translations";
		String defaulticonpath = "." + File.separator + "xml" + File.separator + "documents" + File.separator + "rootNode" + File.separator + "default" + File.separator + "icons";

		File translationfolder = new File(transpath);

		logger.debug("translationfolder=" + translationfolder);

		if (!translationfolder.exists())
		{
			try
			{
				logger.debug("mkdir=" + translationfolder);
				FileUtils.forceMkdir(translationfolder);

				File defaulttranslationfolder = new File(defaulttranspath);

				logger.debug("defaulttranslationfolder=" + defaulttranslationfolder);

				FileUtils.copyDirectory(defaulttranslationfolder, translationfolder);
			}
			catch (IOException e)
			{

			}
		}

		File iconfolder = new File(iconpath);
		logger.debug("iconfolder=" + iconfolder);

		if (!iconfolder.exists())
		{
			try
			{
				logger.debug("mkdir=" + iconfolder);
				FileUtils.forceMkdir(iconfolder);

				File defaulticonfolder = new File(defaulticonpath);

				logger.debug("defaulticonfolder=" + defaulticonfolder);

				FileUtils.copyDirectory(defaulticonfolder, iconfolder);
			}
			catch (IOException e)
			{

			}

		}

		elementNameTranslations = nonNullMap(viewTranslations.loadTranslations(getTranslationPath(elementTranslations), language));

		elementValueTranslations = nonNullMap(viewTranslations.loadTranslations(getTranslationPath(elementValuesTranslations), language));

		attributeNameTranslations = nonNullMap(viewTranslations.loadTranslations(getTranslationPath(attributeTranslations), language));

		attributeValueTranslations = nonNullMap(viewTranslations.loadTranslations(getTranslationPath(attributeValuesTranslations), language));

	}

	private static ConcurrentHashMap<String, String> nonNullMap(ConcurrentHashMap<String, String> m)
	{
		return (m != null) ? m : new ConcurrentHashMap<>();
	}

	private void loadXML(File xmlfile)
	{

		if (xmlfile != null)
		{
			setTitle(xmlfile.getAbsolutePath());

			int mode = Load.Mode_Standard;

			if (viewMode.isSelected())
			{
				mode = Load.Mode_Standard;
			}
			else
			{
				mode = Load.Mode_Flat;
			}

			Load load = new Load();

			treeModel = load.getTreeModel(xmlfile, mode);

			setRootNodeName(load.getRootElementName());

			loadTranslations("en");

			tree.setModel(treeModel);

			tree.setCellRenderer(new ViewRenderer(iconSize));

			tree.setRowHeight(rowHeight);

			TreeExpandUtil.expandToLevelAndCollapseDeeper(tree, 2);

		}
		else
		{
			Load load = new Load();
			treeModel = load.getEmptyTreeModel();
			tree.setModel(treeModel);

			tree.setCellRenderer(new ViewRenderer(iconSize));
		}

	}

	public void openTree()
	{

		loadXML = selectLoadTreeXML();

		if (loadXML != null)
		{
			loadXML(loadXML);
		}
	}

	public void reloadTree()
	{
		if (loadXML != null)
		{
			loadXML(loadXML);
		}
		else
		{
			clearTree();
		}
	}

	public void clearTree()
	{
		setTitle("");
		loadXML = null;
		loadXML(loadXML);

	}

	private void setRootNodeName(String name)
	{
		rootNodeName = util.replaceNullStringwithBlank(name).toLowerCase();
	}

	private String getRootNodeName()
	{
		rootNodeName = util.replaceNullStringwithBlank(rootNodeName).toLowerCase();

		if (rootNodeName.equals(""))
		{
			rootNodeName = "default";
		}

		return rootNodeName;
	}

	private File selectLoadTreeXML()
	{
		logger.debug("selectLoadTreeXML");

		File result = null;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(Common.treeFolderFile);

		JFileFilterXML ffi = new JFileFilterXML();
		fileChooser.setApproveButtonText("Open");
		fileChooser.addChoosableFileFilter(ffi);
		fileChooser.setFileFilter(ffi);
		fileChooser.setMultiSelectionEnabled(false);

		int returnVal = fileChooser.showOpenDialog(ViewTree.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			result = fileChooser.getSelectedFile();
			Common.treeFolderFile = new File(result.getParent());
		}
		logger.debug("selectLoadTreeXML result=[" + result);

		return result;
	}

	private void TreeModeChange()
	{

		if (viewMode.isSelected())
		{
			viewMode.setIcon(Common.icon_mode1);
			lblViewMode_Status.setText("Standard ");
		}
		else
		{
			viewMode.setIcon(Common.icon_mode2);
			viewMode.setIcon(Common.icon_mode1);
			lblViewMode_Status.setText("Flatten  ");
		}

		if (viewIcons.isSelected())
		{
			viewIcons.setIcon(Common.app_icon_on);
			lblIconMode_Status.setText("ON ");
		}
		else
		{
			viewIcons.setIcon(Common.app_icon_off);
			lblIconMode_Status.setText("OFF");
		}

		if (viewTrans.isSelected())
		{
			viewTrans.setIcon(Common.app_trans_on);
			lblTransMode_Status.setText("ON ");
		}
		else
		{
			viewTrans.setIcon(Common.app_trans_off);
			lblTransMode_Status.setText("OFF");
		}

		if (viewBrackets.isSelected())
		{
			viewBrackets.setIcon(Common.app_brackets_on);
			lblBracketMode_Status.setText("ON ");
		}
		else
		{
			viewBrackets.setIcon(Common.app_brackets_off);
			lblBracketMode_Status.setText("OFF");
		}

	}

}
