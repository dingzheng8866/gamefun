package com.tiny.game.common.server.fight.domain;

public class Mathf
{

	public static double deg2Rad = Math.PI / 180;
	public static double rad2Deg = 180 / Math.PI;
	public static double epsilon = 1.401298e-45;
	
	
	public static float clamp(float num, float min, float max)
	{
		if(num < min)
			num = min;
		else if(num > max)
			num = max;
		
		return num;
	}
	
	public static float lerp(float from, float to, float t)
	{
		return from + (to - from) * clamp(t, 0, 1);
	}
	
	
	public static int CeilToInt(float val)
	{
		return (int) Math.ceil(val);
	}

	public static int FloorToInt(float val)
	{
		return (int) Math.floor(val);
	}

	public static float Min(float f, float g) {
		return f <g ? f : g;
	}
	
	
	
}