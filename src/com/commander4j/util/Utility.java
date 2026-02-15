package com.commander4j.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;


public class Utility
{


	public void initLogging(String filename)
	{
		if (filename.isEmpty())
		{
			filename = "xml" + File.separator + "log" + File.separator + "log4j2.xml";
		}

		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		File file = new File(filename);

		// this will force a reconfiguration
		context.setConfigLocation(file.toURI());

	}

	public  GraphicsDevice getGraphicsDevice()
	{
		GraphicsDevice result;

		Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		GraphicsDevice[] devices;

		try
		{
			devices = ge.getScreenDevices();

			GraphicsDevice currentDevice = null;

			for (GraphicsDevice device : devices)
			{
				Rectangle bounds = device.getDefaultConfiguration().getBounds();
				if (bounds.contains(mouseLocation))
				{
					currentDevice = device;
					break;
				}
			}

			GraphicsDevice[] gs = ge.getScreenDevices();

			String defaultID = currentDevice.getIDstring();

			int monitorIndex = 0;

			for (int x = 0; x < gs.length; x++)
			{
				if (gs[x].getIDstring().equals(defaultID))
				{
					monitorIndex = x;
					break;
				}
			}

			result = gs[monitorIndex];
		}
		catch (HeadlessException ex)
		{
			result = null;
		}

		return result;
	}

	public  int getOSWidthAdjustment()
	{
		int result = 0;
		if (isWindows())
		{
			result = 0;
		}
		if (isMac())
		{
			result = -15;
		}
		if (isSolaris())
		{
			result = 0;
		}
		if (isUnix())
		{
			result = 0;
		}
		return result;
	}

	public  int getOSHeightAdjustment()
	{
		int result = 0;
		if (isWindows())
		{
			result = 0;
		}
		if (isMac())
		{
			result = -13;
		}
		if (isSolaris())
		{
			result = 0;
		}
		if (isUnix())
		{
			result = 0;
		}
		return result;
	}

	public  boolean isWindows() {

		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);

	}

	public  boolean isMac() {

		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);

	}

	public  boolean isUnix() {

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	public  boolean isSolaris() {

		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);

	}

	public void setLookandFeel()
	{

		try
		{
			SetLookAndFeel("Metal", "Ocean");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void SetLookAndFeel(String LOOKANDFEEL, String THEME)
	{
		try
		{
			if (LOOKANDFEEL.equals("Metal"))
			{
				if (THEME.equals("DefaultMetal"))
					MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
				else if (THEME.equals("Ocean"))
					MetalLookAndFeel.setCurrentTheme(new OceanTheme());

				UIManager.setLookAndFeel(new MetalLookAndFeel());

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setLookAndFeel(String LAF)
	{
		try
		{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if (LAF.equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (Exception e)
		{

		}
	}

	public String padSpace(int size)
	{
		String s = "";

		for (int i = 0; i < size; i++)
		{
			s = s + " ";
		}

		return s;
	}

	public String padString(int size, String character)
	{
		String s = "";

		for (int i = 0; i < size; i++)
		{
			s = s + character;
		}

		return s;
	}

	public String padString(String input, boolean right, int size, String character)
	{
		int inputlength = 0;
		String result = replaceNullStringwithBlank(input);

		inputlength = result.length();

		if (inputlength > size)
		{
			// result = result.substring(0,size-1);
			result = result.substring(0, size);
		} else
		{
			if (inputlength < size)
			{
				if (right == true)
				{
					result = result + padString(size - inputlength, character);
				} else
				{
					result = padString(size - inputlength, character) + result;
				}
			}
		}

		return result;
	}

	public String replaceNullObjectwithBlank(Object value)
	{
		String result = "";

		if (value != null)
		{
			result = value.toString();
		}

		return result;
	}

	public String replaceNullStringwithBlank(String value)
	{
		if (value == null)
		{
			value = "";
		}

		return value;
	}

	public String left(String inputstr, int size)
	{
		String result = replaceNullStringwithBlank(inputstr);

		if (size > inputstr.length())
		{
			size = inputstr.length();
		}

		if (size >= 0)
		{
			result = inputstr.substring(0, size);
		}
		else
		{
			result = "";
		}

		return result;
	}


}
