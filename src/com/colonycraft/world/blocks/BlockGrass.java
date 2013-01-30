package com.colonycraft.world.blocks;

import com.colonycraft.Side;
import com.colonycraft.world.Block;
import com.colonycraft.world.blocks.shapes.CubeShape;

public class BlockGrass extends Block
{

	public BlockGrass()
	{
		super(2, "Grass");
		CubeShape b = new CubeShape();
		b.setTexture(Side.BOTTOM, "Dirt");
		b.setTexture(Side.TOP, "GrassTop");
		b.setTextureMantle("GrassSide");
		brush = b;
	}
}
