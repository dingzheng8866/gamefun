package com.tiny.game.common.conf.item;

import java.util.HashMap;
import java.util.Map;

import com.tiny.game.common.conf.ConfAnnotation;
import com.tiny.game.common.conf.ConfReader;
import com.tiny.game.common.domain.item.Item;
import com.tiny.game.common.domain.item.ItemId;


@ConfAnnotation(confClass = ItemConfReader.class, path = "resources/config/item.csv")
public class ItemConfReader extends ConfReader<Item> {
	
	@Override
	protected void parseCsv(String[] csv) {
		Item bean = new Item();
		bean.setId(ItemId.valueOf(Integer.parseInt(getSafeValue(csv, "id"))));
//		System.out.println(bean.getId());
		addConfBean(bean.getId().getValue()+"", bean);
	}
	
}
