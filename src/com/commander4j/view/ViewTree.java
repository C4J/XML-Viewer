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
import java.nio.file.LinkOption;
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
import com.commander4j.enu.TreeAction;
import com.commander4j.gui.JButton4j;
import com.commander4j.gui.JComboBox4j;
import com.commander4j.gui.JLabel4j_std;
import com.commander4j.gui.JToggleButton4j;
import com.commander4j.sys.Common;
import com.commander4j.util.JFileFilterXML;
import com.commander4j.util.JHelp;
import com.commander4j.util.TreeExpandUtil;
import com.commander4j.util.Utility;

public final class ViewTree extends JFrame
{
	public static String title1 = "XML Viewer - Version ";
	public static String version = "1.32";

	private static final long serialVersionUID = 1L;

	public static ConcurrentHashMap<String, String> xmlTranslations = new ConcurrentHashMap<String, String>();

	private Utility util = new Utility();

	private JPanel contentPane;

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
	private JButton4j btnRefreshTranslation;
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

	JLabel4j_std lbl_Translations = new JLabel4j_std("Translation File : ");
	JLabel4j_std lbl_Languages = new JLabel4j_std("Language : ");
	JComboBox4j<String> combobox_Translations = new JComboBox4j<String>();
	JComboBox4j<String> combobox_Languages = new JComboBox4j<String>();

	private JScrollPane scrollPane;

	private File loadXML;

	private JSeparator sep = new JSeparator();
	private JSeparator sep2 = new JSeparator();
	private JSeparator sep3 = new JSeparator();

	private ViewTranslations viewTranslations = new ViewTranslations();

	private Dimension buttonSize = new Dimension(32, 32);
	private Dimension blankSize = new Dimension(10, 32);
	private Dimension labelSize = new Dimension(27, 27);

	private JLabel4j_std lblLevel = new JLabel4j_std();
	private JLabel4j_std lblViewMode = new JLabel4j_std("View Mode : ");
	private JLabel4j_std lblIconMode = new JLabel4j_std("  Icon Mode : ");
	private JLabel4j_std lblTransMode = new JLabel4j_std("  Translation Mode : ");
	private JLabel4j_std lblBracketMode = new JLabel4j_std("  Bracket Mode : ");

	private JLabel4j_std lblViewMode_Status = new JLabel4j_std("");
	private JLabel4j_std lblIconMode_Status = new JLabel4j_std("");
	private JLabel4j_std lblTransMode_Status = new JLabel4j_std("");
	private JLabel4j_std lblBracketMode_Status = new JLabel4j_std("");

	private int iconSize = 24;
	private int rowHeight = iconSize + 3;

	private JTree tree;
	private DefaultTreeModel treeModel;
	private ViewRenderer treeRenderer = new ViewRenderer(iconSize);

	private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ViewTree.class);

	boolean initialising = true;

	public static void main(String[] args)
	{

		String filename = "";

		if (args.length > 0)
		{
			filename = args[0];
		}

		ViewTree f = new ViewTree(filename);

		f.setVisible(true);
	}

	private File getFileFromString(String filename)
	{
		File result = null;

		filename = util.replaceNullStringwithBlank(filename);

		if (filename.equals("") == false)
		{

			if (FileUtils.isRegularFile(new File(filename), LinkOption.NOFOLLOW_LINKS))
			{
				result = new File(filename);
			}

		}

		return result;
	}

	public ViewTree(String filename)
	{
		initialising = true;
		util.setLookAndFeel("Nimbus");
		util.initLogging("");

		Common.viewConfig.load();

		lblLevel.setText(String.valueOf(Common.viewConfig.getTreeExpansion()));

		if (filename.equals(""))
		{
			loadXML = Common.viewConfig.getFile();
		}
		else
		{
			loadXML = getFileFromString(filename);
		}

		btnBlank1 = new JButton4j(Common.icon_blank);
		btnBlank2 = new JButton4j(Common.icon_blank);
		btnOpen = new JButton4j(Common.icon_file_open);
		btnReload = new JButton4j(Common.icon_reload);
		btnRefreshTranslation = new JButton4j(Common.icon_reload);
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

		loadTranslations();

		sep.setAlignmentX(JSeparator.CENTER_ALIGNMENT);
		sep.setOrientation(JSeparator.VERTICAL);
		sep.setMaximumSize(labelSize);

		sep2.setAlignmentX(JSeparator.CENTER_ALIGNMENT);
		sep2.setOrientation(JSeparator.VERTICAL);
		sep2.setMaximumSize(labelSize);

		sep3.setAlignmentX(JSeparator.CENTER_ALIGNMENT);
		sep3.setOrientation(JSeparator.VERTICAL);
		sep3.setMaximumSize(labelSize);

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
		contentPane.add(toolBarSide, BorderLayout.EAST);

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
		viewMode.setSelected(Common.viewConfig.isViewMode());
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
		viewIcons.setSelected(Common.viewConfig.isViewIcons());
		viewIcons.setToolTipText("View Icons");
		viewIcons.setPreferredSize(buttonSize);
		viewIcons.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeModeChange();
			}
		});
		toolBarSide.add(viewIcons);

		viewTrans = new JToggleButton4j();
		viewTrans.setSelected(Common.viewConfig.isViewTrans());
		viewTrans.setToolTipText("View Translations");
		viewTrans.setPreferredSize(buttonSize);
		viewTrans.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeModeChange();
			}
		});
		toolBarSide.add(viewTrans);

		viewBrackets = new JToggleButton4j();
		viewBrackets.setSelected(Common.viewConfig.isViewBrackets());
		viewBrackets.setToolTipText("View Brackets");
		viewBrackets.setPreferredSize(buttonSize);
		viewBrackets.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeModeChange();
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
				expandTree(TreeAction.ExpandAll);
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
				expandTree(TreeAction.ExpandSelectedPath);
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
				expandTree(TreeAction.ExpandLevelPlus);
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
				expandTree(TreeAction.CollapseAll);
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
				expandTree(TreeAction.CollapseSelectedPath);

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
				expandTree(TreeAction.ExpandLevelMinus);
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

		toolBarTop.add(sep3);

		lbl_Translations.setFont(Common.font_status_bar_label);
		toolBarTop.add(lbl_Translations);
		combobox_Translations.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				Common.viewConfig.setTranslation((String) combobox_Translations.getSelectedItem());
				Common.viewConfig.save();

				loadTranslations();
				TreeModeChange();

			}
		});

		combobox_Translations.setFont(Common.font_status_bar_label);
		combobox_Translations.setPreferredSize(new Dimension(200, 24));
		combobox_Translations.setMinimumSize(new Dimension(200, 24));
		combobox_Translations.setMaximumSize(new Dimension(200, 24));
		combobox_Translations.setSize(new Dimension(200, 24));
		combobox_Translations.setModel(viewTranslations.populateFiles(Common.viewConfig.getTranslation()));

		toolBarTop.add(combobox_Translations);

		btnRefreshTranslation.setToolTipText("Refresh Translations");
		toolBarTop.add(btnRefreshTranslation);
		btnRefreshTranslation.setPreferredSize(buttonSize);
		btnRefreshTranslation.setFocusable(false);
		btnRefreshTranslation.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				Common.viewConfig.setTranslation((String) combobox_Translations.getSelectedItem());
				Common.viewConfig.save();

				loadTranslations();
				TreeModeChange();

			}
		});

		lbl_Languages.setFont(Common.font_status_bar_label);
		toolBarTop.add(lbl_Languages);
		combobox_Languages.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				Common.viewConfig.setLanguage((String) combobox_Languages.getSelectedItem());
				Common.viewConfig.save();

				loadTranslations();
				TreeModeChange();

			}
		});

		combobox_Languages.setFont(Common.font_status_bar_label);
		combobox_Languages.setPreferredSize(new Dimension(50, 24));
		combobox_Languages.setMinimumSize(new Dimension(50, 24));
		combobox_Languages.setMaximumSize(new Dimension(50, 24));
		combobox_Languages.setSize(new Dimension(50, 24));
		combobox_Languages.setModel(Common.viewConfig.getLanguageOptions());

		toolBarTop.add(combobox_Languages);

		tree = new JTree(new DefaultMutableTreeNode("No document"));
		tree.setBackground(Common.color_app_window);
		tree.setShowsRootHandles(true);

		tree.setRowHeight(rowHeight);
		tree.setCellRenderer(treeRenderer);
		tree.setFont(new Font("Terminal", Font.PLAIN, 14));

		scrollPane = new JScrollPane(tree);

		scrollPane.setPreferredSize(new Dimension(1200, 800));

		contentPane.add(scrollPane, BorderLayout.CENTER);

		pack();

		initialising = false;


		Common.viewConfig.setFile(loadXML);
		loadXML(loadXML);
		buttonState();


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
			Common.viewConfig.save();
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

	private void loadTranslations()
	{

		String transpath = "." + File.separator + "xml" + File.separator + "translations" + File.separator + Common.viewConfig.getTranslation();

		xmlTranslations = nonNullMap(viewTranslations.loadTranslations(transpath, Common.viewConfig.getLanguage()));

	}

	private static ConcurrentHashMap<String, String> nonNullMap(ConcurrentHashMap<String, String> m)
	{
		return (m != null) ? m : new ConcurrentHashMap<>();
	}

	private void setFrameTitle(File file)
	{
		String filename = "";

		String title = "";

		if (file != null)
		{
			filename = "    [" + file.getAbsolutePath() + "]";
		}

		title = title1 + version + "  " + filename;

		setTitle(title);

	}

	private void loadXML(File xmlfile)
	{

		setFrameTitle(xmlfile);

		if (xmlfile != null)
		{

			int mode = Load.Mode_Standard;

			if (viewMode.isSelected())
			{
				mode = Load.Mode_Flat;

			}
			else
			{
				mode = Load.Mode_Standard;
			}

			Load load = new Load();

			treeModel = load.getTreeModel(xmlfile, mode);

			loadTranslations();

			tree.setModel(treeModel);

			tree.setRowHeight(rowHeight);

		}
		else
		{
			Load load = new Load();
			treeModel = load.getEmptyTreeModel();
			tree.setModel(treeModel);
		}

		expandTree(TreeAction.ExpandToLevel);

	}

	private void expandTree(TreeAction level)
	{

		if (initialising == false)
		{
			if (level == TreeAction.ExpandToLevel)
			{
				Thread expand = new Thread()
				{
					public void run()
					{
						TreeExpandUtil.expandToLevelAndCollapseDeeper(tree, Common.viewConfig.getTreeExpansion());
					}
				};

				SwingUtilities.invokeLater(expand);
			}

			if (level == TreeAction.ExpandLevelMinus)
			{
				Thread expand = new Thread()
				{
					public void run()
					{
						TreeExpandUtil.expandToLevelAndCollapseDeeper(tree, Common.viewConfig.reduceTreeExpansion(lblLevel));
					}
				};

				SwingUtilities.invokeLater(expand);

			}

			if (level == TreeAction.CollapseAll)
			{
				Thread expand = new Thread()
				{
					public void run()
					{
						Common.viewConfig.setTreeExpansion(0, lblLevel);
						TreeExpandUtil.collapseAll(tree);
					}
				};

				SwingUtilities.invokeLater(expand);

			}

			if (level == TreeAction.CollapseSelectedPath)
			{
				Thread expand = new Thread()
				{
					public void run()
					{
						TreeExpandUtil.collapseSelectedPath(tree);
					}
				};

				SwingUtilities.invokeLater(expand);

			}

			if (level == TreeAction.ExpandAll)
			{
				Thread expand = new Thread()
				{
					public void run()
					{
						TreeExpandUtil.expandAll(tree);
					}
				};

				SwingUtilities.invokeLater(expand);

			}

			if (level == TreeAction.ExpandSelectedPath)
			{
				Thread expand = new Thread()
				{
					public void run()
					{
						TreeExpandUtil.expandSelectedPath(tree);
					}
				};

				SwingUtilities.invokeLater(expand);

			}

			if (level == TreeAction.ExpandLevelPlus)
			{
				Thread expand = new Thread()
				{
					public void run()
					{
						TreeExpandUtil.expandToLevelAndCollapseDeeper(tree, Common.viewConfig.increaseTreeExpansion(lblLevel));
					}
				};

				SwingUtilities.invokeLater(expand);

			}
		}
	}

	public void openTree()
	{

		loadXML = selectLoadTreeXML();

		if (loadXML != null)
		{
			Common.viewConfig.setFile(loadXML);
			Common.viewConfig.save();
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
		Common.viewConfig.setFile(loadXML);
		Common.viewConfig.save();
		loadXML(loadXML);
		combobox_Translations.setSelectedItem("default.xml");

	}

	private File selectLoadTreeXML()
	{
		logger.debug("selectLoadTreeXML");

		File result = null;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(Common.viewConfig.getFile());

		JFileFilterXML ffi = new JFileFilterXML();
		fileChooser.setApproveButtonText("Open");
		fileChooser.addChoosableFileFilter(ffi);
		fileChooser.setFileFilter(ffi);
		fileChooser.setMultiSelectionEnabled(false);

		int returnVal = fileChooser.showOpenDialog(ViewTree.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			result = fileChooser.getSelectedFile();
			Common.viewConfig.setFile(result);
		}

		logger.debug("selectLoadTreeXML result=[" + result);

		return result;
	}

	private void buttonState()
	{
		Common.viewConfig.setViewMode(viewMode.isSelected());
		Common.viewConfig.setViewTrans(viewTrans.isSelected());
		Common.viewConfig.setViewIcons(viewIcons.isSelected());
		Common.viewConfig.setViewBrackets(viewBrackets.isSelected());

		if (viewMode.isSelected())
		{

			viewMode.setIcon(Common.icon_mode2);
			lblViewMode_Status.setText("Standard ");
		}
		else

		{
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


	private void TreeModeChange()
	{

		buttonState();

		treeModel.reload();

		expandTree(TreeAction.ExpandToLevel);

	}

}
