package com.tiny.game.common.domain.item;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.exception.InternalBugException;

public class Item {

	private static Logger logger = LoggerFactory.getLogger(Item.class);
	
	protected ItemId itemId;
	protected String name;
	protected String avatarId;
	
	protected long maxValue = -1;
	
	protected boolean isAccumulative = true;
	
	protected Map<String, String> props = new HashMap<String, String>();

	public float getAttrFloatValue(String attrKey) {
		return getAttrFloatValue(props, attrKey);
	}
	
	public static float getAttrFloatValue(Map<String, String> props, String attrKey) {
		float v = 0;
		String str = props.get(attrKey);
		if(str!=null &&str.trim().length() >0) {
			try {
				v = Float.parseFloat(str.trim());
			}catch(Exception e) {
				logger.error("Falied to getAttrValue: "+e.getMessage(), e);
				throw new InternalBugException("Falied to getAttrValue: "+e.getMessage(), e);
			}
		}
		return v;
	}
	
	
	public String getKey(){
		return getKey(itemId);
	}
	
	public static String getKey(ItemId itemId){
		return itemId.getValue()+"";
	}
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Item)) {
			return false;
		}
		
		return getKey().equals(((Item) o).getKey());
	}
	
	public ItemId getItemId() {
		return itemId;
	}

	public void setItemId(ItemId id) {
		this.itemId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}

	public void addAttr(String key, String value){
		if(props.containsKey(key)){
			logger.warn(itemId.name()+" add duplicate attr " + key +", " + getAttr(key) +"-->"+value);
		}
		props.put(key, value);
	}
	
	public String getAttr(String key){
		return props.get(key);
	}
	
	public Map<String, String> getProps() {
		return props;
	}

	public void setProps(Map<String, String> props) {
		this.props = props;
	}

	public long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}

	public boolean isAccumulative() {
		return isAccumulative;
	}

	public void setAccumulative(boolean isAccumulative) {
		this.isAccumulative = isAccumulative;
	}

	protected String attrsToString(){
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<String, String> entry : props.entrySet()){
			sb.append(","+entry.getKey()+"=");
			sb.append(entry.getValue());
		}
		return sb.toString();
	}
	
	public String toString(){
		return itemId.name()+",avatarId:"+avatarId+",props:"+attrsToString();
	}
	
}
