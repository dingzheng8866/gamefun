package com.tiny.game.common.domain.email;

import com.tiny.game.common.domain.alliance.AllianceMember;

public class EmailAttachment {

	private int itemId;
	private int itemLevel = 1;
	private int count;
	
	public EmailAttachment(int itemId, int count) {
		this(itemId, 1, count);
	}
	
	public EmailAttachment(int itemId, int level, int count) {
		this.itemId = itemId;
		this.itemLevel = level;
		this.count = count;
	}
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof AllianceMember)) {
			return false;
		}
		
		return itemId == ((EmailAttachment) o).itemId && itemLevel == ((EmailAttachment) o).itemLevel;
	}
	
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getItemLevel() {
		return itemLevel;
	}
	public void setItemLevel(int itemLevel) {
		this.itemLevel = itemLevel;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public String toString() {
		return "EmailAttachment==> id:" + itemId+", level:" + itemLevel+", count:"+count;
	}
	
}
