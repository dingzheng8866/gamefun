package com.tiny.game.common.domain.role;

import java.util.HashMap;
import java.util.Map;

import com.tiny.game.common.domain.item.Item;

public class RoleOwnItem {

	private Item item;
	private int count;
	
	private Map<String, String> extendedProps = new HashMap<String, String>();

	public boolean equals(Object o) {
		if(o==null || !(o instanceof RoleOwnItem)) {
			return false;
		}
		
		return item.equals(((RoleOwnItem)o).item);
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Map<String, String> getExtendedProps() {
		return extendedProps;
	}

	public void setExtendedProps(Map<String, String> extendedProps) {
		this.extendedProps = extendedProps;
	}
	
}
