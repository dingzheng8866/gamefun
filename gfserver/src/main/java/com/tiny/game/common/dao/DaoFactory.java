package com.tiny.game.common.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.dao.impl.AllianceDaoImpl;
import com.tiny.game.common.dao.impl.UserDaoImpl;
import com.tiny.game.common.exception.InternalBugException;

public class DaoFactory {

	private static class SingletonHolder {
		private static DaoFactory instance = new DaoFactory();
	}

	public static DaoFactory getInstance() {
		return SingletonHolder.instance;
	}
	
	private Map<String, Object> daoMap = new ConcurrentHashMap<String, Object>();
	
	private Object getDao(Class<?> daoClass) {
		String daoTag = daoClass.getSimpleName();
		Object obj = daoMap.get(daoTag);
		if(obj==null) {
			try {
				obj = daoClass.newInstance();
			} catch (Exception e) {
				throw new InternalBugException("Failed to create dao instance: " + daoTag);
			}
			daoMap.put(daoTag, obj);
		}
		return obj;
	}
	
	public UserDao getUserDao() {
		return (UserDao)getDao(UserDaoImpl.class);
	}
	
	public AllianceDao getAllianceDao() {
		return (AllianceDao)getDao(AllianceDaoImpl.class);
	}
	
}
