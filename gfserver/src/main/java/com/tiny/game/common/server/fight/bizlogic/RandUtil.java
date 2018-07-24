package com.tiny.game.common.server.fight.bizlogic;

import java.util.List;

public class RandUtil {
	public static WarRandom random = new WarRandom();
	
	public synchronized static long nextLong() {
	     return random.getRandom().nextLong();
	}
	
	public synchronized static int nextInt(int n) {
		if(n <= 0) {
			return 0;
		}
		
		return random.getRandom().nextInt(n);
	}
	
	public synchronized static int range(int min, int max) {
	     return random.range(min, max);
	}
	
	public synchronized static List randomList(List list) {
        return random.randomList(list);
	}

	public static float range(float min, float max) {
		return random.range(min, max);
	}

}
