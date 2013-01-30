package com.colonycraft.world;

import com.colonycraft.math.AABB;
import com.colonycraft.math.Vec3f;
import com.colonycraft.rendering.world.ChunkMesh;
import com.colonycraft.world.blocks.shapes.BlockShape;

public class Block
{

	protected int type;
	protected String name;
	
	protected boolean renders;
	protected boolean hasAlpha;
	protected boolean solid;
	protected boolean raycasts;
	protected boolean lightPasses;
	
	protected BlockShape brush;
	protected int mesh;
	protected int alwaysVisibleFaces;
	protected AABB aabb;
	
	protected Block(int type, String name)
	{
		this.type = type;
		this.name = name;
		
		renders = true;
		hasAlpha = false;
		solid = true;
		raycasts = true;
		
		aabb = new AABB(new Vec3f(), new Vec3f(0.5f, 0.5f, 0.5f));
		
		mesh = ChunkMesh.MESH_OPAQUE;
	}	
	
	public int getType()
	{
		return type;
	}
	public String getName()
	{
		return name;
	}
	public boolean hasAlpha()
	{
		return hasAlpha;
	}
	public boolean isSolid()
	{
		return solid;
	}
	
	public boolean raycasts()
	{
		return raycasts;
	}
	
	public boolean renders()
	{
		return renders;
	}
	
	public boolean lightPasses()
	{
		return lightPasses;
	}
	
	public BlockShape getShape()
	{
		return brush;
	}
	
	public int getMesh()
	{
		return mesh;
	}
	
	public int getAlwaysVisibleFaces()
	{
		return alwaysVisibleFaces;
	}

	public AABB getAABB()
	{
		return aabb;
	}
}
