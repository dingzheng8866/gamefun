package com.tiny.game.common.server.match.bizlogic;


public class MatchRequestCategory {

	private int matchType;
	private int vsX;
	private int vsY;
	private int stageId;
	
	public MatchRequestCategory(int matchType, int vsX, int vsY, int stageId){
		this.matchType = matchType;
		this.vsX = vsX;
		this.vsY = vsY;
		this.stageId = stageId;
	}
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof MatchRequestCategory)) {
			return false;
		}
		
		return matchType == ((MatchRequestCategory)o).matchType && vsX == ((MatchRequestCategory)o).vsX
				&& vsY == ((MatchRequestCategory)o).vsY && stageId == ((MatchRequestCategory)o).stageId;
	}
	
	public int getMatchType() {
		return matchType;
	}
	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}
	public int getVsX() {
		return vsX;
	}
	public void setVsX(int vsX) {
		this.vsX = vsX;
	}
	public int getVsY() {
		return vsY;
	}
	public void setVsY(int vsY) {
		this.vsY = vsY;
	}
	public int getStageId() {
		return stageId;
	}
	public void setStageId(int stageId) {
		this.stageId = stageId;
	}
	
	
	
}
