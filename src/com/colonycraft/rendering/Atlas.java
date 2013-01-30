package com.colonycraft.rendering;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.opengl.Texture;

import com.colonycraft.TextureStorage;
import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec2f;
import com.colonycraft.math.Vec2i;

public class Atlas
{
	private String name;
	private Texture texture;
	
	private BufferedImage img;
	private Graphics graphics;
	private Vec2i tileSize;
	private int tileCount;
	private int tilesPerRow;
	@SuppressWarnings("unused")
	private int tilesPerColumn;
	private int usedTiles;
	
	private float tileTextureW;
	private float tileTextureH;
	
	private List<String> tileNames;
	
	public Atlas(String name, Vec2i tileSize, int tileCount)
	{
		this.name = name;
		this.tileSize = tileSize;
		this.tileCount = tileCount;
		
		/* Find the best dimensions */
		
		int bestW = 0;
		int bestH = 0;
		int bestSurface = Integer.MAX_VALUE;
		
		for (int i = 0; i < 14; ++i)
		{
			int w = 1 << i;
		
			if (w < tileSize.x) continue;
			
			int tpr = w / tileSize.x;
			int tpc = MathHelper.ceil((float) tileCount / tpr);
			
			int h = MathHelper.getPowerOfTwoBiggerThan(tpc * tileSize.y);
			
			int surface = w * h;
			
			if (surface < bestSurface)
			{
				bestSurface = surface;
				bestW = w;
				bestH = h;
				tilesPerColumn = tpc;
				tilesPerRow = tpr;
			}
		}
		
		img = new BufferedImage(bestW, bestH, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		graphics = img.getGraphics();
		tileTextureW = (float) tileSize.x / bestW;
		tileTextureH = (float) tileSize.y / bestH;
		tileNames = new ArrayList<String>(tileCount);
	}
	
	public void addTile(BufferedImage img, String name)
	{
		if (img.getWidth() != tileSize.x || img.getHeight() != tileSize.y)
		{
			throw new IllegalArgumentException("Provided image has not the right dimensions");
		}
		int row = usedTiles / tilesPerRow;
		int column = usedTiles % tilesPerRow;
		usedTiles++;
		tileNames.add(name);
		graphics.drawImage(img, column * tileSize.x, row * tileSize.y, null);
	}
	
	public void finishAndUpload() throws IOException
	{
		graphics.dispose();
		texture = TextureStorage.loadTexture(name, img);
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	/**
	 * Get the Top-Left UV of the specified tile.
	 * @param index
	 * @param output
	 */
	public void getUV(int index, Vec2f output)
	{
		int row = index / tilesPerRow;
		int column = index % tilesPerRow;
		output.set((float) column * tileTextureW, (float) row * tileTextureH);
	}

	public float getTileTextureW()
	{
		return tileTextureW;
	}

	public float getTileTextureH()
	{
		return tileTextureH;
	}
	
	public int getTileCount()
	{
		return tileCount;
	}
	
	public Vec2i getTileSize()
	{
		return tileSize;
	}
	
	public int getTileIndex(String name)
	{
		return tileNames.indexOf(name);
	}

	public String getName()
	{
		return name;
	}
}
