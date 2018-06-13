package com.tiny.game.common.domain.item;

public class LevelItem extends Item {

	@Override
	public String getKey(){
		return getKey(itemId, level);
	}
	
	public static String getKey(ItemId itemId, int level){
		return Item.getKey(itemId, level);
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public String toString(){
		return itemId.name()+"-"+level+",avatarId:"+avatarId+",props:"+attrsToString();
	}
	
}
