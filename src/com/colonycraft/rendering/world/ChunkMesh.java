package com.colonycraft.rendering.world;

import com.colonycraft.rendering.Mesh;

public class ChunkMesh
{

	public static final int MESHES = 3;
	public static final int MESH_OPAQUE = 0;
	public static final int MESH_TRANSLUCENT = 1;
	public static final int MESH_GRASS = 2;

	private Mesh[] meshes;

	public ChunkMesh()
	{
		meshes = new Mesh[MESHES];
	}

	public Mesh getMesh(int mesh)
	{
		return meshes[mesh];
	}

	public void setMesh(int mesh, Mesh m)
	{
		meshes[mesh] = m;
	}

	public void release()
	{
		for (int i = 0; i < MESHES; ++i)
		{
			if (meshes[i] != null)
			{
				meshes[i].releaseVRAMBuffers();
				meshes[i].releaseRAMBuffers();
			}
		}
	}
}
