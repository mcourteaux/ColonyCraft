package com.colonycraft.rendering.camera;

public abstract class Camera
{

	protected ViewFrustum frustum;
	
	public Camera()
	{
		frustum = new ViewFrustum();
	}
	
	public abstract void lookTrough();
	
	public ViewFrustum getFrustum()
	{
		return frustum;
	}
}
