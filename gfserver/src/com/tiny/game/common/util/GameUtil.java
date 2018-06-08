package com.tiny.game.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameUtil {

	private static Random random = new Random();
	
	public static List<String> splitToStringList(String str, String sep){
		List<String> list = new ArrayList<String>();
		if(str!=null && str.trim().length() >0) {
			String[] strArray = str.trim().split(sep);
			for(String s : strArray) {
				if(s!=null && s.trim().length() >0) {
					list.add(s);
				}
			}
		}
		return list;
	}
	
	public static float randomRange(float min, float max) {
		return (float)(min+Math.random()*(max - min));
	}
	
	public static int randomRange(int min, int max) {
		return (int)(min+Math.random()*max);
	}
	
}
