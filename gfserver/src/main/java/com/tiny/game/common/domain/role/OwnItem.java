package com.tiny.game.common.domain.role;

import java.util.HashMap;
import java.util.Map;

import com.tiny.game.common.domain.item.Item;
import com.tiny.game.common.util.GameUtil;

public class OwnItem {

	private Item item;
	private int value;
	
	private Map<String, String> extendedProps = new HashMap<String, String>();

	public String getKey(){
		return item.getKey();
	}
	
	public int getLevel(){
		return item.getLevel();
	}
	
	public float getAttrFloatValue(String attrKey) {
		return item.getAttrFloatValue(attrKey) + Item.getAttrFloatValue(extendedProps, attrKey);
	}
	
	public void setExtendAttrValue(String attrKey, String value) {
		extendedProps.put(attrKey, value);
	}
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof OwnItem)) {
			return false;
		}
		
		return item.equals(((OwnItem)o).item);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(item.toString()+",own:"+value);
		if(extendedProps.size() > 0){
			sb.append(",extendProps:"+GameUtil.toString(extendedProps));
		}
		return sb.toString();
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

	public boolean hasAttr(String key){
		boolean found =  getExtendProp(key)!=null;
		if(!found){
			found = item.getAttr(key)!=null;
		}
		return found;
	}
	
	public String getExtendProp(String key){
		return extendedProps.get(key);
	}
	
	public void addExtendProp(String key, String value){
		extendedProps.put(key, value);
	}
	
	public Map<String, String> getExtendedProps() {
		return extendedProps;
	}

	public void setExtendedProps(Map<String, String> extendedProps) {
		this.extendedProps = extendedProps;
	}
	
}
