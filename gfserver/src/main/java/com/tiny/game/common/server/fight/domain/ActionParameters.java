package com.tiny.game.common.server.fight.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.server.fight.bizlogic.IFight;

import game.protocol.protobuf.FightProtocol.FightObjectParameter;

public class ActionParameters implements Serializable {

	//private static final Logger logger = LoggerFactory.getLogger(ActionParameters.class);
	private int name;
	private Map<Integer, FightObjectParameter> parameters = new HashMap<>();

	public ActionParameters(int name) {
		this.name = name;
	}
	
	public int getName() {
		return name;
	}
	
	public void addParameter(Integer key, FightObjectParameter value) {
		parameters.put(key, value);
	}
	
	private FightObjectParameter parameter(IFight.Para p, IFight.AVPType type) {
		if(IFight.AVP.getAVPType(p) != type) {
			throw new InternalBugException("Invalid int type of avp key: " + p);
		}
		FightObjectParameter fop = parameters.get(p.getValue());
		return fop;
	}

	public int getInt(IFight.Para p) {
		return parameter(p, IFight.AVPType.T_int).getIntValue();
	}
	
	public long getLong(IFight.Para p) {
		return parameter(p, IFight.AVPType.T_long).getLongValue();
	}
	
	public boolean getBoolean(IFight.Para p) {
		return parameter(p, IFight.AVPType.T_boolean).getBooleanValue();
	}
	
	public float getFloat(IFight.Para p) {
		return parameter(p, IFight.AVPType.T_float).getFloatValue();
	}
	
	public String getString(IFight.Para p) {
		return parameter(p, IFight.AVPType.T_string).getStringValue();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ActionParameters: name="+name+",parameters:[ ");
		for(Integer key : parameters.keySet()) {
			sb.append(IFight.Para.valueOf(key)+"="+parameters.get(key)+" ");
		}
		sb.append("]");
		return sb.toString();
	}
	
}
