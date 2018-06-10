package com.tiny.game.common.domain.role;

public class RoleExp {
	
	private int level;
	private int exp;
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public String getKey(){
		return level+"";
	}
	
	public String toString(){
		return "RoleExp level:" + level+", exp:" + exp;
	}
	
}
