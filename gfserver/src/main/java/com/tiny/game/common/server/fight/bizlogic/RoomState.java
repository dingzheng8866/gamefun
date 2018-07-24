package com.tiny.game.common.server.fight.bizlogic;

public enum RoomState {
	Waiting(0),

	Gameing(1),
	
	Over(2);
	
	public int value;
	
	RoomState(int val)
	{
		this.value = val;
	}
	
	
	public static RoomState valueOf(int val)
	{
		switch(val)
		{
			case 0:
				return RoomState.Waiting;
			case 1:
				return RoomState.Gameing;
			case 2:
				return RoomState.Over;
		}
		
		return RoomState.Waiting;
	}
}
