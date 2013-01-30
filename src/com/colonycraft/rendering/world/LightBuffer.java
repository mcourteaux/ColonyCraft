package com.colonycraft.rendering.world;

import com.colonycraft.math.AABB;
import com.colonycraft.math.MathHelper;
import com.colonycraft.world.Chunk;
import com.colonycraft.world.World;

public class LightBuffer
{
	
	private static final int SIZE_1D = Chunk.BPC_1D + 2;
	private static final int SIZE_2D = SIZE_1D * SIZE_1D;
	private static final int SIZE_3D = SIZE_1D * SIZE_1D * SIZE_1D;
	
	private byte[] blockLight;
	private byte[] sunLight;
	
	private World world;
	private Chunk currentBufferedChunk;
	private int refX, refY, refZ;

	public LightBuffer(World world)
	{
		this.world = world;
		blockLight = new byte[SIZE_3D];
		sunLight = new byte[SIZE_3D];
	}
	
	public void buffer(Chunk ch)
	{
		if (currentBufferedChunk == ch && !ch.isLightDirty()) return;
		this.currentBufferedChunk = ch;
		int wx = ch.getWorldPos().x - 1;
		int wy = ch.getWorldPos().y - 1;
		int wz = ch.getWorldPos().z - 1;
				
		AABB aabb = ch.getVisibleContentAABB();
		int minX = MathHelper.floor(aabb.minX()) - wx - 1;
		int maxX = MathHelper.floor(aabb.maxX()) - wx + 1;
		int minY = MathHelper.floor(aabb.minY()) - wy - 1;
		int maxY = MathHelper.floor(aabb.maxY()) - wy + 1;
		int minZ = MathHelper.floor(aabb.minZ()) - wz - 1;
		int maxZ = MathHelper.floor(aabb.maxZ()) - wz + 1;
		
		for (int x = minX; x < maxX; ++x)
		{
			for (int y = minY; y < maxY; ++y)
			{
				for (int z = minZ; z < maxZ; ++z)
				{
					int light = world.getLightAt(wx + x, wy + y, wz + z, false);
					int i = x * SIZE_2D + y * SIZE_1D + z;
					blockLight[i] = (byte) (light & 0xF);
					sunLight[i] = (byte) ((light >>> 4) & 0xF);
				}
			}
		}
	}
	
	public Chunk getCurrentBufferedChunk()
	{
		return currentBufferedChunk;
	}
	
	public void setRef(int x, int y, int z)
	{
		refX = x;
		refY = y;
		refZ = z;
	}
	
	public int getB(int x, int y, int z)
	{
		int i = (x + refX) * SIZE_2D + (y + refY) * SIZE_1D + (z + refZ);
		return blockLight[i];
	}
	
	public int getS(int x, int y, int z)
	{
		int i = (x + refX) * SIZE_2D + (y + refY) * SIZE_1D + (z + refZ);
		return sunLight[i];
	}
}
