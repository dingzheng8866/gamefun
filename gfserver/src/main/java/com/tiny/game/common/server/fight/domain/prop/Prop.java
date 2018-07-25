package com.tiny.game.common.server.fight.domain.prop;

public class Prop {

	private String name;
	private float baseValue;
	
	private float value; // calculate by baseValue
	
	public Prop(String name, float baseValue) {
		this.name = name;
		this.baseValue = baseValue;
		this.value = baseValue;
	}
	
	public float getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}
	
}
