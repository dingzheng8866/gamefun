package com.tiny.game.common.domain.role;

import java.util.HashMap;
import java.util.Map;

import com.tiny.game.common.domain.item.Item;

public class OwnItem {

	private Item item;
	private int value;
	
	private Map<String, String> extendedProps = new HashMap<String, String>();

	public String getKey(){
		return item.getKey();
	}
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof OwnItem)) {
			return false;
		}
		
		return item.equals(((OwnItem)o).item);
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Map<String, String> getExtendedProps() {
		return extendedProps;
	}

	public void setExtendedProps(Map<String, String> extendedProps) {
		this.extendedProps = extendedProps;
	}
	
}
