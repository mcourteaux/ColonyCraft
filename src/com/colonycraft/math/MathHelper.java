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
package com.colonycraft.math;



public class MathHelper
{

	public static final float f_PI = (float) Math.PI;
	public static final float f_2PI = (float) (2.0d * Math.PI);
	public static float f_PI_div_2 = (float) (0.5d * Math.PI);

	private static float SIN_TABLE[];

	static
	{
		/**
		 * Initializes the sin lookup table
		 */
		SIN_TABLE = new float[0x10000];
		for (int i = 0; i < 0x10000; i++)
		{
			SIN_TABLE[i] = (float) Math.sin(((double) i * 3.1415926535897931D * 2D) / 65536D);
		}
	}

	public static final int floorDivision(int i, int divisor)
	{
		return floor(((float) i) / divisor);
	}

	public static final float sin(float f)
	{
		return SIN_TABLE[(int) (f * 10430.38F) & 0xffff];
	}

	public static final float cos(float f)
	{
		return SIN_TABLE[(int) (f * 10430.38F + 16384F) & 0xffff];
	}
	
	public static float tan(float f)
	{
		return sin(f) / cos(f);
	}

	public static int floor(float f)
	{
		int i = (int) f;
		return f >= (float) i ? i : i - 1;
	}

	public static int floor(double d)
	{
		int i = (int) d;
		return d >= (double) i ? i : i - 1;
	}

	public static int ceil(float f)
	{
		return floor(f) + 1;
	}

	public static int round(double d)
	{
		return floor(d + 0.5d);
	}

	public static int round(float f)
	{
		return floor(f + 0.5f);
	}

	public static int roundToZero(float x)
	{
		return (int) x;
	}

	public static int pow(int i, int exp)
	{
		if (exp == 0)
		{
			return 1;
		}
		return i * pow(i, exp - 1);
	}

	/**
	 * Simplifies an angle, given in radians
	 * 
	 * @param rad
	 *            the angle
	 * @return same angle within the range <bb>]-PI, PI]</bb>
	 */
	public static float simplifyRadians(float rad)
	{
		while (rad <= -f_PI)
		{
			rad += f_2PI;
		}
		while (rad > f_PI)
		{
			rad -= f_2PI;
		}
		return rad;
	}

	/**
	 * Simplifies an angle, given in degrees
	 * 
	 * @param rad
	 *            the angle
	 * @return same angle within the range <bb>]-180, 180]</bb>
	 */
	public static float simplifyDegrees(float deg)
	{
		while (deg <= -180.0f)
		{
			deg += 360.0f;
		}
		while (deg > 180.0f)
		{
			deg -= 360.0f;
		}
		return deg;
	}

	/**
	 * Clamps a float
	 * 
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static float clamp(float value, float min, float max)
	{
		return value < min ? min : value > max ? max : value;
	}

	/**
	 * Clamps an int
	 * 
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static int clamp(int value, int min, int max)
	{
		return value < min ? min : value > max ? max : value;
	}

	public static int getPowerOfTwoBiggerThan(int i)
	{
		int r = 1;
		while (r < i)
		{
			r <<= 1;
		}
		return r;
	}

	private static final int[][] EMPTY_MATRIX = new int[0][0];

	public static int[][] cropMatrix(int[][] matrix)
	{
		int minX = 10;
		int minY = 10;
		int maxX = 0;
		int maxY = 0;
		boolean containsData = false;

		for (int y = 0; y < matrix.length; ++y)
		{
			for (int x = 0; x < matrix[0].length; ++x)
			{
				int elem = matrix[y][x];
				if (elem != 0)
				{
					minX = Math.min(minX, x);
					minY = Math.min(minY, y);
					maxX = Math.max(maxX, x);
					maxY = Math.max(maxY, y);
					containsData = true;
				}
			}
		}
		if (!containsData)
		{
			return EMPTY_MATRIX;
		}

		int w = maxX - minX + 1;
		int h = maxY - minY + 1;

		int[][] ret = new int[h][w];

		for (int x = 0; x < w; ++x)
		{
			for (int y = 0; y < h; ++y)
			{
				ret[y][x] = matrix[y + minY][x + minX];
			}
		}

		return ret;
	}

	/**
	 * Linear interpolation.
	 */
	public static double lerp(double x, double x1, double x2, double q00, double q01)
	{
		return ((x2 - x) / (x2 - x1)) * q00 + ((x - x1) / (x2 - x1)) * q01;
	}

	/**
	 * Bilinear interpolation.
	 */
	public static double biLerp(double x, double y, double q11, double q12, double q21, double q22, double x1, double x2, double y1, double y2)
	{
		double r1 = lerp(x, x1, x2, q11, q21);
		double r2 = lerp(x, x1, x2, q12, q22);
		return lerp(y, y1, y2, r1, r2);
	}

	/**
	 * Trilinear interpolation.
	 */
	public static double triLerp(double x, double y, double z, double q000, double q001, double q010, double q011, double q100, double q101, double q110, double q111, double x1,
			double x2, double y1, double y2, double z1, double z2)
	{
		double x00 = lerp(x, x1, x2, q000, q100);
		double x10 = lerp(x, x1, x2, q010, q110);
		double x01 = lerp(x, x1, x2, q001, q101);
		double x11 = lerp(x, x1, x2, q011, q111);
		double r0 = lerp(y, y1, y2, x00, x01);
		double r1 = lerp(y, y1, y2, x10, x11);
		return lerp(z, z1, z2, r0, r1);
	}
	
	/**
	 * Linear interpolation.
	 */
	public static float lerp(float x, float x1, float x2, float q00, float q01)
	{
		return ((x2 - x) / (x2 - x1)) * q00 + ((x - x1) / (x2 - x1)) * q01;
	}

	/**
	 * Bilinear interpolation.
	 */
	public static float biLerp(float x, float y, float q11, float q12, float q21, float q22, float x1, float x2, float y1, float y2)
	{
		float r1 = lerp(x, x1, x2, q11, q21);
		float r2 = lerp(x, x1, x2, q12, q22);
		return lerp(y, y1, y2, r1, r2);
	}

	/**
	 * Trilinear interpolation.
	 */
	public static float triLerp(float x, float y, float z, float q000, float q001, float q010, float q011, float q100, float q101, float q110, float q111, float x1,
			float x2, float y1, float y2, float z1, float z2)
	{
		float x00 = lerp(x, x1, x2, q000, q100);
		float x10 = lerp(x, x1, x2, q010, q110);
		float x01 = lerp(x, x1, x2, q001, q101);
		float x11 = lerp(x, x1, x2, q011, q111);
		float r0 = lerp(y, y1, y2, x00, x01);
		float r1 = lerp(y, y1, y2, x10, x11);
		return lerp(z, z1, z2, r0, r1);
	}

	/**
	 * Maps a 2D value, made by two integers, to a unique 1D value.
	 * 
	 * @param k1
	 * @param k2
	 * @return the unique 1D value.
	 */
	public static int cantorize(int k1, int k2)
	{
		return ((k1 + k2) * (k1 + k2 + 1) / 2) + k2;
	}
	
	public static int cantorize3(int k1, int k2, int k3)
	{
		return cantorize(cantorize(k1, k2), k3);
	}
	
	public static void decantorize3(int c, Vec3i output)
	{
		int j = (int) (Math.sqrt(0.25 + 2 * c) - 0.5);
		int z = c - j * (j + 1) / 2;
		int xandy = j - z;
		j = (int) (Math.sqrt(0.25 + 2 * c) - 0.5);
		int y = xandy - j * (j + 1) / 2;
		int x = j - y;
		output.set(x, y, z);
	}
	
	public static int mapToPositiveAndCantorize3(int x, int y, int z)
	{
		return MathHelper.cantorize3(MathHelper.mapToPositive(x), MathHelper.mapToPositive(y), MathHelper.mapToPositive(z));
	}
	
	public static void decatorize(int c, Vec2i output)
	{
		int j = (int) (Math.sqrt(0.25 + 2 * c) - 0.5);
		int y = c - j * (j + 1) / 2;
		int x = j - y;
		output.set(x, y);
	}

	public static int cantorX(int c)
	{
		int j = (int) (Math.sqrt(0.25 + 2 * c) - 0.5);
		return j - (c - j * (j + 1) / 2);
	}

	public static int cantorY(int c)
	{
		int j = (int) (Math.sqrt(0.25 + 2 * c) - 0.5);
		return c - j * (j + 1) / 2;
	}

	/**
	 * Maps any given value to be positive only.
	 */
	public static int mapToPositive(int x)
	{
		if (x >= 0)
			return x << 1;

		return -(x << 1) - 1;
	}

	/**
	 * Recreates the original value after applying {@code mapToPositive}.
	 */
	public static int redoMapToPositive(int x)
	{
		if ((x & 1) == 0)
		{
			return x >> 1;
		}

		return -(x >> 1) - 1;
	}

	public static float roundDelta(float x, float delta)
	{
		float rounded = Math.round(x);
		float diff = Math.abs(x - rounded);
		if (diff < delta)
		{
			return rounded;
		}
		return x;
	}

	public static int bytesToMagaBytes(long bytes)
	{
		return (int) (((float) bytes) / 1024.0f / 1024.0f);
	}

	public static float calcFOVX(float fovy, float w, float h)
	{
		return fovy / w * h;
	}

	public static int cardinality(byte b)
	{
		int count = 0;
		for (int i = 0; i < 8; ++i)
		{
			if (((b >>> i) & 1) == 1)
			{
				++count;
			}
		}
		return count;
	}
	

	public static int cardinality(int i, int max)
	{
		int count = 0;
		for (int j = 0; j < max; ++j)
		{
			if (((i >>> j) & 1) == 1)
			{
				++count;
			}
		}
		return count;
	}

	public static float simplify(float val, float interval)
	{
		while (val >= interval)
		{
			val -= interval;
		}
		
		while (val < 0)
		{
			val += interval;
		}
		
		return val;
	}
	
	public static int simplify(int val, int interval)
	{
		while (val >= interval)
		{
			val -= interval;
		}
		
		while (val < 0)
		{
			val += interval;
		}
		
		return val;
	}
	
	public static byte setBit(byte b, int bit, boolean value)
	{
		if (value)
		{
			return (byte) (b | (1 << bit));
		} else
		{
			return (byte) (b & ~(1 << bit));
		}
	}

	public static float sqrt(float f)
	{
		return (float) Math.sqrt(f);
	}

	public static float atan2(float y, float x)
	{
		return (float) Math.atan2(y, x);
	}

	/**
	 * Converts an angle given in radians to an angle in degrees.
	 */
	public static float toDegrees(float radians)
	{
		return radians * 180.0f / f_PI;
	}
	
	/**
	 * Converts an angle given in degrees to an angle in radians.
	 */
	public static float toRadians(float degrees)
	{
		return degrees / 180.0f * f_PI;
	}

	public static float smoothCurve(float x)
	{
		return -x * x * (2 * x - 3);
	}




}
