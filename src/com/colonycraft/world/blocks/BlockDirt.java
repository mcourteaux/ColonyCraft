package com.colonycraft.world.blocks;

import com.colonycraft.world.Block;
import com.colonycraft.world.blocks.shapes.CubeShape;

public class BlockDirt extends Block
{

	public BlockDirt()
	{
		super(1, "Dirt");
		
		CubeShape b = new CubeShape();
		b.setTexture("Dirt");
		brush = b;
	}
}
