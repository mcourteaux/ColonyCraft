package com.colonycraft.world;

import com.colonycraft.Side;
import com.colonycraft.math.AABB;
import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec3f;
import com.colonycraft.math.Vec3i;

public class SuperChunk
{

	/**
	 * Chuncks per SuperChunk in one dimension
	 */
	public final static int CPSC_1D = 16;
	public final static int CPSC_2D = CPSC_1D * CPSC_1D;
	public final static int CPSC_3D = CPSC_1D * CPSC_1D * CPSC_1D;

	/**
	 * Blocks per SuperChunk in one dimension
	 */
	public final static int BPSC_1D = CPSC_1D * Chunk.BPC_1D;

	private Vec3f HALF_SUPER_CHUNK_SIZE = new Vec3f(BPSC_1D, BPSC_1D, BPSC_1D).scale(0.5f);

	private World world;
	private Chunk[] chunks;

	private Vec3i pos;
	private Vec3i worldPos;
	private AABB aabb;
	private int positionIdentifier;

	public SuperChunk(World world, int x, int y, int z)
	{
		this.world = world;
		chunks = new Chunk[CPSC_3D];
		pos = new Vec3i(x, y, z);
		worldPos = new Vec3i(x * BPSC_1D, y * BPSC_1D, z * BPSC_1D);
		aabb = new AABB(new Vec3f(worldPos).add(HALF_SUPER_CHUNK_SIZE), HALF_SUPER_CHUNK_SIZE);
		aabb.recalcVertices();
		positionIdentifier = MathHelper.mapToPositiveAndCantorize3(x, y, z);
	}

	public Chunk getChunk(int x, int y, int z, boolean cin)
	{
		int index = z * CPSC_2D + y * CPSC_1D + x;
		Chunk c = chunks[index];
		if (cin && c == null)
		{
			c = new Chunk(world, pos.x * CPSC_1D + x, pos.y * CPSC_1D + y, pos.z * CPSC_1D + z);
			chunks[index] = c;
			System.out.println("new chunk at: " + c.getPos());
			Side[] sides = Side.getMantleSides();
			for (int i = 0; i < sides.length; ++i)
			{
				Vec3i n = sides[i].getNormal();
				Chunk nc = world.getChunk(pos.x * CPSC_1D + x + n.x, pos.y * CPSC_1D + y + n.y, pos.z * CPSC_1D + z + n.z, false);
				if (nc == null)
					continue;
				world.updateVisiblityForAllBlocksOnChunkSide(nc, sides[i ^ 1]);
			}
		}
		return c;
	}

	public void setChunk(Chunk c, int x, int y, int z)
	{
		int index = z * CPSC_2D + y * CPSC_1D + x;
		chunks[index] = c;
	}

	public AABB getAABB()
	{
		return aabb;
	}

	public Vec3i getPos()
	{
		return pos;
	}

	public Vec3i getWorldPos()
	{
		return worldPos;
	}

	public int getPositionIdentifier()
	{
		return positionIdentifier;
	}
}
