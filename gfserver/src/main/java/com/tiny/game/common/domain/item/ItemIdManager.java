package com.tiny.game.common.domain.item;

import java.util.HashMap;
import java.util.Map;

import com.tiny.game.common.exception.InternalBugException;

public class ItemIdManager {

	private static Map<Integer, ItemId> allIds = new HashMap<Integer, ItemId>();
	
	public static void setItemId(ItemId itemType){
		allIds.put(itemType.getValue(), itemType);
	}
	
	public static ItemId getItemId(int id){
		if(!allIds.containsKey(id)){
			throw new InternalBugException("Invalid item id: " + id);
		}
		return allIds.get(id);
	}
}
