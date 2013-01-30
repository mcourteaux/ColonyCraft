package com.colonycraft.world;

import com.colonycraft.math.Vec3i;

public class ChunkData
{

	private static final int SHIFT_TYPE = 0;
	private static final int MASK_TYPE = 0xff << SHIFT_TYPE;

	private static final int SHIFT_FACES = 8;
	private static final int MASK_FACES = 0x3f << SHIFT_FACES;

	private int[] data;
	private byte[] light;

	public ChunkData()
	{
		data = new int[Chunk.BPC_3D];
		light = new byte[Chunk.BPC_3D];
	}

	public static void posForBlock(int index, Vec3i output)
	{
		int x = index % Chunk.BPC_1D;
		int y = (index / Chunk.BPC_1D) % Chunk.BPC_1D;
		int z = (index / Chunk.BPC_2D) % Chunk.BPC_1D;
		output.set(x, y, z);
	}

	public static int offsetForPos(int x, int y, int z)
	{
		return (Chunk.BPC_2D * z + Chunk.BPC_1D * y + x);
	}

	public byte getLight(int index)
	{
		return light[index];
	}

	public byte getLight(int x, int y, int z)
	{
		return light[offsetForPos(x, y, z)];
	}

	public void setLight(int x, int y, int z, int l)
	{
		light[offsetForPos(x, y, z)] = (byte) l;
	}

	public void setLight(int i, int l)
	{
		light[i] = (byte) l;
	}

	public int getBlockData(int index)
	{
		return data[index];
	}

	public int getBlockData(int x, int y, int z)
	{
		int offset = offsetForPos(x, y, z);
		return data[offset];
	}

	public int setBlockData(int x, int y, int z, int data)
	{
		int offset = offsetForPos(x, y, z);
		int old = this.data[offset];
		this.data[offset] = data;
		return old;
	}

	public static int getType(int data)
	{
		return data & MASK_TYPE;
	}

	public static int getFaceMask(int data)
	{
		return (data & MASK_FACES) >>> 8;
	}

	public static int setFaceMask(int data, int faceMask)
	{
		return (data & ~MASK_FACES) | (faceMask << SHIFT_FACES);
	}

}
