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
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.roleLevel, 1, 1));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 1, 0));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.mainBase, 2, 1));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.defenseTowerLeft, 1, 1));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.defenseTowerRight, 1, 1));
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
		int delta = roleExp.getExp() - role.getOwnItemValue(ItemId.roleExp) - 1;
		
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 1, delta));
		assertTrue(role.getLevel() == level);
		
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 1, 10));
		assertTrue(role.getLevel() == level+1);
		
		level = role.getLevel();
		int total = 0;
		int addedLevel = 3;
		for(int i=0; i<addedLevel; i++){
			roleExp = (RoleExp)LocalConfManager.getInstance().getConfReader(RoleExpConfReader.class).getConfBean((level+i)+"");
			total+=roleExp.getExp();
		}
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 1, total));
		assertTrue(role.getLevel() == level+addedLevel);
		
	}
	
}
