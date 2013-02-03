package com.colonycraft;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class Configuration
{

	private File configurationFile;

	private Properties props;

	public Configuration()
	{
		configurationFile = ColonyCraft.getIntance().getFS().getAppDataFile("config.txt");
		System.out.println(configurationFile);
		loadDefaultConf();
		loadConfFile();
		saveConfFile();
	}

	private void loadDefaultConf()
	{
		props = new Properties();
		props.put("vsync", "false");
		props.put("fps", "60");
		props.put("viewingdistance", "100");
		props.put("fullscreen", "true");
		props.put("width", String.valueOf(Toolkit.getDefaultToolkit().getScreenSize().width));
		props.put("height", String.valueOf(Toolkit.getDefaultToolkit().getScreenSize().height));
	}

	private void loadConfFile()
	{
		if (configurationFile.exists() && configurationFile.canRead())
		{
			try
			{
				props.load(new BufferedReader(new InputStreamReader(new FileInputStream(configurationFile), "UTF-8")));
				System.out.println("Load Successful");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		} else
		{
			System.out.println("No existing configuration file found.");
		}
	}

	private void saveConfFile()
	{
		try
		{
			props.store(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configurationFile), "UTF-8")), "Colonycraft Config File");
			System.out.println("Save Successful");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int getFPS()
	{
		return Integer.parseInt(props.getProperty("fps"));
	}
	
	public int getViewingDistance()
	{
		return Integer.parseInt(props.getProperty("viewingdistance"));
	}
	
	public boolean getVSync()
	{
		return Boolean.parseBoolean(props.getProperty("vsync"));
	}
	
	public boolean getFullscreen()
	{
		return Boolean.parseBoolean(props.getProperty("fullscreen"));
	}
	
	public int getWidth()
	{
		return Integer.parseInt(props.getProperty("width"));
	}
	
	public int getHeight()
	{
		return Integer.parseInt(props.getProperty("height"));
	}

}
