package com.tiny.game.common.server.fight.bizlogic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FightRoleManager {
	private Map<Integer, FightRole> fightRolesMap = new ConcurrentHashMap<Integer, FightRole>();
	
	private static class SingletonHolder {
		private static FightRoleManager instance = new FightRoleManager();
	}

	public static FightRoleManager getInstance() {
		return SingletonHolder.instance;
	}
	
	public void addFightRole(FightRole frole) {
		fightRolesMap.put(frole.getRoleId(), frole);
	}
	
	public FightRole getFightRole(int roleId) {
		return fightRolesMap.get(roleId);
	}
	
	public void remove(int roleId) {
		fightRolesMap.remove(roleId);
	}
}
