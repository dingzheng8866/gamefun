package com.tiny.game.common.server.main.bizlogic.gm;

import java.security.InvalidParameterException;

import com.tiny.game.common.domain.role.Role;

public class GmCmdFactory {

	public static GmCmd factoryGmCmd(String roleId, String cmd, String parameter){
		Role role = null; // TODO: RoleDao
		GmCmd gmCmd = null;
		// TODO: use auto register cmd
		if("addItem".equalsIgnoreCase(cmd)){
			gmCmd = new GmCmdAddItem(role, cmd, parameter);
		} else {
			throw new InvalidParameterException("Unknown gm cmd: " + cmd);
		}
		return gmCmd;
	}
	
}
