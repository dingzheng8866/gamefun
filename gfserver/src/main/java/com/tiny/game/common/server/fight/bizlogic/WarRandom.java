package com.tiny.game.common.server.fight.bizlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WarRandom {
	// one battle use the same random seed to make it replayable
	private Random random = null;	
	private long seed = 0;
	
	public WarRandom() {
		random = new Random();
	}
	
	// note: use this one
	public WarRandom(long seed) {
		this.seed = seed;
		random = new Random(seed);
	}
	
	public long getSeed() {
		return seed;
	}
	
	public Random getRandom() {
		return random;
	}
	
	public List randomList(List list) {
		List retList = new ArrayList();
        while(list.size() > 0)
        {
            int index = random.nextInt(list.size());
            retList.add(list.get(index));
            list.remove(index);
        }
        return retList;
	}


	public float range(float min, float max)
	{
		return min + random.nextFloat() * (max-min);
	}

	public int range(int min, int max)
	{
		return min + random.nextInt(max-min);
	}
}
