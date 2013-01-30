/*******************************************************************************
 * Copyright 2012 Martijn Courteaux <martijn.courteaux@skynet.be>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.colonycraft.rendering.world;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import com.colonycraft.TextureStorage;
import com.colonycraft.rendering.Mesh;
import com.colonycraft.rendering.Shader;
import com.colonycraft.rendering.ShaderStorage;
import com.colonycraft.world.Chunk;
import com.colonycraft.world.World;

public class ChunkMeshRenderer
{
	public static final int STRIDE = 7;
	public static final int POSITION_SIZE = 3;
	public static final int POSITION_OFFSET = 0;
	public static final int LIGHT_OFFSET = POSITION_OFFSET + POSITION_SIZE;
	public static final int LIGTH_SIZE = 2;
	public static final int TEX_COORD_SIZE = 2;
	public static final int TEX_COORD_OFFSET = LIGHT_OFFSET + LIGTH_SIZE;
	public static final int FLOAT_SIZE = 4;

	private static Shader shader = ShaderStorage.getShader("chunk");
	private static Shader grassShader = ShaderStorage.getShader("grass");
	private static int CHUNK_ATTRIBUTE_LIGHT = shader.getAttribute("light");
	private static int GRASS_ATTRIBUTE_LIGHT = grassShader.getAttribute("light");

	public static void renderChunkMesh(World world, Chunk chunk, int meshType)
	{
		/* Bind the correct texture */
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		TextureStorage.getTexture("terrain").bind();

		if (meshType == ChunkMesh.MESH_OPAQUE)
		{
			GL11.glDisable(GL11.GL_BLEND);
		} else if (meshType == ChunkMesh.MESH_TRANSLUCENT || meshType == ChunkMesh.MESH_GRASS)
		{
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0f);
		}

		ChunkMesh cmesh = chunk.getMesh();
		Mesh mesh = cmesh.getMesh(meshType);
		if (mesh == null) return;

		/* Bind the buffer */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getVertexBufferHandle());

		/* Enable the different kinds of data in the buffer */
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		if (meshType == ChunkMesh.MESH_GRASS)
		{
			GL20.glEnableVertexAttribArray(GRASS_ATTRIBUTE_LIGHT);
		} else
		{
			GL20.glEnableVertexAttribArray(CHUNK_ATTRIBUTE_LIGHT);
		}

		/* Define the starting positions */
		GL11.glVertexPointer(POSITION_SIZE, GL11.GL_FLOAT, STRIDE * FLOAT_SIZE, POSITION_OFFSET * FLOAT_SIZE);
		GL11.glTexCoordPointer(TEX_COORD_SIZE, GL11.GL_FLOAT, STRIDE * FLOAT_SIZE, TEX_COORD_OFFSET * FLOAT_SIZE);
		GL20.glVertexAttribPointer(CHUNK_ATTRIBUTE_LIGHT, 2, GL11.GL_FLOAT, false, STRIDE * FLOAT_SIZE, LIGHT_OFFSET * FLOAT_SIZE);

		/* Draw the buffer */
		GL11.glDrawArrays(GL11.GL_QUADS, 0, mesh.vertices());

		/* Unbind the buffer */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		/* Disable the different kinds of data */
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		if (meshType == ChunkMesh.MESH_GRASS)
		{
			GL20.glDisableVertexAttribArray(GRASS_ATTRIBUTE_LIGHT);
		} else
		{
			GL20.glDisableVertexAttribArray(CHUNK_ATTRIBUTE_LIGHT);
		}
		
		if (meshType == ChunkMesh.MESH_TRANSLUCENT || meshType == ChunkMesh.MESH_GRASS)
		{
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
		}
	}

}
