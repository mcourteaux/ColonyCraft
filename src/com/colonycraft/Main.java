package com.colonycraft;

import java.lang.reflect.Field;
import java.util.Arrays;

public class Main
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			loadNativeLibs();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		ColonyCraft.createInstance();
		ColonyCraft colonyCraft = ColonyCraft.getIntance();
		colonyCraft.init();
	}

	private static void loadNativeLibs() throws Exception
	{
		if (System.getProperty("os.name").equals("Mac OS X"))
		{
			addLibraryPath("natives/macosx");
		} else if (System.getProperty("os.name").equals("Linux"))
		{
			addLibraryPath("natives/linux");
		} else
		{
			addLibraryPath("natives/windows");
		}
	}

	private static void addLibraryPath(String s) throws Exception
	{
		final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		final String[] paths = (String[]) usrPathsField.get(null);

		for (String path : paths)
		{
			if (path.equals(s))
			{
				return;
			}
		}

		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = s;
		usrPathsField.set(null, newPaths);
	}

}
