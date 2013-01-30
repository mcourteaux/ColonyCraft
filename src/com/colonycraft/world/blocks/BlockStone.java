package com.colonycraft.world.blocks;

import com.colonycraft.world.Block;
import com.colonycraft.world.blocks.shapes.CubeShape;

public class BlockStone extends Block
{

	public BlockStone()
	{
		super(3, "Stone");

		CubeShape b = new CubeShape();
		b.setTexture("Stone");
		brush = b;
	}
}
