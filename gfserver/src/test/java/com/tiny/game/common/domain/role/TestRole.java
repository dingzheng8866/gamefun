package com.tiny.game.common.domain.role;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.conf.role.RoleExpConfReader;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.server.main.bizlogic.role.RoleUtil;

public class TestRole {

	@BeforeClass
	public static void setUp() throws Exception {
		LocalConfManager.getInstance().load();
	}
	
	private Role buildRole(){
		Role role = new Role();
		role.setRoleId("123456");
		role.setLastUpdateTime(Calendar.getInstance().getTime());
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleLevel, 0, 1));
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 0, 0));
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.mainBase, 3, 1));
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.defenseTowerLeft, 2, 1));
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.defenseTowerRight, 1, 1));
		return role;
	}
	
	@Test
	public void testRoleBin(){
		Role role = buildRole();
//		System.out.println(role.toString());
		role = Role.toRole(role.toBinData());
		assertNotNull(role);
//		System.out.println(NetMessageUtil.convertRole(role));
	}
	
	@Test
	public void testRoleAddExp(){
		Role role = buildRole();
		int level = role.getLevel();
		
		RoleExp roleExp = (RoleExp)LocalConfManager.getInstance().getConfReader(RoleExpConfReader.class).getConfBean(level+"");
		int delta = roleExp.getExp() - role.getRoleOwnItemValue(ItemId.roleExp) - 1;
		
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 1, delta));
		assertTrue(role.getLevel() == level);
		
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 1, 10));
		assertTrue(role.getLevel() == level+1);
		
		level = role.getLevel();
		int total = 0;
		int addedLevel = 3;
		for(int i=0; i<addedLevel; i++){
			roleExp = (RoleExp)LocalConfManager.getInstance().getConfReader(RoleExpConfReader.class).getConfBean((level+i)+"");
			total+=roleExp.getExp();
		}
		role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 1, total));
		assertTrue(role.getLevel() == level+addedLevel);
		
	}
	
}