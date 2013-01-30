package com.colonycraft.world;

import java.util.Collections;
import java.util.List;

import com.colonycraft.Side;
import com.colonycraft.math.AABB;
import com.colonycraft.math.Vec3f;
import com.colonycraft.math.Vec3i;
import com.colonycraft.utilities.FastArrayList;

public class BlockRayCastCalculator
{

	private static int BIT_FRONT = 1 << Side.FRONT.ordinal();
	private static int BIT_BACK = 1 << Side.BACK.ordinal();
	private static int BIT_LEFT = 1 << Side.LEFT.ordinal();
	private static int BIT_RIGHT = 1 << Side.RIGHT.ordinal();
	private static int BIT_TOP = 1 << Side.TOP.ordinal();
	private static int BIT_BOTTOM = 1 << Side.BOTTOM.ordinal();

	private Vec3f a, b, norm, intersectPoint;
	private List<Intersection> result;


	public BlockRayCastCalculator()
	{
		a = new Vec3f();
		b = new Vec3f();
		norm = new Vec3f();
		intersectPoint = new Vec3f();
		result = new FastArrayList<BlockRayCastCalculator.Intersection>(2);
	}

	public static class Intersection implements Comparable<Intersection>
	{

		private final float distance;
		@SuppressWarnings("unused")
		private final Vec3f rayOrigin, intersectionPoint, rayDirection;
		private final Vec3i surfaceNormal;
		private final int blockX, blockY, blockZ;

		public Intersection(int blockX, int blockY, int blockZ, Vec3i normal, float t, Vec3f rayOrigin, Vec3f rayDirection, Vec3f intersectionPoint)
		{
			this.distance = t;
			this.rayOrigin = rayOrigin;
			this.rayDirection = rayDirection;
			this.intersectionPoint = intersectionPoint;
			this.surfaceNormal = normal;
			this.blockX = blockX;
			this.blockY = blockY;
			this.blockZ = blockZ;
		}

		@Override
		public int compareTo(Intersection o)
		{
			if (o == null)
			{
				return 0;
			}

			float distance2 = o.distance;

			if (distance == distance2)
			{
				return 0;
			}

			return distance2 > distance ? -1 : 1;
		}

		Vec3i getSurfaceNormal()
		{
			return surfaceNormal;
		}

		public Vec3i calcAdjacentBlockPos()
		{
			Vec3i pos = getBlockPosition();
			pos.add(getSurfaceNormal());

			return pos;
		}

		public Vec3i getBlockPosition()
		{
			return new Vec3i(blockX, blockY, blockZ);
		}

		@Override
		public String toString()
		{
			return String.format("x: %d y: %d z: %d", blockX, blockY, blockZ);
		}

		public float getDistance()
		{
			return distance;
		}
	}

	public List<Intersection> executeIntersection(int x, int y, int z, AABB blockAABB, Vec3f rayOrigin, Vec3f rayDirection, int faces)
	{

		result.clear();

		/*
		 * Fetch all vertices of the specified block.
		 */
		Vec3f[] vertices = blockAABB.getVertices();

		/*
		 * Generate a new intersection for each side of the block.
		 */

		Intersection is;

		// Front
		if ((faces & BIT_FRONT) == BIT_FRONT)
		{
			is = executeBlockFaceIntersection(x, y, z, vertices[0], vertices[1], vertices[3], rayOrigin, rayDirection);
			if (is != null)
			{
				result.add(is);
			}
		}

		// Back
		if ((faces & BIT_BACK) == BIT_BACK)
		{
			is = executeBlockFaceIntersection(x, y, z, vertices[4], vertices[7], vertices[5], rayOrigin, rayDirection);
			if (is != null)
			{
				result.add(is);
			}
		}

		// Left
		if ((faces & BIT_LEFT) == BIT_LEFT)
		{
			is = executeBlockFaceIntersection(x, y, z, vertices[4], vertices[0], vertices[7], rayOrigin, rayDirection);
			if (is != null)
			{
				result.add(is);
			}
		}

		// Right
		if ((faces & BIT_RIGHT) == BIT_RIGHT)
		{
			is = executeBlockFaceIntersection(x, y, z, vertices[5], vertices[6], vertices[1], rayOrigin, rayDirection);
			if (is != null)
			{
				result.add(is);
			}
		}

		// Top
		if ((faces & BIT_TOP) == BIT_TOP)
		{
			is = executeBlockFaceIntersection(x, y, z, vertices[7], vertices[3], vertices[6], rayOrigin, rayDirection);
			if (is != null)
			{
				result.add(is);
			}
		}

		// Bottom
		if ((faces & BIT_BOTTOM) == BIT_BOTTOM)
		{
			is = executeBlockFaceIntersection(x, y, z, vertices[4], vertices[5], vertices[0], rayOrigin, rayDirection);
			if (is != null)
			{
				result.add(is);
			}
		}

		// Sort the intersections by distance to the player
		Collections.sort(result);
		return result;
	}

	private Intersection executeBlockFaceIntersection(int blockX, int blockY, int blockZ, Vec3f v0, Vec3f v1, Vec3f v2, Vec3f origin, Vec3f ray)
	{

		/*
		 * Calculate the plane to intersect with, in the form of Ax + By + Cz +
		 * D = 0
		 */
		a.set(v1);
		a.sub(v0);
		b.set(v2);
		b.sub(v0);
		norm.cross(a, b); /* Norm.xyz = A, B, C */

		/* Calculate D */
		float d = -Vec3f.dot(norm, v0);

		/* Calculate the distance on the ray, where the intersection occurs. */
		float t = -(Vec3f.dot(norm, origin) + d) / Vec3f.dot(ray, norm);

		/* We only want to check positive rays */
		if (t < 0)
		{
			return null;
		}

		/* Calculate the point of intersection. */
		intersectPoint.set(ray);
		intersectPoint.scale(t);
		intersectPoint.add(origin);
		
		float epsilon = 0.001f;

		/* Check if the point lies on block's face. */
		if (
				intersectPoint.x + epsilon >= v0.x && intersectPoint.x - epsilon <= Math.max(v1.x, v2.x) &&
				intersectPoint.y + epsilon >= v0.y && intersectPoint.y - epsilon <= Math.max(v1.y, v2.y) &&
				intersectPoint.z + epsilon >= v0.z && intersectPoint.z - epsilon <= Math.max(v1.z, v2.z))
		{
			return new Intersection(blockX, blockY, blockZ, new Vec3i((int) norm.x, (int) norm.y, (int) norm.z), t, origin, ray, new Vec3f(intersectPoint));
		}

		return null;
	}
}
