package com.tiny.game.common.domain.item;

import java.util.HashMap;
import java.util.Map;

public class Item {

	private int id;
	private String typeStringValue;
	
	private String name;
	private ItemType type;
	private int avatarId;
	
	private Map<String, String> props = new HashMap<String, String>();

	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Item)) {
			return false;
		}
		
		return id == ((Item) o).id;
	}
	
	public void setTypeStringValue(String v){
		this.typeStringValue = v;
	}
	
	public String getTypeStringValue(){
		return this.typeStringValue;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}

//	public int getLevel() {
//		return level;
//	}

	public Map<String, String> getProps() {
		return props;
	}

	public void setProps(Map<String, String> props) {
		this.props = props;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}
	
}
