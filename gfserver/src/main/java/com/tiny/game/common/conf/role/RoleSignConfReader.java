package com.tiny.game.common.conf.role;

import com.tiny.game.common.conf.ConfAnnotation;
import com.tiny.game.common.conf.ConfReader;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.role.RoleSign;

@ConfAnnotation(confClass = RoleSignConfReader.class, path = "resources/config/sign.csv")
public class RoleSignConfReader extends ConfReader<RoleSign> {
	
	@Override
	protected void parseCsv(String[] csv) {
		RoleSign bean = new RoleSign();
		bean.setDay(Integer.parseInt(getSafeValue(csv, "day")));
		bean.setItemId(ItemId.valueOf(Integer.parseInt(getSafeValue(csv, "itemId"))));
		bean.setItemCount(Integer.parseInt(getSafeValue(csv, "itemCount")));
		addConfBean(bean.getKey(), bean);
	}
	
}
