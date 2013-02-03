package com.colonycraft.world.generators;

import com.colonycraft.utilities.FractalBrownianMotion;
import com.colonycraft.utilities.PerlinNoise;
import com.colonycraft.world.Chunk;
import com.colonycraft.world.World;
import com.colonycraft.world.blocks.BlockManager;

public class WorldGenerator
{
	private World world;
	private long seed;

	private PerlinNoise noises[];
	private FractalBrownianMotion fBm;

	public WorldGenerator(World world, long seed)
	{
		this.world = world;
		this.seed = seed;
		this.noises = new PerlinNoise[4];
		this.fBm = new FractalBrownianMotion(new PerlinNoise(seed));
		this.fBm.setFraction(0.8f);
		this.fBm.setOctaves(4);
		for (int i = 0; i < noises.length; ++i)
		{
			this.noises[i] = new PerlinNoise(seed ^ i * 1356402422L + 1394235413L);
		}
	}

	public void generateChunk(int cx, int cy, int cz)
	{
		int ax = cx * Chunk.BPC_1D;
		int ay = cy * Chunk.BPC_1D;
		int az = cz * Chunk.BPC_1D;

		Chunk chunk = world.getChunk(cx, cy, cz, true);
		chunk.setLoading(true);

		int[][] hmap = new int[Chunk.BPC_1D][Chunk.BPC_1D];
		for (int x = 0; x < Chunk.BPC_1D; ++x)
		{
			for (int z = 0; z < Chunk.BPC_1D; ++z)
			{
				hmap[x][z] = (int) (10.0f * fBm.noise((ax + x) * 0.00005f, (az + z) * 0.00005f));
			}
		}

		for (int x = 0; x < Chunk.BPC_1D; ++x)
		{
			for (int z = 0; z < Chunk.BPC_1D; ++z)
			{
				int h = hmap[x][z];
				for (int y = ay; y < ay + Chunk.BPC_1D && y < h; ++y)
				{
					float n = noises[0].noise((ax + x) * 0.02f, y * 0.03f, (az + z) * 0.02f);
					if (n > -0.3f)
					{
						if (y < h - Math.abs(n * 30.0f) - 3)
						{
							world.setBlockAt(ax + x, y, az + z, BlockManager.getBlock("Stone"));
						} else
						{
							world.setBlockAt(ax + x, y, az + z, BlockManager.getBlock("Dirt"));
						}
						if (y == h - 1)
						{
							world.setBlockAt(ax + x, y, az + z, BlockManager.getBlock("Grass"));
							if (noises[1].noise((ax + x) * 0.18f, (az + z) * 0.18f) > 0.4f)
							{
								world.setBlockAt(ax + x, y + 1, az + z, BlockManager.getBlock("Tall Grass"));
							}
						}
					}
				}
			}
		}

		chunk.setLoading(false);
	}

}
