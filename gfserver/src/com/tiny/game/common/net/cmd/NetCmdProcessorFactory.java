package com.tiny.game.common.net.cmd;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.exception.InternalBugException;

public class NetCmdProcessorFactory {

	private static final Logger logger = LoggerFactory.getLogger(NetCmdProcessorFactory.class);
	
	private static Map<String, NetCmdProcessor> processors = new ConcurrentHashMap<String, NetCmdProcessor>();
	
	private static void addNetCmdProcessor(NetCmdProcessor p) {
		if(processors.containsKey(p.getCmd())) {
			throw new InternalBugException("Duplicated NetCmdProcessor: " + p.getCmd());
		}
		processors.put(p.getCmd(), p);
	}
	
	public static NetCmdProcessor getNetCmdProcessor(String cmd) {
		return processors.get(cmd);
	}
	
	public static void loadNetCmdProcessors(String msgProcessorPath) {
		logger.info("loadNetCmdProcessors =>" + msgProcessorPath);
		Reflections reflections = new Reflections(msgProcessorPath);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(NetCmdAnnimation.class);
		try {
			for (Class<?> processorClass : annotated) {
				NetCmdAnnimation ann = processorClass.getAnnotation(NetCmdAnnimation.class);
				Object msgProcessorObj = processorClass.newInstance();
				if(!(msgProcessorObj instanceof NetCmdProcessor)) {
					throw new InternalBugException("Invalid NetCmdProcessor: " + msgProcessorObj.getClass().getName());
				}
				
				NetCmdProcessor ncp = (NetCmdProcessor) msgProcessorObj;
				ncp.setCmd(ann.cmd().getSimpleName());
				ncp.setEnable(ann.enable());
				
				addNetCmdProcessor(ncp);
			}
		} catch (Exception e) {
			throw new InternalBugException("Failed to loadNetCmdProcessors: " + e.getMessage(), e);
		}
	}
	
}
