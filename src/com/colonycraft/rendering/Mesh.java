package com.colonycraft.rendering;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;

import com.colonycraft.math.Vec3f;

public class Mesh
{

	private int vertexBufferHandle;
	private int indexBufferHandle;
	
	private FloatBuffer vertexBuffer;
	private IntBuffer indexBuffer;
	
	private int size;
	private int vertices;
	private int vertexSize;
	private int indices;
	
	private int usage;
	
	public Mesh(int vertices, int vertexSize, int indices)
	{
		this.vertexBuffer = ByteBuffer.allocateDirect(vertices * vertexSize * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.indexBuffer = ByteBuffer.allocateDirect(indices * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		this.vertexBufferHandle = GL15.glGenBuffers();
		this.indexBufferHandle = GL15.glGenBuffers();
		this.vertices = vertices;
		this.vertexSize = vertexSize;
		this.size = vertexSize * vertices;
		this.indices = indices;
		
		usage = GL15.GL_STATIC_DRAW;
		
	}
	
	public void releaseVRAMBuffers()
	{
		GL15.glDeleteBuffers(vertexBufferHandle);
		GL15.glDeleteBuffers(indexBufferHandle);
	}
	
	public FloatBuffer getVertexBuffer()
	{
		return vertexBuffer;
	}
	
	public IntBuffer getIndexBuffer()
	{
		return indexBuffer;
	}
	
	public int vertices()
	{
		return vertices;
	}
	
	public int vertexSize()
	{
		return vertexSize;
	}
	
	public int indices()
	{
		return indices;
	}
	
	public void setUsage(int usage)
	{
		this.usage = usage;
	}
	
	public void releaseRAMBuffers()
	{
		vertexBuffer = null;
		indexBuffer = null;
	}
	
	public void uploadVertexBuffer()
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, usage);
	}
	
	public void uploadIndexBuffer()
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, indexBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, indexBuffer, usage);
	}

	public int getVertexBufferHandle()
	{
		return vertexBufferHandle;
	}
	
	public int getIndexBufferHandle()
	{
		return indexBufferHandle;
	}
	
	public int getRawSize()
	{
		return size;
	}

	/**
	 * Relaxes the first three components of every vertex using the given relaxation value.
	 * @param d Relaxation value
	 */
	public void relaxVertexBuffer(float d)
	{
		Vec3f vec = new Vec3f();
		for (int i = 0; i < vertices; ++i)
		{
			int offset = i * vertexSize;
			vec.x = vertexBuffer.get(offset + 0);
			vec.y = vertexBuffer.get(offset + 1);
			vec.z = vertexBuffer.get(offset + 2);
			
			vec.relax(d);
			
			vertexBuffer.put(offset + 0, vec.x);
			vertexBuffer.put(offset + 1, vec.y);
			vertexBuffer.put(offset + 2, vec.z);
		}
	}
	
}
