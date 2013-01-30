package com.colonycraft.world.physics;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.colonycraft.ColonyCraft;
import com.colonycraft.utilities.FastArrayList;
import com.colonycraft.world.Chunk;
import com.colonycraft.world.ChunkDistanceComparator;
import com.colonycraft.world.World;

public class PhysicsManager
{

	private World world;
	private DynamicsWorld dynamicsWorld;
	private ChunkShapeBuilderDistanceComparator csbdc;
	private List<Chunk> physicalChunks;
	private List<ChunkShapeBuilder> enqueuedChunkShapeBuilders;
	private ChunkShapeBuilder workingShapeBuilder;

	private List<Chunk> chunksToRemove;

	public PhysicsManager(World world)
	{
		this.world = world;
		this.csbdc = new ChunkShapeBuilderDistanceComparator();
		this.physicalChunks = new FastArrayList<Chunk>();
		this.chunksToRemove = new FastArrayList<Chunk>();
		this.enqueuedChunkShapeBuilders = new FastArrayList<ChunkShapeBuilder>();
		createDynamicsWorld();
	}

	private void createDynamicsWorld()
	{

		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		Vector3f worldAabbMin = new Vector3f(-1000, -1000, -1000);
		Vector3f worldAabbMax = new Vector3f(1000, 1000, 1000);
		final int maxProxies = 4196;

		BroadphaseInterface broadphase = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -9.81f, 0));
	}

	public void update()
	{

		if (workingShapeBuilder != null)
		{
			if (workingShapeBuilder.isReady())
			{
				addChunk(workingShapeBuilder.getChunk(), workingShapeBuilder.getShape(), workingShapeBuilder.update);
				workingShapeBuilder = null;
			}
		}
		dynamicsWorld.stepSimulation(ColonyCraft.getIntance().getStep());
		dynamicsWorld.clearForces();

		if (workingShapeBuilder == null && enqueuedChunkShapeBuilders.size() > 0)
		{
			sortBuilders();
			ChunkShapeBuilder csb = enqueuedChunkShapeBuilders.get(0);
			if (!csb.getChunk().isLoading())
			{
				workingShapeBuilder = enqueuedChunkShapeBuilders.remove(0);
				Thread th = new Thread(workingShapeBuilder);
				th.start();
			}
		}

		for (int i = 0; i < physicalChunks.size(); ++i)
		{
			Chunk c = physicalChunks.get(i);
			if (c.isBodyDirty())
			{
				updateChunk(c);
			}
		}
	}

	public void removeChunk(Chunk chunk)
	{
		RigidBody b = chunk.getBody();
		if (b != null)
		{
			dynamicsWorld.removeRigidBody(b);
			chunk.setBody(null);
			b.destroy();
		}
		boolean removed = physicalChunks.remove(chunk);
		if (!removed)
		{
			chunksToRemove.add(chunk);
		}
	}

	public void addChunk(Chunk chunk)
	{
		chunksToRemove.remove(chunk);
		enqueuedChunkShapeBuilders.add(new ChunkShapeBuilder(chunk));
		chunk.setBodyClean();
	}

	public void updateChunk(Chunk chunk)
	{
		ChunkShapeBuilder csb = new ChunkShapeBuilder(chunk);
		csb.update = true;
		enqueuedChunkShapeBuilders.add(0, csb);
		chunk.setBodyClean();
	}

	private void addChunk(Chunk chunk, CollisionShape shape, boolean update)
	{
		if (chunksToRemove.contains(chunk))
		{
			chunksToRemove.remove(chunk);
			return;
		}
		if (shape != null)
		{
			Transform tr = new Transform();
			tr.setIdentity();
			MotionState motionState = new DefaultMotionState(tr);

			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0.0f, motionState, shape, new Vector3f());

			RigidBody body = new RigidBody(rbInfo);
			body.setRestitution(0.2f);
			dynamicsWorld.addRigidBody(body);
			if (update && chunk.getBody() != null)
			{
				dynamicsWorld.removeRigidBody(chunk.getBody());
			} else
			{
				physicalChunks.add(chunk);
			}
			chunk.setBody(body);
		} else
		{
			if (update && chunk.getBody() != null)
			{
				dynamicsWorld.removeRigidBody(chunk.getBody());
			}
			chunk.setBody(null);
		}
	}

	public List<Chunk> getPhysicalChunks()
	{
		return physicalChunks;
	}

	public void addRigidBody(RigidBody body)
	{
		dynamicsWorld.addRigidBody(body);
	}
	
	private void sortBuilders()
	{
		csbdc.setReference(world.getActivePlayer().getTransform().origin);
		Collections.sort(enqueuedChunkShapeBuilders, csbdc);
	}
	
	private static class ChunkShapeBuilderDistanceComparator implements Comparator<ChunkShapeBuilder>
	{
		
		private ChunkDistanceComparator cdc = new ChunkDistanceComparator();
		

		public void setReference(Vector3f ref)
		{
			cdc.setReference(ref);
		}

		@Override
		public int compare(ChunkShapeBuilder o1, ChunkShapeBuilder o2)
		{
			return cdc.compare(o1.getChunk(), o2.getChunk());
		}
		
	}

}
