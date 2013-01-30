package com.colonycraft.world;

import java.util.List;

import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec3f;
import com.colonycraft.math.Vec3i;
import com.colonycraft.rendering.camera.ViewFrustum;
import com.colonycraft.utilities.FastArrayList;

public class EnvironmentManager
{

	private World world;

	private List<SuperChunk> localSuperChunks;
	private List<Chunk> localChunks;
	private List<Chunk> visibleChunks;

	private List<Chunk> secondaryLocalChunks;

	private EnvironmentManagerCallbacks callbacks;
	private Vec3i localAreaLowerBounds;
	private Vec3i localAreaUpperBounds;

	public EnvironmentManager(World w)
	{
		world = w;
		localSuperChunks = new FastArrayList<SuperChunk>();
		localChunks = new FastArrayList<Chunk>();
		visibleChunks = new FastArrayList<Chunk>();

		secondaryLocalChunks = new FastArrayList<Chunk>();

		localAreaLowerBounds = new Vec3i();
		localAreaUpperBounds = new Vec3i();

	}

	public void setCallbacks(EnvironmentManagerCallbacks callbacks)
	{
		this.callbacks = callbacks;
	}

	@SuppressWarnings("unused")
	public void manage(Vec3f position, float viewingDistance, ViewFrustum frustum)
	{
		selectLocalSuperChunks(position, viewingDistance, localSuperChunks);

		Vec3i lalbOld = new Vec3i(localAreaLowerBounds);
		Vec3i laubOld = new Vec3i(localAreaUpperBounds);

		selectLocalChunks(position, viewingDistance, secondaryLocalChunks, localAreaLowerBounds, localAreaUpperBounds);
		selectVisibleChunks(frustum);

		/* Quick check if there were changes */
		boolean noChanges = false;
		if (localChunks.size() == secondaryLocalChunks.size())
		{
			int size = localChunks.size();
			if (size != 0)
			{
				if (localChunks.get(0) == secondaryLocalChunks.get(0) && localChunks.get(size - 1) == secondaryLocalChunks.get(size - 1))
				{
					noChanges = true;
				}
			}
		}

		/* Find changes in the local chunk */
		if (!noChanges && false)
		{
			lalbOld.x = Math.min(lalbOld.x, localAreaLowerBounds.x);
			lalbOld.y = Math.min(lalbOld.y, localAreaLowerBounds.y);
			lalbOld.z = Math.min(lalbOld.z, localAreaLowerBounds.z);

			laubOld.x = Math.max(laubOld.x, localAreaUpperBounds.x);
			laubOld.y = Math.max(laubOld.y, localAreaUpperBounds.y);
			laubOld.z = Math.max(laubOld.z, localAreaUpperBounds.z);

			int i = 0, j = 0;

			Vec3i v = new Vec3i();
			v.set(laubOld);
			v.sub(lalbOld);
			int w = v.x + 1;
			int h = v.y + 1;
			// int d = v.z;

			int iS = localChunks.size();
			int jS = secondaryLocalChunks.size();

			while (i < iS && j < jS)
			{
				Chunk cI = localChunks.get(i);
				Chunk cJ = secondaryLocalChunks.get(j);

				Vec3i cPi = cI.getPos();
				Vec3i cPj = cJ.getPos();

				v.set(cPi);
				v.sub(lalbOld);

				int iV = v.z * h * w + v.y * w + v.x;

				v.set(cPj);
				v.sub(lalbOld);

				int jV = v.z * h * w + v.y * w + v.x;

				if (iV < jV)
				{
					callbacks.unloadChunk(cI);
					i++;
				} else if (iV > jV)
				{
					callbacks.loadChunk(cJ);
					j++;
				} else
				{
					i++;
					j++;
				}
			}

			while (i < iS)
			{
				callbacks.unloadChunk(localChunks.get(i));
				i++;
			}
			while (j < jS)
			{
				callbacks.loadChunk(secondaryLocalChunks.get(j));
				j++;
			}

		}

		if (!noChanges)
		{
			/* Find the new ones */
			outer: for (int i = 0; i < secondaryLocalChunks.size(); ++i)
			{
				Chunk c = secondaryLocalChunks.get(i);
				for (int j = 0; j < localChunks.size(); ++j)
				{
					if (c == localChunks.get(j))
					{
						continue outer;
					}
				}
				callbacks.loadChunk(c);
			}

			/* Find the old ones */
			outer: for (int i = 0; i < localChunks.size(); ++i)
			{
				Chunk c = localChunks.get(i);
				for (int j = 0; j < secondaryLocalChunks.size(); ++j)
				{
					if (c == secondaryLocalChunks.get(j))
					{
						continue outer;
					}
				}
				callbacks.unloadChunk(c);
			}
		}

		/* Swap the lists */
		List<Chunk> previousLocalChunks = localChunks;
		localChunks = secondaryLocalChunks;
		secondaryLocalChunks = previousLocalChunks;
	}

	public Chunk getLocalChunk(int x, int y, int z, boolean cin)
	{
		int superX = MathHelper.floorDivision(x, SuperChunk.CPSC_1D);
		int superY = MathHelper.floorDivision(y, SuperChunk.CPSC_1D);
		int superZ = MathHelper.floorDivision(z, SuperChunk.CPSC_1D);

		int subX = x - superX * SuperChunk.CPSC_1D;
		int subY = y - superY * SuperChunk.CPSC_1D;
		int subZ = z - superZ * SuperChunk.CPSC_1D;

		int positionID = MathHelper.mapToPositiveAndCantorize3(superX, superY, superZ);

		for (int i = 0; i < localSuperChunks.size(); ++i)
		{
			SuperChunk schunk = localSuperChunks.get(i);
			if (schunk.getPositionIdentifier() == positionID)
			{
				return schunk.getChunk(subX, subY, subZ, cin);
			}
		}
		return null;
	}

	public void selectLocalChunks(Vec3f position, float viewingDistance, List<Chunk> chunks, Vec3i lowerBoundsOut, Vec3i upperBoundsOut)
	{
		viewingDistance /= Chunk.BPC_1D;
		viewingDistance += 1.1f;

		int distance = MathHelper.ceil(viewingDistance);
		int distanceSq = distance * distance;

		int centerX = MathHelper.floor(position.x / Chunk.BPC_1D);
		int centerY = MathHelper.floor(position.y / Chunk.BPC_1D);
		int centerZ = MathHelper.floor(position.z / Chunk.BPC_1D);

		lowerBoundsOut.set(centerX - distance, centerY - distance, centerZ - distance);
		upperBoundsOut.set(centerX + distance, centerY + distance, centerZ + distance);

		chunks.clear();

		for (int x = -distance; x <= distance; ++x)
		{
			for (int y = -distance; y <= distance; ++y)
			{
				for (int z = -distance; z <= distance; ++z)
				{
					int distSq = x * x + z * z;
					if (distSq <= distanceSq)
					{
						Chunk chunk = getLocalChunk(centerX + x, centerY + y, centerZ + z, false);
						if (chunk != null)
						{
							chunks.add(chunk);
						}
					}
				}
			}
		}
	}

	public void selectLocalSuperChunks(Vec3f position, float viewingDistance, List<SuperChunk> chunks)
	{
		viewingDistance /= SuperChunk.BPSC_1D;
		viewingDistance += 1.1f;

		int distance = MathHelper.ceil(viewingDistance);
		int distanceSq = distance * distance;

		int centerX = MathHelper.floor(position.x / SuperChunk.BPSC_1D);
		int centerY = MathHelper.floor(position.y / SuperChunk.BPSC_1D);
		int centerZ = MathHelper.floor(position.z / SuperChunk.BPSC_1D);

		chunks.clear();

		for (int x = -distance; x <= distance; ++x)
		{
			for (int y = -distance; y <= distance; ++y)
			{
				for (int z = -distance; z <= distance; ++z)
				{
					int distSq = x * x + z * z;
					if (distSq <= distanceSq)
					{
						SuperChunk chunk = world.getSuperChunk(centerX + x, centerY + y, centerZ + z, false);
						if (chunk != null)
							chunks.add(chunk);
					}
				}
			}
		}
	}

	private void selectVisibleChunks(ViewFrustum frustum)
	{
		visibleChunks.clear();
		for (int i = 0; i < localChunks.size(); ++i)
		{
			Chunk ch = localChunks.get(i);
			if (ch.getVisibleBlocks().size() > 0)
			{
				if (frustum.intersects(ch.getVisibleContentAABB()))
				{
					visibleChunks.add(ch);
				}
			}
		}
	}

	public List<Chunk> getLocalChunks()
	{
		return localChunks;
	}

	public List<Chunk> getVisibleChunks()
	{
		return visibleChunks;
	}

}
