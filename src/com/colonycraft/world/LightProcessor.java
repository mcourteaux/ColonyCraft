package com.colonycraft.world;

import com.colonycraft.Side;
import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec3i;
import com.colonycraft.utilities.FastUnordredArrayList;
import com.colonycraft.world.blocks.BlockManager;

public class LightProcessor
{

	public static final int MAX_LIGTH = 0xF;

	private Side[] sides = Side.getSides();
	private Side[] mantleSides = Side.getMantleSides();
	private World world;

	private FastUnordredArrayList<Vec3i> list = new FastUnordredArrayList<Vec3i>(100);

	public LightProcessor(World world)
	{
		this.world = world;
	}

	private static byte setB(byte d, int l)
	{
		return (byte) ((d & ~0x0F) | l);
	}

	private static byte setS(byte d, int l)
	{
		return (byte) ((d & ~0xF0) | (l << 4));
	}

	private static int getB(byte d)
	{
		return (d & 0xF);
	}

	private static int getS(byte d)
	{
		return (d >>> 4) & 0xF;
	}

	public void respreadLightB(int x, int y, int z)
	{
		spreadLightB(x, y, z, getB(world.getLightAt(x, y, z, false)));
	}

	public void respreadLightS(int x, int y, int z)
	{
		int olds = getS(world.getLightAt(x, y, z, false));
		spreadLightS(x, y, z, olds);
		if (olds == MAX_LIGTH)
		{
			fillSun(x, y, z, 0);
		}
	}

	public void unspreadLightB(int x, int y, int z)
	{
		unspreadLightB(x, y, z, getB(world.getLightAt(x, y, z, false)));
	}

	public void unspreadLightS(int x, int y, int z)
	{
		unspreadLightS(x, y, z, getS(world.getLightAt(x, y, z, false)));
	}

	public void spreadLightB(int x, int y, int z, int l)
	{
		byte old = world.getLightAt(x, y, z, false);
		int oldb = getB(old);
		if (oldb > l)
		{
			return;
		}

		world.setLightAt(x, y, z, setB(old, l));

		if (--l == 0)
		{
			return;
		}

		for (int i = 0; i < 6; ++i)
		{
			Vec3i n = sides[i].getNormal();

			int blData = world.getBlockAt(x + n.x, y + n.y, z + n.z, false);
			if (blData == -1)
				continue;
			Block bl = BlockManager.getBlock(ChunkData.getType(blData));
			if (!bl.lightPasses())
				continue;

			byte oldside = world.getLightAt(x + n.x, y + n.y, z + n.z, false);
			int oldsideb = getB(oldside);
			if (oldsideb >= l)
			{
				continue;
			}

			spreadLightB(x + n.x, y + n.y, z + n.z, l);
		}
	}

	public void spreadLightS(int x, int y, int z, int l)
	{
		byte old = world.getLightAt(x, y, z, false);
		int olds = getS(old);
		if (olds > l)
		{
			return;
		}

		world.setLightAt(x, y, z, setS(old, l));
		if (--l == 0)
		{
			return;
		}

		for (int i = 0; i < 6; ++i)
		{
			Vec3i n = sides[i].getNormal();

			int blData = world.getBlockAt(x + n.x, y + n.y, z + n.z, false);
			if (blData == -1)
				continue;
			Block bl = BlockManager.getBlock(ChunkData.getType(blData));
			if (!bl.lightPasses())
				continue;

			byte oldside = world.getLightAt(x + n.x, y + n.y, z + n.z, false);
			int oldsideb = getS(oldside);
			if (oldsideb >= l)
			{
				continue;
			}
			spreadLightS(x + n.x, y + n.y, z + n.z, l);
		}
	}

	private void unspreadLightB(int x, int y, int z, int l, FastUnordredArrayList<Vec3i> brightSpots)
	{
		byte old = world.getLightAt(x, y, z, false);
		int oldb = getB(old);
		if (oldb > l)
		{
			brightSpots.add(new Vec3i(x, y, z));
			return;
		} else
		{
			// If this block is in the bright spots by mistake, remove it.
			int r = 0;
			for (int i = 0; i < brightSpots.size(); ++i)
			{
				Vec3i v = brightSpots.get(i);
				if (v != null)
				{
					if (v.equals(x, y, z))
					{
						brightSpots.remove(i);
						if (++r == 4)
						{
							break;
						}
					}
				}
			}
		}
		if (oldb < l)
			return;
		world.setLightAt(x, y, z, setB(old, 0));

		if (--l == 0)
		{
			return;
		}

		for (int i = 0; i < 6; ++i)
		{
			Vec3i n = sides[i].getNormal();

			int blData = world.getBlockAt(x + n.x, y + n.y, z + n.z, false);
			if (blData == -1)
				continue;
			Block bl = BlockManager.getBlock(ChunkData.getType(blData));
			if (!bl.lightPasses())
				continue;

			unspreadLightB(x + n.x, y + n.y, z + n.z, l, brightSpots);
		}
	}

	private void unspreadLightS(int x, int y, int z, int l, FastUnordredArrayList<Vec3i> brightSpots)
	{
		byte old = world.getLightAt(x, y, z, false);
		int olds = getS(old);
		if (olds > l)
		{
			brightSpots.add(new Vec3i(x, y, z));
			return;
		} else
		{
			// If this block is in the bright spots by mistake, remove it.
			int r = 0;
			for (int i = 0; i < brightSpots.size(); ++i)
			{
				Vec3i v = brightSpots.get(i);
				if (v != null)
				{
					if (v.equals(x, y, z))
					{
						brightSpots.remove(i);
						if (++r == 3)
						{
							break;
						}
					}
				}
			}
		}
		if (olds < l)
			return;
		world.setLightAt(x, y, z, setS(old, 0));

		if (--l == 0)
		{
			return;
		}

		for (int i = 0; i < 6; ++i)
		{
			Vec3i n = sides[i].getNormal();

			int blData = world.getBlockAt(x + n.x, y + n.y, z + n.z, false);
			if (blData == -1)
				continue;
			Block bl = BlockManager.getBlock(ChunkData.getType(blData));
			if (!bl.lightPasses())
				continue;

			unspreadLightS(x + n.x, y + n.y, z + n.z, l, brightSpots);
		}
	}

	public void unspreadLightB(int x, int y, int z, int l)
	{
		unspreadLightB(x, y, z, l, list);
		for (int i = 0; i < list.size(); ++i)
		{
			Vec3i v = list.get(i);
			if (v != null)
			{
				// System.out.printf("Respread! %d, %d, %d: %d%n", v.x, v.y,
				// v.z, getB(world.getLightAt(v.x, v.y, v.z, false)));
				spreadLightB(v.x, v.y, v.z, getB(world.getLightAt(v.x, v.y, v.z, false)));
			}
		}
	}

	public void unspreadLightS(int x, int y, int z, int l)
	{
		unspreadLightS(x, y, z, l, list);
		for (int i = 0; i < list.size(); ++i)
		{
			Vec3i v = list.get(i);
			if (v != null)
				spreadLightS(v.x, v.y, v.z, getS(world.getLightAt(v.x, v.y, v.z, false)));
		}
	}

	public void fillSun(int x, int y, int z, int spreading)
	{
		int chX = MathHelper.floorDivision(x, Chunk.BPC_1D);
		int chY = MathHelper.floorDivision(y, Chunk.BPC_1D);
		int chZ = MathHelper.floorDivision(z, Chunk.BPC_1D);

		Chunk bottomChunk = world.getChunk(chX, chY - 1, chZ, false);
		int endY = chY * Chunk.BPC_1D - 1;
		if (bottomChunk == null)
		{
			endY = (chY - 1) * Chunk.BPC_1D;
		}

		for (int i = y; i > endY; --i)
		{
			int bdata = world.getBlockAt(x, i, z, false);
			if (bdata == -1)
				continue;
			int blType = ChunkData.getType(bdata);
			Block bl = BlockManager.getBlock(blType);
			if (!bl.lightPasses())
				return;

			byte old = world.getLightAt(x, i, z, false);
			world.setLightAt(x, i, z, setS(old, MAX_LIGTH));

			for (int s = 0, bit = 1; s < mantleSides.length; ++s, bit <<= 1)
			{
				Vec3i n = mantleSides[s].getNormal();

				if ((spreading & bit) == bit)
				{
					bdata = world.getBlockAt(x + n.x, i + n.y, z + n.z, false);
					if (bdata == -1)
						continue;
					blType = ChunkData.getType(bdata);
					bl = BlockManager.getBlock(blType);
					if (!bl.lightPasses())
						continue;

					spreadLightS(x + n.x, i + n.y, z + n.z, MAX_LIGTH - 1);
				} else
				{
					bdata = world.getBlockAt(x + n.x, i + n.y, z + n.z, false);
					if (bdata == -1)
						continue;
					blType = ChunkData.getType(bdata);
					bl = BlockManager.getBlock(blType);
					if (bl.lightPasses())
						continue;

					spreading |= bit;
				}
			}

		}

		if (bottomChunk != null)
		{
			fillSun(x, endY, z, spreading);
		}
	}

	public void unfillSun(int x, int y, int z, boolean covered)
	{
		int chX = MathHelper.floorDivision(x, Chunk.BPC_1D);
		int chY = MathHelper.floorDivision(y, Chunk.BPC_1D);
		int chZ = MathHelper.floorDivision(z, Chunk.BPC_1D);

		Chunk bottomChunk = world.getChunk(chX, chY - 1, chZ, false);
		int endY = chY * Chunk.BPC_1D - 1;
		if (bottomChunk == null)
		{
			endY = (chY - 1) * Chunk.BPC_1D;
		}

		for (int i = y; i > endY; --i)
		{
			int bdata = world.getBlockAt(x, i, z, false);
			if (bdata == -1)
				continue;
			int blType = ChunkData.getType(bdata);
			Block bl = BlockManager.getBlock(blType);
			if (!bl.lightPasses())
			{
				covered = true;
			}

			if (covered)
			{
				byte old = world.getLightAt(x, i, z, false);
				int olds = getS(old);

				if (olds != MAX_LIGTH)
				{
					System.out.println("Stop at (" + x + ", " + i + ", " + z + ") because: " + olds);
					return;
				}
				System.out.println("unspread sunlight at (" + x + ", " + i + ", " + z + ")");
				unspreadLightS(x, i, z, olds);
			}

		}

		if (bottomChunk != null)
		{
			unfillSun(x, endY, z, covered);
		}
	}

}
