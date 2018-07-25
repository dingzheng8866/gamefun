package com.tiny.game.common.server.fight.domain.prop;

public class PropAttacher {

	private static int idSeq = 1;
	private int id;
	private String propId;
	
	private float addValue=0;
	private int addPercentageValue = 0;
	
	public PropAttacher(String propId, float addValue) {
		this.propId = propId;
		this.addValue = addValue;
		initId();
		id = idSeq;
	}

	public PropAttacher(String propId, int addPercentageValue) {
		this.propId = propId;
		this.addPercentageValue = addPercentageValue;
		initId();
		id = idSeq;
	}

	public boolean equals(Object o) {
		if(o==null || !(o instanceof PropAttacher)) {
			return false;
		}
		
		return id==(((PropAttacher)o).id);
	}
	
	private synchronized static void initId() {
		idSeq++;
		if(idSeq >= Integer.MAX_VALUE) {
			idSeq=1;
		}
	}
	
	public int getId() {
		return id;
	}

	public String getPropId() {
		return propId;
	}

	public float getAddValue() {
		return addValue;
	}

	public int getAddPercentageValue() {
		return addPercentageValue;
	}

	
}
