package com.colonycraft.rendering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader
{

	private static final String[] PREPROCESSOR_MACROS = { "#VERTEX", "#FRAGMENT" };

	private static Shader LAST_USED_SHADER = null;

	private String name;
	private String file;

	private int program;
	private int vertexShader;
	private int fragmentShader;

	public Shader(String name, String file) throws IOException
	{
		this.name = name;
		this.file = file;

		try
		{
			loadShader();
		} catch (ShaderException e)
		{
			e.printStackTrace();
			cleanup();
		}
	}

	private void loadShader() throws IOException
	{

		StringBuilder[] codeBuilders = new StringBuilder[PREPROCESSOR_MACROS.length];
		for (int i = 0; i < codeBuilders.length; ++i)
		{
			codeBuilders[i] = new StringBuilder();
		}

		loadSourceFile(getShaderFile(), codeBuilders, -1);

		createShader(codeBuilders[0].toString(), codeBuilders[1].toString());
		createProgram();
	}

	private void loadSourceFile(File file, StringBuilder[] codeBuilders, int fixedBuilder) throws IOException
	{
		int currentBuilder = -1;

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line = null;
		outer: while ((line = br.readLine()) != null)
		{
			String trimmedLine = line.trim();
			if (trimmedLine.startsWith("#include"))
			{
				String filename = trimmedLine.substring("#include  ".length(), trimmedLine.length() - 1);
				File sFile = getSiblingFile(filename);

				for (int i = 0; i < codeBuilders.length; ++i)
				{
					if (i == currentBuilder || currentBuilder == -1)
					{
						loadSourceFile(sFile, codeBuilders, i);
					}
				}
				continue;
			} else if (trimmedLine.startsWith("#"))
			{

				for (int i = 0; i < PREPROCESSOR_MACROS.length; ++i)
				{
					if (trimmedLine.equals(PREPROCESSOR_MACROS[i]))
					{
						currentBuilder = i;
						continue outer;
					}
				}
			}
			for (int i = 0; i < codeBuilders.length; ++i)
			{
				if (i == currentBuilder || (currentBuilder == -1 && fixedBuilder == -1) || fixedBuilder == i)
					codeBuilders[i].append(line).append("\n");
			}
		}
		br.close();

	}

	private void createProgram()
	{
		program = GL20.glCreateProgram();

		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);
		GL20.glLinkProgram(program);

		int status = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
		if (status == GL11.GL_FALSE)
		{
			int len = GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH);
			String str = GL20.glGetProgramInfoLog(program, len);
			throw new ShaderException(str);
		}
	}

	private void createShader(String vertexShaderCode, String fragmentShaderCode)
	{

		System.out.println("VertexShader: \n" + vertexShaderCode);
		System.out.println("FragmentShader: \n" + fragmentShaderCode);

		vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

		int status;

		GL20.glShaderSource(vertexShader, vertexShaderCode);
		GL20.glCompileShader(vertexShader);
		status = GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS);

		if (status == GL11.GL_FALSE)
		{
			int logLen = GL20.glGetShaderi(vertexShader, GL20.GL_INFO_LOG_LENGTH);
			String str = GL20.glGetShaderInfoLog(vertexShader, logLen);

			throw new ShaderException("VertexShader: " + str);
		}

		GL20.glShaderSource(fragmentShader, fragmentShaderCode);
		GL20.glCompileShader(fragmentShader);
		status = GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS);

		if (status == GL11.GL_FALSE)
		{
			int logLen = GL20.glGetShaderi(fragmentShader, GL20.GL_INFO_LOG_LENGTH);
			String str = GL20.glGetShaderInfoLog(fragmentShader, logLen);

			throw new ShaderException("FragmentShader: " + str);
		}
	}

	public void cleanup()
	{
		GL20.glDeleteShader(vertexShader);
		GL20.glDeleteShader(fragmentShader);
		GL20.glDeleteProgram(program);

		vertexShader = 0;
		fragmentShader = 0;
		program = 0;
	}

	public String getName()
	{
		return name;
	}

	private File getShaderFile()
	{
		return new File("res/shaders/" + file);
	}

	private File getSiblingFile(String file)
	{
		return new File("res/shaders/" + file);
	}

	public int getProgram()
	{
		return program;
	}

	public void useProgram()
	{
		if (this == LAST_USED_SHADER)
		{
			return;
		}
		LAST_USED_SHADER = this;
		GL20.glUseProgram(program);
	}

	public int getAttribute(String a)
	{
		return GL20.glGetAttribLocation(program, a);
	}

	public int getUniform(String u)
	{
		return GL20.glGetUniformLocation(program, u);
	}

	public static void useDefaultProgram()
	{
		LAST_USED_SHADER = null;
		GL20.glUseProgram(0);
	}

}
