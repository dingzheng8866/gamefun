package com.tiny.game.common.conf.item;

import org.apache.commons.lang3.StringUtils;

import com.tiny.game.common.conf.ConfAnnotation;
import com.tiny.game.common.conf.ConfReader;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.item.RoleInitItem;

@ConfAnnotation(confClass = RoleInitItemConfReader.class, path = "resources/config/role_init_item.csv")
public class RoleInitItemConfReader extends ConfReader<RoleInitItem> {
	
	@Override
	protected void parseCsv(String[] csv) {
		RoleInitItem bean = new RoleInitItem();
		bean.setItemId(ItemId.valueOf(Integer.parseInt(getSafeValue(csv, "id"))));
		bean.setValue(Integer.parseInt(getSafeValue(csv, "value")));
		
		String level = getSafeValue(csv, "level");
		if(StringUtils.isNotEmpty(level)){
			bean.setLevel(Integer.parseInt(level));
		}
		
		addConfBean(bean.getKey(), bean);
	}
	
}