package com.colonycraft.utilities;

public class FractalBrownianMotion
{

	private PerlinNoise noise;
	
	private int octaves;
	private float fraction;
	
	public FractalBrownianMotion(PerlinNoise noise)
	{
		this.noise = noise;
		this.octaves = 4;
		this.fraction = 0.5f;
	}
	
	public void setFraction(float fraction)
	{
		this.fraction = fraction;
	}
	
	public void setOctaves(int octaves)
	{
		this.octaves = octaves;
	}
	
	public float noise(float x, float y, float z)
	{
		float result = 0.0f;
		float f = 1.0f;
		float octave = 1.0f;
		for (int i = 0; i < octaves; ++i)
		{
			result += f * noise.noise(x * octave, y * octave, z * octave);
			octave *= 8.0f;
			f *= fraction;
		}
		return result;
	}
	
	public float noise(float x, float y)
	{
		float result = 0.0f;
		float f = 1.0f;
		float octave = 1.0f;
		for (int i = 0; i < octaves; ++i)
		{
			result += f * noise.noise(x * octave, y * octave);
			octave *= 8.0f;
			f *= fraction;
		}
		return result;
	}
	
}
