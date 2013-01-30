package com.colonycraft.world.blocks;

import com.colonycraft.rendering.world.ChunkMesh;
import com.colonycraft.world.Block;
import com.colonycraft.world.blocks.shapes.CrossedShape;

public class BlockTallGrass extends Block
{

	protected BlockTallGrass()
	{
		super(4, "Tall Grass");
		mesh = ChunkMesh.MESH_GRASS;
		
		lightPasses = true;
		hasAlpha = true;
		raycasts = true;
		solid = false;
		
		brush = new CrossedShape("Tall Grass");
		
	}

}
