package com.colonycraft.world.blocks.shapes;

import static com.colonycraft.rendering.world.ChunkMeshBuilder.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.colonycraft.TextureStorage;
import com.colonycraft.math.Vec2f;
import com.colonycraft.math.Vec3f;
import com.colonycraft.rendering.Atlas;
import com.colonycraft.rendering.Mesh;
import com.colonycraft.rendering.world.LightBuffer;


public class CrossedShape extends BlockShape
{
	private static Atlas atlas;
	private Vec2f uv;
	
	public CrossedShape(String tileName)
	{
		atlas = TextureStorage.getAtlas("terrain");
		atlas.getUV(atlas.getTileIndex(tileName), uv = new Vec2f());
	}

	@Override
	public void putInMesh(Mesh mesh, Vec3f pos, int faceMask, LightBuffer lightBuffer)
	{
		FloatBuffer vertexBuffer = mesh.getVertexBuffer();

		byte lightB = (byte) lightBuffer.getB(1, 1, 1);
		byte lightS = (byte) lightBuffer.getS(1, 1, 1);
		
		float x = pos.x;
		float y = pos.y;
		float z = pos.z;
		

		float tsw = atlas.getTileTextureW();
		float tsh = atlas.getTileTextureH();
		
		/* Blade 0 */
		put3f(vertexBuffer, x - 0.5f, y + 0.5f, z - 0.5f);
		putLight(vertexBuffer, lightB);
		putLight(vertexBuffer, lightS);
		put2f(vertexBuffer, uv.x, uv.y);

		put3f(vertexBuffer, x + 0.5f, y + 0.5f, z + 0.5f);
		putLight(vertexBuffer, lightB);
		putLight(vertexBuffer, lightS);
		put2f(vertexBuffer, uv.x + tsw, uv.y);

		put3f(vertexBuffer, x + 0.5f, y - 0.5f, z + 0.5f);
		putLight(vertexBuffer, lightB);
		putLight(vertexBuffer, lightS);
		put2f(vertexBuffer, uv.x + tsw, uv.y + tsh);

		put3f(vertexBuffer, x - 0.5f, y - 0.5f, z - 0.5f);
		putLight(vertexBuffer, lightB);
		putLight(vertexBuffer, lightS);
		put2f(vertexBuffer, uv.x, uv.y + tsh);

		/* Blade 1 */
		put3f(vertexBuffer, x + 0.5f, y + 0.5f, z - 0.5f);
		putLight(vertexBuffer, lightB);
		putLight(vertexBuffer, lightS);
		put2f(vertexBuffer, uv.x, uv.y);

		put3f(vertexBuffer, x - 0.5f, y + 0.5f, z + 0.5f);
		putLight(vertexBuffer, lightB);
		putLight(vertexBuffer, lightS);
		put2f(vertexBuffer, uv.x + tsw, uv.y);

		put3f(vertexBuffer, x - 0.5f, y - 0.5f, z + 0.5f);
		putLight(vertexBuffer, lightB);
		putLight(vertexBuffer, lightS);
		put2f(vertexBuffer, uv.x + tsw, uv.y + tsh);

		put3f(vertexBuffer, x + 0.5f, y - 0.5f, z - 0.5f);
		putLight(vertexBuffer, lightB);
		putLight(vertexBuffer, lightS);
		put2f(vertexBuffer, uv.x, uv.y + tsh);
	}

	@Override
	public int vertexCount(int faces)
	{
		return 8;
	}

	@Override
	public int indexCount(int faces)
	{
		return 0;
	}

	@Override
	public void putInPhysicalBuffer(ByteBuffer vertexBuffer, ByteBuffer indexBuffer, Vec3f pos, int faceMask)
	{
		
	}

	@Override
	public int physicalVertexCount(int faces)
	{
		return 0;
	}

	@Override
	public int physicalIndexCount(int faces)
	{
		return 0;
	}

}
