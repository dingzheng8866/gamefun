package com.tiny.game.common.conf.item;

import java.util.HashMap;
import java.util.Map;

import com.tiny.game.common.conf.ConfAnnotation;
import com.tiny.game.common.conf.ConfReader;
import com.tiny.game.common.domain.item.ItemBean;


@ConfAnnotation(confClass = ItemBeanConfReader.class, path = "resources/config/item.csv")
public class ItemBeanConfReader extends ConfReader<ItemBean> {
	
	@Override
	protected void parseCsv(String[] csv) {
		ItemBean bean = new ItemBean();
		
		bean.setId(Integer.parseInt(getSafeValue(csv, "id")));
		bean.setLevel(Integer.parseInt(getSafeValue(csv, "level")));
		
		addConfBean(bean.getId()+"", bean);
	}
	
}
