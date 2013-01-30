package com.colonycraft.rendering;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShaderStorage
{

	private static Map<String, Shader> shaders = new HashMap<String, Shader>();
	
	public static Shader getShader(String name)
	{
		return shaders.get(name);
	}
	
	public static void loadShader(String name) throws IOException
	{
		Shader shader = new Shader(name, name + ".glsl");
		shaders.put(name, shader);
	}
	
	public static void unloadShaders()
	{
		for (Shader s : shaders.values())
		{
			s.cleanup();
		}
		shaders.clear();
	}
	
}
