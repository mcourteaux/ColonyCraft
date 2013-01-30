package com.colonycraft.world;

import java.util.Comparator;

import javax.vecmath.Vector3f;

import com.colonycraft.math.Vec3f;

public class ChunkDistanceComparator implements Comparator<Chunk>
{
	
	private Vec3f reference;
	private Vec3f p1, p2;
	
	public ChunkDistanceComparator()
	{
		reference = new Vec3f();
		p1 = new Vec3f();
		p2 = new Vec3f();
	}
	
	public void setReference(Vec3f reference)
	{
		this.reference.set(reference);
	}
	
	public void setReference(Vector3f ref)
	{
		this.reference.set(ref.x, ref.y, ref.z);
	}

	@Override
	public int compare(Chunk o1, Chunk o2)
	{
		p1.set(o1.getWorldPos()).sub(reference);
		p2.set(o2.getWorldPos()).sub(reference);
		float lSq1 = p1.lengthSquared();
		float lSq2 = p2.lengthSquared();
		return Float.compare(lSq1, lSq2);
	}



}
