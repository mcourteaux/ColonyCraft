package com.colonycraft.world.blocks.shapes;

import static com.colonycraft.rendering.world.ChunkMeshBuilder.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.colonycraft.Side;
import com.colonycraft.TextureStorage;
import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec2f;
import com.colonycraft.math.Vec3f;
import com.colonycraft.rendering.Atlas;
import com.colonycraft.rendering.Mesh;
import com.colonycraft.rendering.world.LightBuffer;

public class CubeShape extends BlockShape
{
	private static Atlas atlas;

	private Vec2f textureOffsets[];
	private float insets[];

	public CubeShape()
	{
		textureOffsets = new Vec2f[6];
		for (int i = 0; i < 6; ++i)
			textureOffsets[i] = new Vec2f();
		insets = new float[6];
		atlas = TextureStorage.getAtlas("terrain");
	}

	/**
	 * Sets the same texture to all sides
	 * 
	 * @param tileName
	 */
	public void setTexture(String tileName)
	{
		atlas.getUV(atlas.getTileIndex(tileName), this.textureOffsets[0]);
		for (int i = 1; i < 6; ++i)
		{
			textureOffsets[i].set(textureOffsets[0]);
		}
	}

	public void setTextureMantle(String tileName)
	{
		atlas.getUV(atlas.getTileIndex(tileName), this.textureOffsets[Side.FRONT.ordinal()]);
		atlas.getUV(atlas.getTileIndex(tileName), this.textureOffsets[Side.BACK.ordinal()]);
		atlas.getUV(atlas.getTileIndex(tileName), this.textureOffsets[Side.LEFT.ordinal()]);
		atlas.getUV(atlas.getTileIndex(tileName), this.textureOffsets[Side.RIGHT.ordinal()]);
	}

	public void setTexture(Side side, String tileName)
	{
		atlas.getUV(atlas.getTileIndex(tileName), this.textureOffsets[side.ordinal()]);
	}

	private float getInset(Side side)
	{
		return insets[side.ordinal()];
	}

	@Override
	public void putInMesh(Mesh mesh, Vec3f pos, int faceMask, LightBuffer lightBuffer)
	{

		FloatBuffer vertexBuffer = mesh.getVertexBuffer();

		float x = pos.x;
		float y = pos.y;
		float z = pos.z;

		float tsw = atlas.getTileTextureW();
		float tsh = atlas.getTileTextureH();

		//Vec3f color = new Vec3f(1, 1, 1);

		for (int i = 0, bit = 1; i < 6; ++i, bit <<= 1)
		{
			if ((bit & faceMask) == bit)
			{
				Side side = Side.values()[i];
				Vec2f uv = textureOffsets[i];

				float inset = getInset(side);

				if (side == Side.TOP)
				{
					put3f(vertexBuffer, x - 0.5f, y + 0.5f - inset, z + 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(1, 2, 1), lightBuffer.getB(0, 2, 1), lightBuffer.getB(0, 2, 2), lightBuffer.getB(1, 2, 2));
					putLight4(vertexBuffer, lightBuffer.getS(1, 2, 1), lightBuffer.getS(0, 2, 1), lightBuffer.getS(0, 2, 2), lightBuffer.getS(1, 2, 2));
					put2f(vertexBuffer, uv.x, uv.y);

					put3f(vertexBuffer, x + 0.5f, y + 0.5f - inset, z + 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(1, 2, 1), lightBuffer.getB(2, 2, 1), lightBuffer.getB(1, 2, 2), lightBuffer.getB(2, 2, 2));
					putLight4(vertexBuffer, lightBuffer.getS(1, 2, 1), lightBuffer.getS(2, 2, 1), lightBuffer.getS(1, 2, 2), lightBuffer.getS(2, 2, 2));
					put2f(vertexBuffer, uv.x + tsw, uv.y);

					put3f(vertexBuffer, x + 0.5f, y + 0.5f - inset, z - 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(1, 2, 1), lightBuffer.getB(1, 2, 0), lightBuffer.getB(2, 2, 0), lightBuffer.getB(2, 2, 1));
					putLight4(vertexBuffer, lightBuffer.getS(1, 2, 1), lightBuffer.getS(1, 2, 0), lightBuffer.getS(2, 2, 0), lightBuffer.getS(2, 2, 1));
					put2f(vertexBuffer, uv.x + tsw, uv.y + tsh);

					put3f(vertexBuffer, x - 0.5f, y + 0.5f - inset, z - 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(1, 2, 1), lightBuffer.getB(0, 2, 1), lightBuffer.getB(1, 2, 0), lightBuffer.getB(0, 2, 0));
					putLight4(vertexBuffer, lightBuffer.getS(1, 2, 1), lightBuffer.getS(0, 2, 1), lightBuffer.getS(1, 2, 0), lightBuffer.getS(0, 2, 0));
					put2f(vertexBuffer, uv.x, uv.y + tsh);
				} else if (side == Side.LEFT)
				{
					put3f(vertexBuffer, x - 0.5f + inset, y - 0.5f, z - 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(0, 1, 1), lightBuffer.getB(0, 0, 1), lightBuffer.getB(0, 1, 0), lightBuffer.getB(0, 0, 0));
					putLight4(vertexBuffer, lightBuffer.getS(0, 1, 1), lightBuffer.getS(0, 0, 1), lightBuffer.getS(0, 1, 0), lightBuffer.getS(0, 0, 0));
					put2f(vertexBuffer, uv.x, uv.y + tsh);

					put3f(vertexBuffer, x - 0.5f + inset, y - 0.5f, z + 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(0, 1, 1), lightBuffer.getB(0, 0, 1), lightBuffer.getB(0, 1, 2), lightBuffer.getB(0, 0, 2));
					putLight4(vertexBuffer, lightBuffer.getS(0, 1, 1), lightBuffer.getS(0, 0, 1), lightBuffer.getS(0, 1, 2), lightBuffer.getS(0, 0, 2));
					put2f(vertexBuffer, uv.x + tsw, uv.y + tsh);

					put3f(vertexBuffer, x - 0.5f + inset, y + 0.5f, z + 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(0, 1, 1), lightBuffer.getB(0, 1, 2), lightBuffer.getB(0, 2, 1), lightBuffer.getB(0, 2, 2));
					putLight4(vertexBuffer, lightBuffer.getS(0, 1, 1), lightBuffer.getS(0, 1, 2), lightBuffer.getS(0, 2, 1), lightBuffer.getS(0, 2, 2));
					put2f(vertexBuffer, uv.x + tsw, uv.y);

					put3f(vertexBuffer, x - 0.5f + inset, y + 0.5f, z - 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(0, 1, 1), lightBuffer.getB(0, 1, 0), lightBuffer.getB(0, 2, 0), lightBuffer.getB(0, 2, 1));
					putLight4(vertexBuffer, lightBuffer.getS(0, 1, 1), lightBuffer.getS(0, 1, 0), lightBuffer.getS(0, 2, 0), lightBuffer.getS(0, 2, 1));
					put2f(vertexBuffer, uv.x, uv.y);
				} else if (side == Side.FRONT)
				{
					put3f(vertexBuffer, x - 0.5f, y - 0.5f, z + 0.5f - inset);
					putLight4(vertexBuffer, lightBuffer.getB(1, 1, 2), lightBuffer.getB(0, 0, 2), lightBuffer.getB(0, 1, 2), lightBuffer.getB(1, 0, 2));
					putLight4(vertexBuffer, lightBuffer.getS(1, 1, 2), lightBuffer.getS(0, 0, 2), lightBuffer.getS(0, 1, 2), lightBuffer.getS(1, 0, 2));
					put2f(vertexBuffer, uv.x, uv.y + tsh);

					put3f(vertexBuffer, x + 0.5f, y - 0.5f, z + 0.5f - inset);
					putLight4(vertexBuffer, lightBuffer.getB(1, 1, 2), lightBuffer.getB(1, 0, 2), lightBuffer.getB(2, 1, 2), lightBuffer.getB(2, 0, 2));
					putLight4(vertexBuffer, lightBuffer.getS(1, 1, 2), lightBuffer.getS(1, 0, 2), lightBuffer.getS(2, 1, 2), lightBuffer.getS(2, 0, 2));
					put2f(vertexBuffer, uv.x + tsw, uv.y + tsh);

					put3f(vertexBuffer, x + 0.5f, y + 0.5f, z + 0.5f - inset);
					putLight4(vertexBuffer, lightBuffer.getB(1, 1, 2), lightBuffer.getB(1, 2, 2), lightBuffer.getB(2, 2, 2), lightBuffer.getB(2, 1, 2));
					putLight4(vertexBuffer, lightBuffer.getS(1, 1, 2), lightBuffer.getS(1, 2, 2), lightBuffer.getS(2, 2, 2), lightBuffer.getS(2, 1, 2));
					put2f(vertexBuffer, uv.x + tsw, uv.y);

					put3f(vertexBuffer, x - 0.5f, y + 0.5f, z + 0.5f - inset);
					putLight4(vertexBuffer, lightBuffer.getB(1, 1, 2), lightBuffer.getB(1, 2, 2), lightBuffer.getB(0, 1, 2), lightBuffer.getB(0, 2, 2));
					putLight4(vertexBuffer, lightBuffer.getS(1, 1, 2), lightBuffer.getS(1, 2, 2), lightBuffer.getS(0, 1, 2), lightBuffer.getS(0, 2, 2));
					put2f(vertexBuffer, uv.x, uv.y);
				} else if (side == Side.RIGHT)
				{
					put3f(vertexBuffer, x + 0.5f - inset, y + 0.5f, z - 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(2, 1, 1), lightBuffer.getB(2, 2, 1), lightBuffer.getB(2, 1, 0), lightBuffer.getB(2, 2, 0));
					putLight4(vertexBuffer, lightBuffer.getS(2, 1, 1), lightBuffer.getS(2, 2, 1), lightBuffer.getS(2, 1, 0), lightBuffer.getS(2, 2, 0));
					put2f(vertexBuffer, uv.x, uv.y);

					put3f(vertexBuffer, x + 0.5f - inset, y + 0.5f, z + 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(2, 1, 1), lightBuffer.getB(2, 2, 2), lightBuffer.getB(2, 2, 1), lightBuffer.getB(2, 1, 2));
					putLight4(vertexBuffer, lightBuffer.getS(2, 1, 1), lightBuffer.getS(2, 2, 2), lightBuffer.getS(2, 2, 1), lightBuffer.getS(2, 1, 2));
					put2f(vertexBuffer, uv.x + tsw, uv.y);

					put3f(vertexBuffer, x + 0.5f - inset, y - 0.5f, z + 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(2, 1, 1), lightBuffer.getB(2, 0, 2), lightBuffer.getB(2, 0, 1), lightBuffer.getB(2, 1, 2));
					putLight4(vertexBuffer, lightBuffer.getS(2, 1, 1), lightBuffer.getS(2, 0, 2), lightBuffer.getS(2, 0, 1), lightBuffer.getS(2, 1, 2));
					put2f(vertexBuffer, uv.x + tsw, uv.y + tsh);

					put3f(vertexBuffer, x + 0.5f - inset, y - 0.5f, z - 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(2, 1, 1), lightBuffer.getB(2, 0, 0), lightBuffer.getB(2, 0, 1), lightBuffer.getB(2, 1, 0));
					putLight4(vertexBuffer, lightBuffer.getS(2, 1, 1), lightBuffer.getS(2, 0, 0), lightBuffer.getS(2, 0, 1), lightBuffer.getS(2, 1, 0));
					put2f(vertexBuffer, uv.x, uv.y + tsh);
				} else if (side == Side.BACK)
				{
					put3f(vertexBuffer, x - 0.5f, y + 0.5f, z - 0.5f + inset);
					putLight4(vertexBuffer, lightBuffer.getB(1, 1, 0), lightBuffer.getB(1, 2, 0), lightBuffer.getB(0, 2, 0), lightBuffer.getB(0, 1, 0));
					putLight4(vertexBuffer, lightBuffer.getS(1, 1, 0), lightBuffer.getS(1, 2, 0), lightBuffer.getS(0, 2, 0), lightBuffer.getS(0, 1, 0));
					put2f(vertexBuffer, uv.x, uv.y);

					put3f(vertexBuffer, x + 0.5f, y + 0.5f, z - 0.5f + inset);
					putLight4(vertexBuffer, lightBuffer.getB(1, 1, 0), lightBuffer.getB(1, 2, 0), lightBuffer.getB(2, 2, 0), lightBuffer.getB(2, 1, 0));
					putLight4(vertexBuffer, lightBuffer.getS(1, 1, 0), lightBuffer.getS(1, 2, 0), lightBuffer.getS(2, 2, 0), lightBuffer.getS(2, 1, 0));
					put2f(vertexBuffer, uv.x + tsw, uv.y);

					put3f(vertexBuffer, x + 0.5f, y - 0.5f, z - 0.5f + inset);
					putLight4(vertexBuffer, lightBuffer.getB(1, 1, 0), lightBuffer.getB(1, 0, 0), lightBuffer.getB(2, 0, 0), lightBuffer.getB(2, 1, 0));
					putLight4(vertexBuffer, lightBuffer.getS(1, 1, 0), lightBuffer.getS(1, 0, 0), lightBuffer.getS(2, 0, 0), lightBuffer.getS(2, 1, 0));
					put2f(vertexBuffer, uv.x + tsw, uv.y + tsh);

					put3f(vertexBuffer, x - 0.5f, y - 0.5f, z - 0.5f + inset);
					putLight4(vertexBuffer, lightBuffer.getB(1, 1, 0), lightBuffer.getB(1, 0, 0), lightBuffer.getB(0, 0, 0), lightBuffer.getB(0, 1, 0));
					putLight4(vertexBuffer, lightBuffer.getS(1, 1, 0), lightBuffer.getS(1, 0, 0), lightBuffer.getS(0, 0, 0), lightBuffer.getS(0, 1, 0));
					put2f(vertexBuffer, uv.x, uv.y + tsh);
				} else if (side == Side.BOTTOM)
				{
					put3f(vertexBuffer, x - 0.5f, y - 0.5f + inset, z - 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(1, 0, 1), lightBuffer.getB(1, 0, 0), lightBuffer.getB(0, 0, 0), lightBuffer.getB(0, 0, 1));
					putLight4(vertexBuffer, lightBuffer.getS(1, 0, 1), lightBuffer.getS(1, 0, 0), lightBuffer.getS(0, 0, 0), lightBuffer.getS(0, 0, 1));
					put2f(vertexBuffer, uv.x, uv.y);

					put3f(vertexBuffer, x + 0.5f, y - 0.5f + inset, z - 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(1, 0, 1), lightBuffer.getB(1, 0, 0), lightBuffer.getB(2, 0, 0), lightBuffer.getB(2, 0, 1));
					putLight4(vertexBuffer, lightBuffer.getS(1, 0, 1), lightBuffer.getS(1, 0, 0), lightBuffer.getS(2, 0, 0), lightBuffer.getS(2, 0, 1));
					put2f(vertexBuffer, uv.x + tsw, uv.y);

					put3f(vertexBuffer, x + 0.5f, y - 0.5f + inset, z + 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(1, 0, 1), lightBuffer.getB(1, 0, 2), lightBuffer.getB(2, 0, 2), lightBuffer.getB(2, 0, 1));
					putLight4(vertexBuffer, lightBuffer.getS(1, 0, 1), lightBuffer.getS(1, 0, 2), lightBuffer.getS(2, 0, 2), lightBuffer.getS(2, 0, 1));
					put2f(vertexBuffer, uv.x + tsw, uv.y + tsh);

					put3f(vertexBuffer, x - 0.5f, y - 0.5f + inset, z + 0.5f);
					putLight4(vertexBuffer, lightBuffer.getB(1, 0, 1), lightBuffer.getB(1, 0, 2), lightBuffer.getB(0, 0, 2), lightBuffer.getB(0, 0, 1));
					putLight4(vertexBuffer, lightBuffer.getS(1, 0, 1), lightBuffer.getS(1, 0, 2), lightBuffer.getS(0, 0, 2), lightBuffer.getS(0, 0, 1));
					put2f(vertexBuffer, uv.x, uv.y + tsh);
				}
			}
		}
	}

	@Override
	public void putInPhysicalBuffer(ByteBuffer vertexBuffer, ByteBuffer indexBuffer, Vec3f pos, int faceMask)
	{

		float x = pos.x;
		float y = pos.y;
		float z = pos.z;
		
		for (int i = 0, bit = 1; i < 6; ++i, bit <<= 1)
		{
			if ((bit & faceMask) == bit)
			{
				Side side = Side.values()[i];
				int s = vertexBuffer.position() / 12;
				float inset = getInset(side);

				if (side == Side.TOP)
				{
					put3f(vertexBuffer, x - 0.5f, y + 0.5f - inset, z + 0.5f);
					put3f(vertexBuffer, x + 0.5f, y + 0.5f - inset, z + 0.5f);
					put3f(vertexBuffer, x + 0.5f, y + 0.5f - inset, z - 0.5f);
					put3f(vertexBuffer, x - 0.5f, y + 0.5f - inset, z - 0.5f);
				} else if (side == Side.LEFT)
				{
					put3f(vertexBuffer, x - 0.5f + inset, y - 0.5f, z - 0.5f);
					put3f(vertexBuffer, x - 0.5f + inset, y - 0.5f, z + 0.5f);
					put3f(vertexBuffer, x - 0.5f + inset, y + 0.5f, z + 0.5f);
					put3f(vertexBuffer, x - 0.5f + inset, y + 0.5f, z - 0.5f);
				} else if (side == Side.FRONT)
				{
					put3f(vertexBuffer, x - 0.5f, y - 0.5f, z + 0.5f - inset);
					put3f(vertexBuffer, x + 0.5f, y - 0.5f, z + 0.5f - inset);
					put3f(vertexBuffer, x + 0.5f, y + 0.5f, z + 0.5f - inset);
					put3f(vertexBuffer, x - 0.5f, y + 0.5f, z + 0.5f - inset);
				} else if (side == Side.RIGHT)
				{
					put3f(vertexBuffer, x + 0.5f - inset, y + 0.5f, z - 0.5f);
					put3f(vertexBuffer, x + 0.5f - inset, y + 0.5f, z + 0.5f);
					put3f(vertexBuffer, x + 0.5f - inset, y - 0.5f, z + 0.5f);
					put3f(vertexBuffer, x + 0.5f - inset, y - 0.5f, z - 0.5f);
				} else if (side == Side.BACK)
				{
					put3f(vertexBuffer, x - 0.5f, y + 0.5f, z - 0.5f + inset);
					put3f(vertexBuffer, x + 0.5f, y + 0.5f, z - 0.5f + inset);
					put3f(vertexBuffer, x + 0.5f, y - 0.5f, z - 0.5f + inset);
					put3f(vertexBuffer, x - 0.5f, y - 0.5f, z - 0.5f + inset);
				} else if (side == Side.BOTTOM)
				{
					put3f(vertexBuffer, x - 0.5f, y - 0.5f + inset, z - 0.5f);
					put3f(vertexBuffer, x + 0.5f, y - 0.5f + inset, z - 0.5f);
					put3f(vertexBuffer, x + 0.5f, y - 0.5f + inset, z + 0.5f);
					put3f(vertexBuffer, x - 0.5f, y - 0.5f + inset, z + 0.5f);
				}
				
				indexBuffer.putShort((short) (s + 0));
				indexBuffer.putShort((short) (s + 1));
				indexBuffer.putShort((short) (s + 2));
				indexBuffer.putShort((short) (s + 0));
				indexBuffer.putShort((short) (s + 2));
				indexBuffer.putShort((short) (s + 3));
			}
		}
	}
	
	@Override
	public int physicalIndexCount(int faces)
	{
		return 6 * MathHelper.cardinality(faces, 6);
	}

	@Override
	public int physicalVertexCount(int faces)
	{
		return 4 * MathHelper.cardinality(faces, 6);
	}

	@Override
	public int vertexCount(int faces)
	{
		return MathHelper.cardinality(faces, 6) * 4;
	}

	@Override
	public int indexCount(int faces)
	{
		return MathHelper.cardinality(faces, 6) * 4;
	}

}
