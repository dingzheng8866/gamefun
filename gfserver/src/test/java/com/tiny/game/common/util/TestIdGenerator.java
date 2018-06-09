package com.tiny.game.common.util;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestIdGenerator {

	@Test
	public void testGenUniqueId(){
		int idLen = 12;
		int testLoop = 1;
		for(int j=0; j<testLoop; j++){
			Set<String> set = new HashSet<String>();
			for(int i=0; i<1000000; i++){
				String id = IdGenerator.genUniqueId(idLen);
//				System.out.println(id);
				assertTrue(id.length() == idLen);
				assertFalse(set.contains(id));
				set.add(id);
			}
			System.out.println("Test gen unique id size: " + set.size());
		}
	}
	
}
