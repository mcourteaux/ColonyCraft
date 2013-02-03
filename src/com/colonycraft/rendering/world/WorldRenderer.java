package com.colonycraft.rendering.world;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec3f;
import com.colonycraft.rendering.Shader;
import com.colonycraft.rendering.ShaderStorage;
import com.colonycraft.world.Chunk;
import com.colonycraft.world.World;

public class WorldRenderer
{

	private World world;

	private Shader shChunk;
	private int shChunkFogColor;
	private int shChunkFog;
	private int shChunkSun;
	private int shChunkFlicker;

	private Shader shGrass;
	private int shGrassWave;
	private int shGrassFogColor;
	private int shGrassFog;
	private int shGrassSun;
	private int shGrassFlicker;

	private Vec3f fogColor;

	public WorldRenderer(World world)
	{
		this.world = world;
		this.fogColor = calculateFogColor(world.getViewingDistance());

		this.shChunk = ShaderStorage.getShader("chunk");
		this.shChunkFog = shChunk.getUniform("fog");
		this.shChunkFogColor = shChunk.getUniform("fogColor");
		this.shChunkSun = shChunk.getUniform("sun");
		this.shChunkFlicker = shChunk.getUniform("flicker");

		this.shGrass = ShaderStorage.getShader("grass");
		this.shGrassFog = shGrass.getUniform("fog");
		this.shGrassFogColor = shGrass.getUniform("fogColor");
		this.shGrassSun = shGrass.getUniform("sun");
		this.shGrassFlicker = shGrass.getUniform("flicker");
		this.shGrassWave = shGrass.getUniform("wave");
	}

	private Vec3f calculateFogColor(float viewingDistance)
	{
		Vec3f nearFogColor = new Vec3f(1.0f, 1.0f, 1.0f);
		Vec3f farFogColor = new Vec3f(0.37f, 0.67f, 0.89f);
		float near = 100.0f;
		float far = 300.0f;
		float f = MathHelper.lerp(MathHelper.clamp(viewingDistance, near, far), near, far, 0.0f, 1.0f);
		return nearFogColor.scale(1.0f - f).addFactor(farFogColor, f);
	}

	public void render()
	{
		/* Clear the framebuffer */
		float s = world.getSunligth();
		GL11.glClearColor(s * fogColor.x, s * fogColor.y, s * fogColor.z, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		/* State changes */
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_CULL_FACE);

		shChunk.useProgram();

		/* Set the fog color for the chunk shader */
		GL20.glUniform2f(shChunkFog, world.getViewingDistance() * 0.8f, world.getViewingDistance());
		GL20.glUniform4f(shChunkFogColor, s * fogColor.x, s * fogColor.y, s * fogColor.z, 1.0f);

		/* Set light */
		GL20.glUniform1f(shChunkSun, s);
		GL20.glUniform1f(shChunkFlicker, world.getFlicker());

		/* Render all the chunks */
		List<Chunk> visibleChunks = world.getLocalEnvironmentManager().getVisibleChunks();
		for (int i = 0; i < visibleChunks.size(); ++i)
		{
			Chunk ch = visibleChunks.get(i);
			if (ch.isMeshDirty())
			{
				ch.rebuildMesh();
			}
			ChunkMeshRenderer.renderChunkMesh(world, ch, ChunkMesh.MESH_OPAQUE);
		}

		/* Render grass */
		shGrass.useProgram();
		/* Set the fog color for the grass shader */
		GL20.glUniform2f(shGrassFog, world.getViewingDistance() * 0.8f, world.getViewingDistance());
		GL20.glUniform4f(shGrassFogColor, s * fogColor.x, s * fogColor.y, s * fogColor.z, 1.0f);

		/* Set light */
		GL20.glUniform1f(shGrassSun, s);
		GL20.glUniform1f(shGrassFlicker, world.getFlicker());

		/* Set the waving */
		GL20.glUniform1f(shGrassWave, world.getTime() * 1.0f);

		for (int i = 0; i < visibleChunks.size(); ++i)
		{
			Chunk ch = visibleChunks.get(i);
			ChunkMeshRenderer.renderChunkMesh(world, ch, ChunkMesh.MESH_GRASS);
		}

		Shader.useDefaultProgram();

		/* Render the player */
		world.getActivePlayer().render();

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
		{
			renderDebugAABBs();
		}
	}

	private void renderDebugAABBs()
	{
		// GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		List<Chunk> chunks = world.getLocalEnvironmentManager().getLocalChunks();
		for (int i = 0; i < chunks.size(); ++i)
		{
			Chunk ch = chunks.get(i);
			if (ch.getBody() != null)
			{
				ch.getVisibleContentAABB().render(0.0f, 0.0f, 1.0f, 0.1f);
			} else
			{
				ch.getVisibleContentAABB().render(1.0f, 0.0f, 0.0f, 0.1f);
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
	}

}
