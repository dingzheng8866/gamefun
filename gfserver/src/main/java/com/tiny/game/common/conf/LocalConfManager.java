package com.tiny.game.common.conf;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.exception.InternalBugException;


public class LocalConfManager {
	private static final Logger logger = LoggerFactory.getLogger(LocalConfManager.class);
	
	protected Map<String, ConfReader> confReaders = new ConcurrentHashMap<String, ConfReader>();
	
	private static class SingletonHolder {
		private static LocalConfManager instance = new LocalConfManager();
	}

	public static LocalConfManager getInstance() {
		return SingletonHolder.instance;
	}
	
	private LocalConfManager() {
		initConfReaders(null);
	}

	public void load() {
		for(ConfReader reader : confReaders.values()) {
			reader.load();
		}
	}
	
	public void reload() {
		for(ConfReader reader : confReaders.values()) {
			reader.reLoad();
		}
	}
	
	public ConfReader getConfReader(Class c) {
		return confReaders.get(c.getSimpleName());
	}
	
	private void initConfReaders(String path) {
		if(path==null || path.trim().length() < 1) {
			path = LocalConfManager.class.getPackage().getName();
			System.out.println(path);
		}
		Reflections reflections = new Reflections(path);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(ConfAnnotation.class);
		for (Class<?> processorClass : annotated) {
			ConfAnnotation ann = processorClass.getAnnotation(ConfAnnotation.class);
			Object confReaderObj;
			try {
				confReaderObj = processorClass.newInstance();
			} catch (Exception e) {
				throw new InternalBugException("Failed to create conf reader object: "+e.getMessage(),e);
			}
			Class readerClass = ann.confClass();
			
			if (confReaders.containsKey(readerClass.getSimpleName())) {
				throw new InternalBugException("Duplicate conf reader: "+readerClass.getName());
			}
			if(!(confReaderObj instanceof ConfReader)) {
				throw new InternalBugException("Invalid conf reader type: "+readerClass.getName());
			}
			String confPath = ann.path();
			if(confPath==null || confPath.trim().length() < 1) {
				throw new InternalBugException("Invalid empty conf reader path: "+readerClass.getName());
			}
			confPath = confPath.trim();
			
			ConfReader reader = (ConfReader)confReaderObj;
			reader.setConfigPath(confPath);
			confReaders.put(readerClass.getSimpleName(), reader);
			
			logger.info("Init conf reader: " + readerClass.getName() +", path: " + confPath);
		}
	}
	
}
