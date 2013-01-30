package com.colonycraft.world.blocks.shapes;

import java.nio.ByteBuffer;

import com.colonycraft.math.Vec3f;
import com.colonycraft.rendering.Mesh;
import com.colonycraft.rendering.world.LightBuffer;

public abstract class BlockShape
{
	public abstract void putInMesh(Mesh mesh, Vec3f pos, int faceMask, LightBuffer lightBuffer);
	public abstract int vertexCount(int faces);
	public abstract int indexCount(int faces);
	
	
	public abstract void putInPhysicalBuffer(ByteBuffer vertexBuffer, ByteBuffer indexBuffer, Vec3f pos, int faceMask);
	public abstract int physicalVertexCount(int faces);
	public abstract int physicalIndexCount(int faces);	
}
