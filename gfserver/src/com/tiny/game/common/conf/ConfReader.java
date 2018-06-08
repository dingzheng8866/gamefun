package com.tiny.game.common.conf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.exception.InternalBugException;


public abstract class ConfReader<T> {
	private static final Logger logger = LoggerFactory.getLogger(ConfReader.class);
	
	protected Map<String, T> configDict = new ConcurrentHashMap<String, T>();
	
	protected boolean ready = false;
	
	protected String configPath = "";
	
	protected ReadWriteLock configLock = new ReentrantReadWriteLock();
	
	protected Map<String, Integer> itemNamesMap = new ConcurrentHashMap<String, Integer>();
	protected Map<Integer, String> itemIndexMap = new ConcurrentHashMap<Integer, String>();
	
	
	public ConfReader() {
	}

	public void load() {
		if(configPath.length() == 0) {
			return;
		}
		
		try (BufferedReader buffReader = new BufferedReader(new FileReader(configPath))) {
			buffReader.readLine();  // ignore Chinese header
			String itemNames =buffReader.readLine();  // ignore English header
			String[] nameArray = itemNames.split(";", -1);
			for(int i = 0; i < nameArray.length; i ++){
				itemNamesMap.put(nameArray[i].trim(), i);
				itemIndexMap.put(i, nameArray[i].trim());
			}
			while(true) {
				String line = buffReader.readLine();
				if(line == null) {
					break;
				}
				
				//System.out.println(line);
				String[] csv = line.split(";", -1);
				if(csv.length !=0 && !csv[0].isEmpty()) {
					parseCsv(csv);
				}
			}
			
			parseComplete();
			buffReader.close();
			ready = true;
		} catch (Exception e) {
//			logger.error("Failed to parse config: "+configPath+", error: "+e.getMessage(), e);
			throw new InternalBugException("Failed to parse config: "+configPath+", error: "+e.getMessage(), e);
		}
	}
		
	public void reLoad() {
		ready = false;
		configLock.writeLock().lock();
		try {
			load ();
		} finally {
			configLock.writeLock().unlock();
		}
		ready = true;
	}
	
	public String getConfigPath() {
		return this.configPath;
	}
	
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	
	public void addConfBean(String key, T bean) {
		configLock.writeLock().lock();
		try {
			configDict.put(key, bean);
		} finally {
			configLock.writeLock().unlock();
		}
	}
	
	public T getConfBean(String key) {
		configLock.readLock().lock();
		T result;

		try {
			result = configDict.get(key);
		} finally {
			configLock.readLock().unlock();
		}
		return result;
	}
	
	public List<T> getAllConfBeans() {
		configLock.readLock().lock();
		List<T> result;

		try {
			result = new ArrayList<T>(configDict.values());
		} finally {
			configLock.readLock().unlock();
		}
		return result;
	}
	
	public String getValue(String[] csv, String key){
		Integer idx = itemNamesMap.get(key);
		try{
			String value = csv[idx];
		}catch(Exception e){
			throw e;
		}
		return csv[idx];
	}
	
	public int getIndex(String key)
	{
		return itemNamesMap.get(key);
	}
	
	public String getColumnKey(int index)
	{
		return itemIndexMap.get(index);
	}
	
	protected abstract void parseCsv(String[] csv);
	
	protected void parseComplete() {}
	
	
	protected String getSafeValue(String[] csv, String key) {
		String v = getValue(csv, key);
		if(v==null || v.trim().isEmpty()) {
			v = "";
		} else {
			v = v.trim();
		}
		return v;
	}
	
	public static List<Integer> toList(String[] args){
		List<Integer> list = new ArrayList<Integer>();
		for(String arg : args) {
			if(arg!=null && arg.trim().length() > 0) {
				list.add(Integer.parseInt(arg.trim()));
			}
		}
		return list;
	}
	
}
