package com.colonycraft.world.entities;

import com.bulletphysics.linearmath.Transform;
import com.colonycraft.world.World;

public abstract class Entity
{
	protected World world;
	protected Transform transform;
	
	public Entity(World world)
	{
		this.world = world;
		transform = new Transform();
	}
	
	public Transform getTransform()
	{
		return transform;
	}
	
	public void render()
	{

	}
	
	public void update()
	{
		
	}
	
	
}
