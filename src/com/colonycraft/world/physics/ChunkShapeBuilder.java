package com.colonycraft.world.physics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.colonycraft.math.Vec3f;
import com.colonycraft.math.Vec3i;
import com.colonycraft.utilities.IntList;
import com.colonycraft.world.Block;
import com.colonycraft.world.Chunk;
import com.colonycraft.world.ChunkData;
import com.colonycraft.world.blocks.BlockManager;
import com.colonycraft.world.blocks.shapes.BlockShape;

public class ChunkShapeBuilder implements Runnable
{
	private Chunk chunk;
	private CollisionShape shape;
	private volatile boolean ready;
	
	public boolean update;

	public ChunkShapeBuilder(Chunk chunk)
	{
		this.chunk = chunk;
	}

	@Override
	public void run()
	{
		shape = createCollisionShape();
		ready = true;
	}

	public CollisionShape getShape()
	{
		return shape;
	}

	public Chunk getChunk()
	{
		return chunk;
	}

	public boolean isReady()
	{
		return ready;
	}

	private CollisionShape createCollisionShape()
	{
		IntList visibleBlocks = chunk.getVisibleBlocks();
		visibleBlocks.executeModificationBuffer();
		Vec3i worldPos = chunk.getWorldPos();
		ChunkData data = chunk.getData();

		int vertices = 0;
		int indices = 0;
		
		int vbc = visibleBlocks.size();
		
		if (vbc == 0)
		{
			return null;
		}

		for (int i = 0; i < vbc; ++i)
		{
			int index = visibleBlocks.get(i);
			int d = data.getBlockData(index);
			int f = ChunkData.getFaceMask(d);
			Block bl = BlockManager.getBlock(ChunkData.getType(d));
			BlockShape br = bl.getShape();
			vertices += br.physicalVertexCount(f);
			indices += br.physicalIndexCount(f);
		}
		
		if (indices == 0)
		{
			return null;
		}

		ByteBuffer indexBuffer = ByteBuffer.allocateDirect(indices * 2).order(ByteOrder.nativeOrder());
		ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices * 12).order(ByteOrder.nativeOrder());


		Vec3i posi = new Vec3i();
		Vec3f posf = new Vec3f();
		for (int i = 0; i < vbc; ++i)
		{
			int index = visibleBlocks.get(i);
			ChunkData.posForBlock(index, posi);
			posf.set(worldPos).add(posi).add(0.5f, 0.5f, 0.5f);
			int d = data.getBlockData(index);
			int f = ChunkData.getFaceMask(d);
			Block bl = BlockManager.getBlock(ChunkData.getType(d));
			BlockShape br = bl.getShape();
			br.putInPhysicalBuffer(vertexBuffer, indexBuffer, posf, f);
		}

		if (vbc != visibleBlocks.size())
		{
			System.out.println("Concurrency problem! Visible block count changed!");
		}

		indexBuffer.flip();

		TriangleIndexVertexArray array = new TriangleIndexVertexArray();
		IndexedMesh mesh = new IndexedMesh();
		mesh.indexType = ScalarType.SHORT;
		mesh.numTriangles = indices / 3;
		mesh.numVertices = vertices;
		mesh.triangleIndexBase = indexBuffer;
		mesh.triangleIndexStride = 6;
		mesh.vertexBase = vertexBuffer;
		mesh.vertexStride = 12;
		array.addIndexedMesh(mesh, mesh.indexType);
		BvhTriangleMeshShape shape = new BvhTriangleMeshShape(array, true, true);
		return shape;
	}
}
