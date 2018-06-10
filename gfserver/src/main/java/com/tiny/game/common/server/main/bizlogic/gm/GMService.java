package com.tiny.game.common.server.main.bizlogic.gm;

public class GMService {

	public static void doGmCmd(String roleId, String cmd, String parameter){
		GmCmd gmCmd = GmCmdFactory.factoryGmCmd(roleId, cmd, parameter);
		gmCmd.execute();
	}
	
}
