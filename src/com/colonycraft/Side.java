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
package com.colonycraft;

import com.colonycraft.math.Vec3f;
import com.colonycraft.math.Vec3i;

public enum Side
{
	
	
	BACK(new Vec3i(0, 0, -1)), FRONT(new Vec3i(0, 0, 1)), LEFT(new Vec3i(-1, 0, 0)), RIGHT(new Vec3i(1, 0, 0)), TOP(new Vec3i(0, 1, 0)), BOTTOM(new Vec3i(0, -1, 0));

	
	private static Side[] cachedValues;
	private static Side[] mantleSides;
	
	private Vec3i normal;
	private Vec3f vertices[];
	
	
	private Side(Vec3i normal)
	{
		this.normal = normal;
		this.vertices = new Vec3f[4];
		
		for (int i = 0; i < 4; ++i)
		{
			Vec3f v = new Vec3f(normal);
			v.scale(0.5f);
			
			int x0 = i & 1;
			int x1 = i & 2;
			int[] xs = {x0, x1};
			
			if (x0 == 0) x0 = -1;
			if (x1 == 0) x1 = -1;
			
			int e = 0;
			if (normal.x == 0)
			{
				v.x(xs[e++]);
			}
			if (normal.y == 0)
			{
				v.y(xs[e++]);
			}
			if (normal.z == 0)
			{
				v.z(xs[e++]);
			}
			
			vertices[i] = v;
		}
		
	}
	
	public Vec3i getNormal()
	{
		return normal;
	}
	
	public Vec3f[] getFaceVertices()
	{
		return vertices;
	}

	public static Side getOppositeSide(Side side)
	{
		/* This method requires the opposite sides be next to each other in the enumeration. */
		return cachedValues[side.ordinal() ^ 1];
	}
	
	public static final Side[] getSides()
	{
		if (cachedValues == null)
		{
			cachedValues = values();
		}
		return cachedValues;
	}
	
	public static final Side[] getMantleSides()
	{
		if (mantleSides == null)
		{
			mantleSides = new Side[4];
			mantleSides[0] = BACK;
			mantleSides[1] = FRONT;
			mantleSides[2] = LEFT;
			mantleSides[3] = RIGHT;
		}
		return mantleSides;
	}
	
	public static final Side getSide(int index)
	{
		return getSides()[index];
	}
}
