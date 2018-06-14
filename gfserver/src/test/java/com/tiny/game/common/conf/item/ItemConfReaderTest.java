package com.tiny.game.common.conf.item;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.domain.item.Item;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.item.LevelItem;

public class ItemConfReaderTest {

	@BeforeClass
	public static void setUp() throws Exception {
		LocalConfManager.getInstance().load();
	}
	
	@Test
	public void testItem(){
		Item bean = (Item)LocalConfManager.getInstance().getConfReader(ItemConfReader.class).getConfBean(Item.getKey(ItemId.roleLevel));
		assertNotNull(bean);
		
		assertTrue(ItemLevelAttrConfReader.getMaxLevel(ItemId.mainBase) >1);
	}
	
	@Test
	public void testItemAttr(){
		LevelItem bean = (LevelItem)LocalConfManager.getInstance().getConfReader(ItemLevelAttrConfReader.class).getConfBean(LevelItem.getKey(ItemId.mainBase, 1));
		assertNotNull(bean);
	}
	
}
