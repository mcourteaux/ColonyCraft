package com.colonycraft.rendering.world;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.colonycraft.math.AABB;
import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec3f;
import com.colonycraft.math.Vec3i;
import com.colonycraft.rendering.Mesh;
import com.colonycraft.utilities.IntList;
import com.colonycraft.world.Block;
import com.colonycraft.world.Chunk;
import com.colonycraft.world.ChunkData;
import com.colonycraft.world.World;
import com.colonycraft.world.blocks.BlockManager;
import com.colonycraft.world.blocks.shapes.BlockShape;

public class ChunkMeshBuilder
{

	private static final boolean SMOOTH_LIGHTING = true;
	private static final Vec3f HALF_BLOCK = new Vec3f(0.5f, 0.5f, 0.5f);

	@SuppressWarnings("unused")
	private Chunk chunk;
	private ChunkData cdata;
	private ChunkMesh cmesh;
	private LightBuffer lightBuffer;

	public ChunkMeshBuilder(World world)
	{
		lightBuffer = new LightBuffer(world);
	}

	public void buildMesh(ChunkMesh mesh, Chunk chunk, int meshType)
	{
		this.chunk = chunk;
		this.cmesh = mesh;
		this.cdata = chunk.getData();
		this.lightBuffer.buffer(chunk);

		IntList visibleBlocks = chunk.getVisibleBlocks();
		Vec3i vectori = new Vec3i();
		Vec3f vectorf = new Vec3f();
		AABB aabb = null;
		AABB blockAABB = new AABB(new Vec3f(), HALF_BLOCK);
		int vertices = 0;
		for (int i = 0; i < visibleBlocks.size(); ++i)
		{
			/* Extract all data */
			int index = visibleBlocks.get(i);
			int data = cdata.getBlockData(index);
			int type = ChunkData.getType(data);
			int faceMask = ChunkData.getFaceMask(data);

			Block block = BlockManager.getBlock(type);
			if (block.getMesh() == meshType)
			{
				BlockShape brush = block.getShape();
				vertices += brush.vertexCount(faceMask);
			}
		}
		
		if (vertices == 0)
		{
			chunk.setMeshClean();
			return;
		}
		
		mesh.setMesh(meshType, new Mesh(vertices, ChunkMeshRenderer.STRIDE * 4, 0));
		for (int i = 0; i < visibleBlocks.size(); ++i)
		{
			int index = visibleBlocks.get(i);
			int data = cdata.getBlockData(index);

			ChunkData.posForBlock(index, vectori);
			vectorf.set(vectori).add(chunk.getWorldPos()).add(HALF_BLOCK);
			blockAABB.setPosition(vectorf);
			if (aabb == null)
			{
				aabb = new AABB(blockAABB);
			} else
			{
				aabb.include(blockAABB);
			}

			lightBuffer.setRef(vectori.x, vectori.y, vectori.z);

			/* Extract all data */
			int type = ChunkData.getType(data);
			int faceMask = ChunkData.getFaceMask(data);

			Block block = BlockManager.getBlock(type);
			if (block.getMesh() == meshType)
			{
				BlockShape brush = block.getShape();
				brush.putInMesh(cmesh.getMesh(block.getMesh()), vectorf, faceMask, lightBuffer);
			}
		}

		cmesh.getMesh(meshType).getVertexBuffer().flip();
		cmesh.getMesh(meshType).uploadVertexBuffer();
		chunk.setMeshClean();
		chunk.setVisibleContantAABB(aabb);
		cmesh.getMesh(meshType).releaseRAMBuffers();
	}

	public static void putLight4(FloatBuffer vertexBuffer, int light, int light1, int light2, int light3)
	{
		float value;

		if (SMOOTH_LIGHTING)
		{
			value = light + light1 + light2 + light3;
			value /= 60.0001f;
		} else
		{
			value = light / 15.001f;
		}
		vertexBuffer.put(MathHelper.smoothCurve(value));
	}

	public static void putLight3(FloatBuffer vertexBuffer, int light, int light1, int light2)
	{
		float value;

		if (SMOOTH_LIGHTING)
		{
			value = light + light1 + light2;
			value /= 45.0001f;
		} else
		{
			value = light / 15.001f;
		}

		vertexBuffer.put(MathHelper.smoothCurve(value));
	}

	public static void putLight(FloatBuffer vertexBuffer, byte light)
	{
		float value;
		value = light / 15.001f;
		vertexBuffer.put(MathHelper.smoothCurve(value));
	}

	public static void putVec3f(FloatBuffer vertexBuffer, Vec3f vec)
	{
		vertexBuffer.put(vec.x);
		vertexBuffer.put(vec.y);
		vertexBuffer.put(vec.z);
	}

	public static void put3f(FloatBuffer vertexBuffer, float f0, float f1, float f2)
	{
		vertexBuffer.put(f0);
		vertexBuffer.put(f1);
		vertexBuffer.put(f2);
	}

	public static void put3f(ByteBuffer vertexBuffer, float f0, float f1, float f2)
	{
		vertexBuffer.putFloat(f0);
		vertexBuffer.putFloat(f1);
		vertexBuffer.putFloat(f2);
	}

	public static void put2f(FloatBuffer vertexBuffer, float f0, float f1)
	{
		vertexBuffer.put(f0);
		vertexBuffer.put(f1);

	}

}
