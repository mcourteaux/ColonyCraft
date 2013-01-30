/*
 * Copyright 2011 Benjamin Glatzel <benjamin.glatzel@me.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.colonycraft.rendering.camera;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.colonycraft.math.AABB;
import com.colonycraft.math.MathHelper;
import com.colonycraft.math.Vec3f;

/**
 * View frustum usable for frustum culling.
 * 
 * @author Benjamin Glatzel <benjamin.glatzel@me.com>
 */
public class ViewFrustum
{

	private static final Vec3f VEC3F_ZERO = new Vec3f();

	private final FrustumPlane[] planes = new FrustumPlane[6];

	private final FloatBuffer proj = BufferUtils.createFloatBuffer(16);
	private final FloatBuffer model = BufferUtils.createFloatBuffer(16);
	private final FloatBuffer clip = BufferUtils.createFloatBuffer(16);

	/**
	 * Init. a new view frustum.
	 */
	public ViewFrustum()
	{
		for (int i = 0; i < 6; i++)
			planes[i] = new FrustumPlane();
	}

	/**
	 * Updates the view frustum using the currently active modelview and
	 * projection matrices.
	 */
	public void updateFrustum()
	{
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, proj);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, model);

		/* Multiply both matrixes */
		clip.put(0, model.get(0) * proj.get(0) + model.get(1) * proj.get(4) + model.get(2) * proj.get(8) + model.get(3) * proj.get(12));
		clip.put(1, model.get(0) * proj.get(1) + model.get(1) * proj.get(5) + model.get(2) * proj.get(9) + model.get(3) * proj.get(13));
		clip.put(2, model.get(0) * proj.get(2) + model.get(1) * proj.get(6) + model.get(2) * proj.get(10) + model.get(3) * proj.get(14));
		clip.put(3, model.get(0) * proj.get(3) + model.get(1) * proj.get(7) + model.get(2) * proj.get(11) + model.get(3) * proj.get(15));

		clip.put(4, model.get(4) * proj.get(0) + model.get(5) * proj.get(4) + model.get(6) * proj.get(8) + model.get(7) * proj.get(12));
		clip.put(5, model.get(4) * proj.get(1) + model.get(5) * proj.get(5) + model.get(6) * proj.get(9) + model.get(7) * proj.get(13));
		clip.put(6, model.get(4) * proj.get(2) + model.get(5) * proj.get(6) + model.get(6) * proj.get(10) + model.get(7) * proj.get(14));
		clip.put(7, model.get(4) * proj.get(3) + model.get(5) * proj.get(7) + model.get(6) * proj.get(11) + model.get(7) * proj.get(15));

		clip.put(8, model.get(8) * proj.get(0) + model.get(9) * proj.get(4) + model.get(10) * proj.get(8) + model.get(11) * proj.get(12));
		clip.put(9, model.get(8) * proj.get(1) + model.get(9) * proj.get(5) + model.get(10) * proj.get(9) + model.get(11) * proj.get(13));
		clip.put(10, model.get(8) * proj.get(2) + model.get(9) * proj.get(6) + model.get(10) * proj.get(10) + model.get(11) * proj.get(14));
		clip.put(11, model.get(8) * proj.get(3) + model.get(9) * proj.get(7) + model.get(10) * proj.get(11) + model.get(11) * proj.get(15));

		clip.put(12, model.get(12) * proj.get(0) + model.get(13) * proj.get(4) + model.get(14) * proj.get(8) + model.get(15) * proj.get(12));
		clip.put(13, model.get(12) * proj.get(1) + model.get(13) * proj.get(5) + model.get(14) * proj.get(9) + model.get(15) * proj.get(13));
		clip.put(14, model.get(12) * proj.get(2) + model.get(13) * proj.get(6) + model.get(14) * proj.get(10) + model.get(15) * proj.get(14));
		clip.put(15, model.get(12) * proj.get(3) + model.get(13) * proj.get(7) + model.get(14) * proj.get(11) + model.get(15) * proj.get(15));

		/* Extract the planes: ax + by + cz + d = 0*/
		
		// RIGHT
		planes[0].setA(clip.get(3) - clip.get(0));
		planes[0].setB(clip.get(7) - clip.get(4));
		planes[0].setC(clip.get(11) - clip.get(8));
		planes[0].setD(clip.get(15) - clip.get(12));
		planes[0].normalize();

		// LEFT
		planes[1].setA(clip.get(3) + clip.get(0));
		planes[1].setB(clip.get(7) + clip.get(4));
		planes[1].setC(clip.get(11) + clip.get(8));
		planes[1].setD(clip.get(15) + clip.get(12));
		planes[1].normalize();

		// BOTTOM
		planes[2].setA(clip.get(3) + clip.get(1));
		planes[2].setB(clip.get(7) + clip.get(5));
		planes[2].setC(clip.get(11) + clip.get(9));
		planes[2].setD(clip.get(15) + clip.get(13));
		planes[2].normalize();

		// TOP
		planes[3].setA(clip.get(3) - clip.get(1));
		planes[3].setB(clip.get(7) - clip.get(5));
		planes[3].setC(clip.get(11) - clip.get(9));
		planes[3].setD(clip.get(15) - clip.get(13));
		planes[3].normalize();

		// FAR
		planes[4].setA(clip.get(3) - clip.get(2));
		planes[4].setB(clip.get(7) - clip.get(6));
		planes[4].setC(clip.get(11) - clip.get(10));
		planes[4].setD(clip.get(15) - clip.get(14));
		planes[4].normalize();

		// NEAR
		planes[5].setA(clip.get(3) + clip.get(2));
		planes[5].setB(clip.get(7) + clip.get(6));
		planes[5].setC(clip.get(11) + clip.get(10));
		planes[5].setD(clip.get(15) + clip.get(14));
		planes[5].normalize();
	}

	/**
	 * Returns true if the given point intersects the view frustum.
	 */
	public boolean intersects(float x, float y, float z)
	{
		for (int i = 0; i < 6; i++)
		{
			if (planes[i].getA() * x + planes[i].getB() * y + planes[i].getC() * z + planes[i].getD() <= 0)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if this view frustum intersects the given AABB.
	 */
	public boolean intersects(AABB aabb)
	{

		if (aabb == null)
			return false;

		Vec3f[] aabbVertices = aabb.getVertices();
		Vec3f rp = VEC3F_ZERO; // Reference Point for rendereing

		outer: for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 8; ++j)
			{
				if (planes[i].getA() * (aabbVertices[j].x - rp.x) + planes[i].getB() * (aabbVertices[j].y - rp.y) + planes[i].getC() * (aabbVertices[j].z - rp.z) + planes[i].getD() > 0)
				{
					continue outer;
				}
			}
			return false;
		}
		return true;
	}

	public class FrustumPlane
	{

		private float a, b, c, d;

		/**
		 * Init. a new view frustum with the default values in place.
		 */
		public FrustumPlane()
		{
			// Do nothing.
		}

		/**
		 * Init. a new view frustum with a given plane equation. ax + by + cz +
		 * d = 0
		 */
		public FrustumPlane(float a, float b, float c, float d)
		{
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		/**
		 * Normalizes this plane.
		 */
		public void normalize()
		{
			float t = MathHelper.sqrt(a * a + b * b + c * c);
			a /= t;
			b /= t;
			c /= t;
			d /= t;
		}

		public float getA()
		{
			return a;
		}

		public void setA(float a)
		{
			this.a = a;
		}

		public float getB()
		{
			return b;
		}

		public void setB(float b)
		{
			this.b = b;
		}

		public float getC()
		{
			return c;
		}

		void setC(float c)
		{
			this.c = c;
		}

		public float getD()
		{
			return d;
		}

		public void setD(float d)
		{
			this.d = d;
		}
	}
}
