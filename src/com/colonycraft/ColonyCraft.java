package com.colonycraft;

import java.awt.Font;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec2i;
import com.colonycraft.rendering.Atlas;
import com.colonycraft.rendering.GLFont;
import com.colonycraft.rendering.ShaderStorage;
import com.colonycraft.world.World;

public class ColonyCraft
{

	private long frameStart;
	private int frameTime;
	private float framerate;
	private float framerateCap;
	private int frametimeMinMillis;
	private float step;
	private int sleeptimeMillis;
	private float averangeFramerate;
	
	private GLFont debugFont;
	
	private World world;
	
	public void init()
	{
		try
		{
			setDisplayMode(1280, 800, true);
			Display.setVSyncEnabled(true);
			Display.create();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			ShaderStorage.loadShader("chunk");
			ShaderStorage.loadShader("grass");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		framerateCap = 60;
		averangeFramerate = framerateCap;
		frametimeMinMillis = (int) (1000 / framerateCap);
		
		debugFont = new GLFont(new Font("Courier New", Font.PLAIN, 12));
		
		loadTerrainAtlas();
		world = new World();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		gameLoop();
	}
	
	
	private void loadTerrainAtlas()
	{
		try
		{
			Atlas a = new Atlas("terrain", new Vec2i(16, 16), 5);
			a.addTile(TextureStorage.loadBufferedImage("terrain/Dirt.png"), "Dirt");
			a.addTile(TextureStorage.loadBufferedImage("terrain/Stone.png"), "Stone");
			a.addTile(TextureStorage.loadBufferedImage("terrain/GrassTop.png"), "GrassTop");
			a.addTile(TextureStorage.loadBufferedImage("terrain/GrassSide.png"), "GrassSide");
			a.addTile(TextureStorage.loadBufferedImage("terrain/TallGrass.png"), "Tall Grass");
			a.finishAndUpload();
			TextureStorage.putAtlas(a);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void gameLoop()
	{
		frameStart = System.nanoTime();
		framerate = framerateCap;
		update();
		while (!Display.isCloseRequested())
		{
			frameStart = System.nanoTime();

			
			render();
			update();
			Display.update();
			
			frameTime = (int) (System.nanoTime() - frameStart);
			sleeptimeMillis = Math.max(0, frametimeMinMillis - (frameTime / 1000000));
			framerate = 1e9f / (frameTime + sleeptimeMillis * 1000000);
			averangeFramerate = averangeFramerate * 0.9f + framerate * 0.1f;
			step = 1.0f / averangeFramerate;
			try
			{
				Thread.sleep(sleeptimeMillis);
			} catch (Exception e)
			{
			}
		}
		
		TextureStorage.release();
		Display.destroy();
	}
	
	public void update()
	{
		world.update();
	}
	
	public void render()
	{

		world.render();
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), 0, Display.getHeight(), -10, 10);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glColor4f(1, 1, 1, 1);
		debugFont.print(3, 5, String.format("FPS: %.0f", averangeFramerate));
		debugFont.print(3, 20, String.format("Pos: %s", world.getActivePlayer().getTransform().origin.toString()));
		debugFont.print(4, Display.getHeight() - 20 - 15, "Sleeping: " + String.format("%4d", sleeptimeMillis));
		debugFont.print(4, Display.getHeight() - 20 - 30, "Heap Size: " + MathHelper.bytesToMagaBytes(Runtime.getRuntime().totalMemory()) + " MB");
		debugFont.print(4, Display.getHeight() - 20 - 45, "Heap Use:  " + MathHelper.bytesToMagaBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " MB");

	}

	
	/* Singleton */
	private static ColonyCraft colonyCraft;
	private ColonyCraft()
	{
	}
	protected static void createInstance()
	{
		colonyCraft = new ColonyCraft();
	}
	public static ColonyCraft getIntance()
	{
		return colonyCraft;
	}

	public World getWorld()
	{
		return world;
	}

	public float getStep()
	{
		return step;
	}
	
	public void setDisplayMode(int width, int height, boolean fullscreen)
	{
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen))
		{
			return;
		}

		try
		{
			DisplayMode targetDisplayMode = null;

			if (fullscreen)
			{
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++)
				{
					DisplayMode current = modes[i];
					if ((current.getWidth() == width) && (current.getHeight() == height))
					{
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq))
						{
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel()))
							{
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency()))
						{
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else
			{
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null)
			{
				System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch (LWJGLException e)
		{
			System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}
	}
}
