package com.tiny.game.common.server.fight.bizlogic;

public class WarTime {
	public long time;
	public float deltaTime;
	public long systemTime; // do not use this one
	public int frameCount;
	public long beginTime;

	private float timeScale = 1f;
	
	public WarTime() {
		systemTime = System.currentTimeMillis();
		beginTime = systemTime;
		deltaTime = 0;
		time = 0;
		frameCount = 0;
	}

	public void setTimeScale(float scale) {
		this.timeScale = scale;
	}
	
	public void update() {
		long sysCurTime = System.currentTimeMillis();
		deltaTime 	= (sysCurTime - systemTime) / 1000f;
		systemTime = sysCurTime;
		//time = systemTime-beginTime;
		
		deltaTime*=timeScale;
		
		time+=deltaTime*1000f;
		
		frameCount += 1* timeScale;
	}

	public long getWarPassedTime() {
		return time;
	}

//	public long getSystemTime() {
//		return systemTime;
//	}
	
	public float getDeltaTime() {
		return deltaTime;
	}
}
