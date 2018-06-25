package com.tiny.game.common.domain.alliance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tiny.game.common.GameConst;

public class AllianceEvent {

	private String allianceId;
	private String eventId;
	private int allianceEventType;
	private Date time;
	private Map<Integer, String> parameters = new HashMap<Integer, String>();
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof AllianceEvent)) {
			return false;
		}
		
		return allianceId.equals(((AllianceEvent) o).allianceId) && eventId.equals(((AllianceEvent) o).eventId);
	}
	
	public String getBelongToRoleId() {
		return parameters.get(GameConst.ALLIANCE_PARA_ACTION_ROLE_ID);
	}
	
	public String getAllianceId() {
		return allianceId;
	}
	public void setAllianceId(String allianceId) {
		this.allianceId = allianceId;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public int getAllianceEventType() {
		return allianceEventType;
	}
	public void setAllianceEventType(int allianceEventType) {
		this.allianceEventType = allianceEventType;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Map<Integer, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<Integer, String> parameters) {
		this.parameters = parameters;
	}
	
	public void setParameter(int key, String value) {
		parameters.put(key, value);
	}
	
	public String getParameter(int key) {
		return parameters.get(key);
	}
	
}
