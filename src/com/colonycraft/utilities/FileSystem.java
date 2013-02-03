package com.colonycraft.utilities;

import java.io.File;

public class FileSystem
{
	
	private static final int OS_UNKNOWN = 0;
	private static final int OS_WINDOWS = 1;
	private static final int OS_LINUX = 2;
	private static final int OS_OSX = 3;
	
	
	private int os = OS_UNKNOWN;
	
	private File personalDir;
	private File appDataDir;

	public FileSystem()
	{
		String osname = System.getProperty("os.name").toLowerCase();
		if (osname.contains("windows"))
		{
			os = OS_WINDOWS;
		} else if (osname.contains("nux") || osname.contains("nix"))
		{
			os = OS_LINUX;
		} else if (osname.contains("mac os x"))
		{
			os = OS_OSX;
		} else
		{
			System.out.println("Unknown OS");
		}
		
		personalDir = locatePersonalDir();
		appDataDir = locateAppData();
		
		appDataDir.mkdirs();
	}
	
	private File locateAppData()
	{
		if (os == OS_LINUX)
		{
			return getPersonalFile(".colonycraft");
		} else if (os == OS_OSX)
		{
			return getPersonalFile("Library/Application Support/Colonycraft");
		} else if (os == OS_WINDOWS)
		{
			return new File(new File(System.getenv("LOCALAPPDATA")), "Colonycraft");
		}
		return null;
	}


	private File locatePersonalDir()
	{
		if (os == OS_LINUX)
		{
			return new File("/home/" + System.getProperty("user.name") + "/");
		} else
		{
			return new File(System.getProperty("user.home"));
		}
	}
	
	public File getPersonalFile(String path)
	{
		return new File(personalDir, path);
	}
	
	public File getAppDataFile(String path)
	{
		return new File(appDataDir, path);
	}
	
	
}
