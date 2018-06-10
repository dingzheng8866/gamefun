package com.tiny.game.common.domain.role;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.server.main.bizlogic.role.RoleUtil;
import com.tiny.game.common.util.NetMessageUtil;

public class TestRole {

	@BeforeClass
	public static void setUp() throws Exception {
		LocalConfManager.getInstance().load();
	}
	
	private Role buildRole(){
		Role role = new Role();
		role.setRoleId("123456");
		role.setLastUpdateTime(Calendar.getInstance().getTime());
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleLevel, 0, 5));
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 0, 125));
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.mainBase, 3, 1));
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.defenseTowerLeft, 2, 1));
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.defenseTowerRight, 1, 1));
		return role;
	}
	
	@Test
	public void testRoleBin(){
		Role role = buildRole();
		System.out.println(role.toString());
		role = Role.toRole(role.toBinData());
		System.out.println(role.toString());
		System.out.println(NetMessageUtil.convertRole(role));
	}
	
}
