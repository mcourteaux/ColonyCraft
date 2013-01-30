package com.colonycraft.world.blocks;

import com.colonycraft.world.Block;

public class BlockManager
{

	static
	{
		loadBlocks();
	}
	
	private static Block[] blocks;

	public static Block getBlock(int type)
	{
		return blocks[type];
	}
	
	public static Block getBlock(String name)
	{
		int hash = name.hashCode();
		for (int i = 0; i < blocks.length; ++i)
		{
			Block b = blocks[i];
			if (b.getName().hashCode() == hash)
			{
				return b;
			}
		}
		return null;
	}
	
	private static void s(Block b)
	{
		int i = b.getType();
		if (blocks[i] != null)
		{
			System.out.println("Type overwrite: " + i + " (Old: " + blocks[i].getName() + ", New: " + b.getName() + ")");
			return;
		}
		blocks[i] = b;
	}
	
	private static void loadBlocks()
	{
		blocks = new Block[5];
		
		s(new BlockAir());
		s(new BlockDirt());
		s(new BlockGrass());
		s(new BlockStone());
		s(new BlockTallGrass());
	}
}
