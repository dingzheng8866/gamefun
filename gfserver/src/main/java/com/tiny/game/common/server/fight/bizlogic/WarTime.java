package com.tiny.game.common.server.fight.bizlogic;

public class WarTime {
	private long passedTime;
	private float deltaTime;
	private long systemTime; // do not use this one
	private int frameCount;
	private long beginTime;
	
	private long timeMax = 120;

	private float timeScale = 1f;
	
	public WarTime(long timeMax) {
		systemTime = System.currentTimeMillis();
		beginTime = systemTime;
		deltaTime = 0;
		passedTime = 0;
		frameCount = 0;
		this.timeMax = timeMax;
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
		
		passedTime+=deltaTime*1000f;
		
		frameCount += 1* timeScale;
	}

	public long getWarPassedTime() {
		return passedTime;
	}

//	public long getSystemTime() {
//		return systemTime;
//	}
	
	public float getDeltaTime() {
		return deltaTime;
	}
	
	public int getTimeleft() {
		int timeLeft = (int) (timeMax - getWarPassedTime() / 1000);
		if (timeLeft <= 0) {
			timeLeft = 0;
		}
		return timeLeft;
	}
	
}
