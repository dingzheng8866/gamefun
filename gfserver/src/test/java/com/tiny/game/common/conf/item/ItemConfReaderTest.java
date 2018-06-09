package com.tiny.game.common.conf.item;

import org.junit.Test;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.domain.item.Item;

public class ItemConfReaderTest {

	@Test
	public void test(){
		LocalConfManager.getInstance().load();
		
		Item item = (Item)LocalConfManager.getInstance().getConfReader(ItemConfReader.class).getConfBean("100001");
		System.out.println(item.getId());
	}
	
}
