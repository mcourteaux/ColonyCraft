package com.colonycraft.rendering.camera;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.colonycraft.math.Vec3f;

public class FirstPersonCamera extends Camera
{
	
	private float near, far;
	private float fovy;
	
	private Vec3f pos;
	private Vec3f dir;
	
	public FirstPersonCamera()
	{
		near = 0.2f;
		far = 500.0f;
		fovy = 50.0f;
		
		pos = new Vec3f();
		dir = new Vec3f();
	}
	
	public void lookTrough()
	{
		float aspect = (float) Display.getWidth() / Display.getHeight();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(fovy, aspect, near, far);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GLU.gluLookAt(pos.x, pos.y, pos.z, pos.x + dir.x, pos.y + dir.y, pos.z + dir.z, 0, 1, 0);
		
		frustum.updateFrustum();
	}
	
	public void setPos(float x, float y, float z)
	{
		this.pos.set(x, y, z);
	}
	
	public void setDir(float x, float y, float z)
	{
		this.dir.set(x, y, z);
	}
	
	public Vec3f getDir()
	{
		return dir;
	}
	
	public Vec3f getPos()
	{
		return pos;
	}

	public void setNearAndFar(float near, float far)
	{
		this.near = near;
		this.far = far;
	}
}
