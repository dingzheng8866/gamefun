package com.tiny.game.common.domain.item;

public class LevelItem extends Item {

	public static final String KEY_SEP = "-";
	
	private int level = 1;

	@Override
	public String getKey(){
		return getKey(itemId, level);
	}
	
	public static String getKey(ItemId itemId, int level){
		return Item.getKey(itemId)+KEY_SEP+level;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public String toString(){
		return itemId.name()+"-"+level+","+avatarId+attrsToString();
	}
	
}
