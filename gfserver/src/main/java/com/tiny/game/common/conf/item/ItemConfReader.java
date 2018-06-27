package com.tiny.game.common.conf.item;

import org.apache.commons.lang3.StringUtils;

import com.tiny.game.common.conf.ConfAnnotation;
import com.tiny.game.common.conf.ConfReader;
import com.tiny.game.common.domain.item.Item;
import com.tiny.game.common.domain.item.ItemCategory;
import com.tiny.game.common.domain.item.ItemId;


@ConfAnnotation(confClass = ItemConfReader.class, path = "resources/config/item.csv")
public class ItemConfReader extends ConfReader<Item> {
	
	@Override
	protected void parseCsv(String[] csv) {
		Item bean = new Item();
		bean.setItemId(ItemId.valueOf(Integer.parseInt(getSafeValue(csv, "id"))));
//		System.out.println(bean.getId());
		bean.setName(getSafeValue(csv, "name"));
		bean.setAvatarId(getSafeValue(csv, "avatarId"));
		
		String maxValue = getSafeValue(csv, "maxValue");
		if(StringUtils.isNotEmpty(maxValue)){
			bean.setMaxValue(Long.parseLong(maxValue));
		}
		
		String accumulativeFlag = getSafeValue(csv, "accumulativeFlag");
		if(StringUtils.isNotEmpty(accumulativeFlag)){
			bean.setAccumulative("0".equals(accumulativeFlag) ? false : true);
		}
		
		String categoryStr = getSafeValue(csv, "category");
		if(StringUtils.isNotEmpty(categoryStr)){
			bean.setCategory(ItemCategory.valueOf(Integer.parseInt(categoryStr)));
		}
		
		String str = getSafeValue(csv, "isVisableByOtherRole");
		if(StringUtils.isNotEmpty(str)){
			bean.setVisableToOtherToShow("1".equals(str));
		}
		
		addConfBean(bean.getKey(), bean);
	}
	
}
