package com.commander4j.sys;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import com.commander.renderer.JDBListRenderer;

public class Common {

	public static String helpURL = "http://wiki.commander4j.com";

	public static final JDBListRenderer renderer_list = new JDBListRenderer();

	public static File treeFolderFile = null;
	public static File treeFolderPath = null;

	public static String osName = "";

	public static JTree tree;
	public static DefaultTreeModel treeModel;
	public static boolean treeChanged = false;

	public final static Font font_dates = new Font("Arial", Font.PLAIN, 11);
	public final static Font font_std = new Font("Arial", Font.PLAIN, 11);
	public final static Font font_input = new Font("Arial", Font.PLAIN, 11);
	public final static Font font_input_large = new Font("Arial", Font.PLAIN, 13);
	public final static Font font_popup = new Font("Arial", Font.PLAIN, 11);
	public final static Font font_bold = new Font("Arial", Font.BOLD, 11);
	public final static Font font_italic = new Font("Arial", Font.ITALIC, 11);
	public final static Font font_btn = new Font("Arial", Font.PLAIN, 11);
	public final static Font font_btn_bold = new Font("Arial", Font.BOLD, 9);
	public final static Font font_btn_small = new Font("Arial", Font.PLAIN, 9);
	public final static Font font_btn_small_bold = new Font("Arial", Font.BOLD, 9);
	public final static Font font_title = new Font("Arial", Font.ITALIC, 12);
	public final static Font font_tree = new Font("Arial", Font.PLAIN, 12);
	public final static Font font_menu = new Font("Arial", Font.PLAIN, 12);
	public final static Font font_list = new Font("Monospaced", 0, 11);
	public final static Font font_list_weights = new Font("Monospaced", 0, 14);
	public final static Font font_status_bar_label = new Font("Monospaced", Font.PLAIN, 11);
	public final static Font font_status_bar_status = new Font("Monospaced", Font.BOLD, 11);
	public final static Font font_combo = new Font("Monospaced", Font.PLAIN, 11);
	public final static Font font_table_header = new java.awt.Font("Arial", Font.PLAIN, 11);
	public final static Font font_table = new java.awt.Font("Monospaced", 0, 11);
	public final static Font font_textArea = new java.awt.Font("Monospaced", 0, 14);
	public final static Font font_tree_tooltip = new Font( "Monospaced", Font.PLAIN, 14);
	public final static Font font_bom = new Font("Arial", Font.PLAIN, 14);
	public final static Font font_tree_branch = new Font("Arial", Font.BOLD, 13);
	public final static Font font_tree_leaf = new Font("Arial", Font.PLAIN, 11);
	public final static Font font_tree_root = new Font("Arial", Font.BOLD, 14);
	public final static Font font_terminal = new Font("Dialog", Font.PLAIN, 14);

	public final static Color color_textfield_foreground_focus_color = Color.BLACK;
	public final static Color color_textfield_forground_nofocus_color = Color.BLACK;
	public final static Color color_textfield_background_focus_color = new Color(255, 255, 200);
	public final static Color color_textfield_background_nofocus_color = Color.WHITE;
	public final static Color color_text_maxsize_color = Color.RED;
	public final static Color color_list_assigned = new Color(233, 255, 233);
	public final static Color color_list_unassigned = new Color(255, 240, 255);
	public final static Color color_listFontStandard = Color.BLUE;
	public final static Color color_listFontSelected = Color.BLACK;
	public final static Color color_listBackground = new Color(243,251,255);
	public final static Color color_listHighlighted = new Color(184, 207, 229);
	public final static Color color_tablerow1 = new Color(248, 226, 226);
	public final static Color color_tablerow2 = new Color(240,255,240);
	public final static Color color_tablerow3 = new Color(204, 255, 204);
	public final static Color color_tablebackground = new Color(233, 240, 249);
	public final static Color color_tableHeaderFont = Color.BLACK;
	public final static Color color_text_disabled = Color.BLACK;
	public final static Color color_edit_properties = new Color(241, 241, 241);
	public final static Color color_app_window = new Color(241, 241, 241);
	public final static Color color_button = new Color(233,236,242);
	public final static Color color_button_hover =  new Color(160, 160, 160);
	public final static Color color_button_font = Color.black;
	public final static Color color_button_font_hover = Color.black;
	public final static Color color_textfield_foreground_disabled = Color.BLUE;
	public final static Color color_textfield_background_disabled = new Color(241, 241, 241);

	public static String appIconPath = "."+File.separator+"images"+File.separator+"appIcons"+File.separator;
	public static String xmlIconPath = "."+File.separator+"images"+File.separator+"xmlIcons"+File.separator;




	public final static ImageIcon app_icon_on = new ImageIcon(appIconPath+"icons_on_24x24.png");
	public final static ImageIcon app_icon_off = new ImageIcon(appIconPath+"icons_off_24x24.png");
	public final static ImageIcon app_brackets_on = new ImageIcon(appIconPath+"brackets_on_24x24.png");
	public final static ImageIcon app_brackets_off = new ImageIcon(appIconPath+"brackets_off_24x24.png");
	public final static ImageIcon app_trans_on = new ImageIcon(appIconPath+"trans_on_24x24.png");
	public final static ImageIcon app_trans_off = new ImageIcon(appIconPath+"trans_off_24x24.png");
	public final static ImageIcon app_icon = new ImageIcon(appIconPath+"icon_32x32.png");
	public final static ImageIcon icon_mode1 = new ImageIcon(appIconPath+"mode1_24x24.png");
	public final static ImageIcon icon_mode2 = new ImageIcon(appIconPath+"mode2_24x24.png");
	public final static ImageIcon tray_icon_mac = new ImageIcon(appIconPath+"TrayIcon_Mac.png");
	public final static ImageIcon tray_icon_windows = new ImageIcon(appIconPath+"TrayIcon_Windows.png");
	public final static ImageIcon tray_icon_linux = new ImageIcon(appIconPath+"TrayIcon_Linux.png");
	public final static ImageIcon icon_left_arrow = new ImageIcon(appIconPath+"left_arrow_24x24.png");
	public final static ImageIcon icon_menuStructure = new ImageIcon(appIconPath+"folder_tree.png");
	public final static ImageIcon icon_select_folder = new ImageIcon(appIconPath+"/select_folder_24x24.png");
	public final static ImageIcon icon_select_file = new ImageIcon(appIconPath+"/select_file_24x24.png");
	public final static ImageIcon icon_add = new ImageIcon(appIconPath+"/add_24x24.png");
	public final static ImageIcon icon_open = new ImageIcon(appIconPath+"/open_file_24x24.png");
	public final static ImageIcon icon_new = new ImageIcon(appIconPath+"/new_file_24x24.png");
	public final static ImageIcon icon_save = new ImageIcon(appIconPath+"/save_24x24.png");
	public final static ImageIcon icon_password = new ImageIcon(appIconPath+"/password_24x24.png");
	public final static ImageIcon icon_erase = new ImageIcon(appIconPath+"/eraser_24x24.png");
	public final static ImageIcon icon_font = new ImageIcon(appIconPath+"/font_24x24.png");
	public final static ImageIcon icon_duplicate = new ImageIcon(appIconPath+"/duplicate_24x24.png");
	public final static ImageIcon icon_release = new ImageIcon(appIconPath+"/release_24x24.png");
	public final static ImageIcon icon_hold = new ImageIcon(appIconPath+"/hold_24x24.png");
	public final static ImageIcon icon_button_key = new ImageIcon(appIconPath+"/button_key_24x24.png");
	public final static ImageIcon icon_about = new ImageIcon(appIconPath+"/about_24x24.png");
	public final static ImageIcon icon_help = new ImageIcon(appIconPath+"/help_24x24.png");
	public final static ImageIcon icon_exit = new ImageIcon(appIconPath+"/exit_24x24.png");
	public final static ImageIcon icon_ok = new ImageIcon(appIconPath+"/ok_24x24.png");
	public final static ImageIcon icon_cancel = new ImageIcon(appIconPath+"/cancel_24x24.png");
	public final static ImageIcon icon_delete = new ImageIcon(appIconPath+"/delete_24x24.png");
	public final static ImageIcon icon_edit = new ImageIcon(appIconPath+"/edit_24x24.png");
	public final static ImageIcon icon_file_new = new ImageIcon(appIconPath+"/exit_24x24.gif");
	public final static ImageIcon icon_file_open = new ImageIcon(appIconPath+"/open_file_24x24.png");
	public final static ImageIcon icon_expandAll = new ImageIcon(appIconPath+"/expandall_24x24.png");
	public final static ImageIcon icon_blank = new ImageIcon(appIconPath+"/blank_24x24.png");
	public final static ImageIcon icon_expandNode = new ImageIcon(appIconPath+"/expandnode_24x24.png");
	public final static ImageIcon icon_expandPlus = new ImageIcon(appIconPath+"/expandplus_24x24.png");
	public final static ImageIcon icon_expandMinus = new ImageIcon(appIconPath+"/expandminus_24x24.png");
	public final static ImageIcon icon_collapseAll = new ImageIcon(appIconPath+"/collapseall_24x24.png");
	public final static ImageIcon icon_collapeNode = new ImageIcon(appIconPath+"/collapsenode_24x24.png");
	public final static ImageIcon icon_execute = new ImageIcon(appIconPath+"/execute_24x24.png");
	public final static ImageIcon icon_settings = new ImageIcon(appIconPath+"/settings_24x24.png");
	public final static ImageIcon icon_reload = new ImageIcon(appIconPath+"/refresh_24x24.png");
	public final static ImageIcon icon_function = new ImageIcon(appIconPath+"/function.gif");
	public final static ImageIcon icon_info = new ImageIcon(appIconPath+"/info.gif");
	public final static ImageIcon icon_branchOpen = new ImageIcon(appIconPath+"/folder_open.png");
	public final static ImageIcon icon_branchClose = new ImageIcon(appIconPath+"/folder_closed.png");
	public final static ImageIcon icon_confirm = new ImageIcon(appIconPath+"/Icon_Menu4j.png");
	public final static ImageIcon icon_license = new ImageIcon(appIconPath+"/open_source_24x24.png");

}

