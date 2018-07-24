package com.tiny.game.common.server.fight.bizlogic;

public class BattleRoomStatus {
	/** 等待： 等待玩家连接战斗服务器 */
	public static final int ROOM_WAITING = 1;
	
	/** 准备： 等待客户端加载安装关卡 */
	public static final	int ROOM_PREPARE = 2;
	
	/** 战斗进行中 */
	public static final int ROOM_INBATTLE = 3;
	
	/** 战斗结束 */
	public static final int	ROOM_BATTLEEND = 4;
}
