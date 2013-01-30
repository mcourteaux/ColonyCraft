/*******************************************************************************
 * Copyright 2012 Martijn Courteaux <martijn.courteaux@skynet.be>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.colonycraft;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.BufferedImageUtil;

import com.colonycraft.rendering.Atlas;

/**
 * 
 * @author martijncourteaux
 */
public class TextureStorage
{
	private static Map<String, Texture> map = new HashMap<String, Texture>();
	private static Map<String, Atlas> atlasMap = new HashMap<String, Atlas>();
	private static String texturePack = "default";
	private static String fallBackTexturePack = "default";

	public static void setTexturePack(String pack)
	{
		texturePack = pack;
	}

	public TextureStorage()
	{
	}

	public static void release()
	{
		for (Entry<String, Texture> item : map.entrySet())
		{
			System.out.printf("Releasing '%s'%n", item.getKey());
			item.getValue().release();
		}
	}

	public static Texture loadTexture(String id, String format, InputStream in) throws IOException
	{
		Texture t = TextureLoader.getTexture(format, in, GL11.GL_NEAREST);
		map.put(id, t);
		return t;
	}

	public static Texture loadTexture(String id, BufferedImage bi) throws IOException
	{
		Texture t = BufferedImageUtil.getTexture(id, bi, GL11.GL_TEXTURE_2D, // target
				GL11.GL_RGBA8, // dest pixel format
				GL11.GL_NEAREST, // min filter (unused)
				GL11.GL_NEAREST);
		map.put(id, t);
		return t;
	}
	
	private static File getTextureFile(String resource)
	{
		File res = new File("res/textures/" + texturePack + "/" + resource);
		if (!res.exists())
		{
			res = new File("res/textures/" + fallBackTexturePack + "/" + resource);
		}
		return res;
	}

	public static Texture loadTexture(String id, String format, String resource) throws IOException
	{
		return loadTexture(id, format, new FileInputStream(getTextureFile(resource)));
	}
	
	public static BufferedImage loadBufferedImage(String resource) throws IOException
	{
		return ImageIO.read(getTextureFile(resource));
	}

	public static Texture getTexture(String id)
	{
		Texture text = map.get(id);
		return text;
	}
	
	public static void putAtlas(Atlas a)
	{
		atlasMap.put(a.getName(), a);
	}
	
	public static Atlas getAtlas(String id)
	{
		return atlasMap.get(id);
	}
}
