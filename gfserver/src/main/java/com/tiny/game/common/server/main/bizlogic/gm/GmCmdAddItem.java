package com.tiny.game.common.server.main.bizlogic.gm;

import java.security.InvalidParameterException;
import java.util.Map;

import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.item.LevelItem;
import com.tiny.game.common.domain.role.OwnItem;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.server.main.bizlogic.role.RoleUtil;


@GmCmdAnnotation(cmd = "addItem")
public class GmCmdAddItem extends GmCmd {

	public GmCmdAddItem(Role role, String cmd, String parameter) {
		super(role, cmd, parameter);
	}

	@Override
	public void execute() {
		for(Map.Entry<String, String> entry : getParameters().entrySet()){
			String itemKey = entry.getKey();
			int level = 1;
			String value = entry.getValue();
			
			if(itemKey.contains(LevelItem.KEY_SEP)){
				String[] sa = itemKey.split(LevelItem.KEY_SEP);
				if(sa.length !=2){
					throw new InvalidParameterException("gm " + cmd + " parameter is not right: " + itemKey);
				}
				itemKey = sa[0];
				level = Integer.parseInt(sa[1]);
			}
			
			ItemId itemId = ItemId.valueOf(Integer.parseInt(itemKey));
			OwnItem ownItem = RoleUtil.buildOwnItem(itemId, level, Integer.parseInt(value));
			role.addOwnItem(ownItem);
			logger.info("gm add role "+role.getRoleId()+" item: "+itemId + ", value: " + value);
		}
	}

}
