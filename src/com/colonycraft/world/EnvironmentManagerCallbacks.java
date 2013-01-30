package com.colonycraft.world;

public interface EnvironmentManagerCallbacks
{

	public void unloadChunk(Chunk oldChunk);
	
	public void loadChunk(Chunk chunk);

}
