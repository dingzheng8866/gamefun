package com.tiny.game.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class GameUtil {

	private static Random random = new Random();
	
	public static List<String> splitToStringList(String str, String sep){
		List<String> list = new ArrayList<String>();
		if(str!=null && str.trim().length() >0) {
			String[] strArray = str.trim().split(sep);
			for(String s : strArray) {
				if(s!=null && s.trim().length() >0) {
					list.add(s);
				}
			}
		}
		return list;
	}
	
	public static float randomRange(float min, float max) {
		return (float)(min+Math.random()*(max - min));
	}
	
	public static int randomRange(int min, int max) {
		return (int)(min+Math.random()*max);
	}
	
	public static String readFileAllContent(String fileName) {  
        String encoding = "UTF-8";  
        File file = new File(fileName);  
        Long filelength = file.length();  
        byte[] filecontent = new byte[filelength.intValue()];  
        FileInputStream in =null;
        try {  
            in = new FileInputStream(file);  
            in.read(filecontent);  
            in.close();  
            return new String(filecontent, encoding);  
        } catch (Exception e) {  
            throw new RuntimeException("Failed to read file content: " + fileName+", error: "+e.getMessage(), e);
        } finally {
        	if(in!=null){
        		try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    } 
	
	public static void writeFile(String content, String file){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(file));
			pw.println(content);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to write to file: " + file +", error: " + e.getMessage(), e);
		}
	}
	
	public static String toString(Map map){
		StringBuffer sb = new StringBuffer();
		for(Object key : map.keySet()){
			Object value = map.get(key);
			sb.append(key+"="+value+",");
		}
		return sb.toString();
	}
	
}
