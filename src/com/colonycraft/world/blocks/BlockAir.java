package com.colonycraft.world.blocks;

import com.colonycraft.world.Block;

public class BlockAir extends Block
{
	public BlockAir()
	{
		super(0, "Air");
		this.raycasts = false;
		this.solid = false;
		this.renders = false;
		this.lightPasses = true;
	}

}
