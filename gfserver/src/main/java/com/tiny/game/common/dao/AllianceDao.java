package com.tiny.game.common.dao;

import java.util.List;

import com.tiny.game.common.domain.alliance.Alliance;
import com.tiny.game.common.domain.alliance.AllianceEvent;
import com.tiny.game.common.domain.alliance.AllianceMember;

public interface AllianceDao {

	public void createAlliance(Alliance alliance);
	public void updateAlliance(Alliance alliance);
	public void removeAlliance(String allianceId);
	public Alliance getAllianceById(String allianceId);
	public List<Alliance> getAlliances(String allianceName);
	public List<Alliance> getAlliances(int maxMembers, int location, int prize, int limitCount);
	public List<Alliance> getRecommendAlliancesByRoleLeaguePrize(int prize);
	
	public void createAllianceMember(AllianceMember allianceMember);
	public void updateAllianceMember(AllianceMember allianceMember);
	public void removeAllianceMember(String roleId);
	public List<AllianceMember> getAllianceMembers(String allianceId);
	public AllianceMember getAllianceMember(String roleId);
	
	public void createAllianceEvent(AllianceEvent ae);
	public void deleteAllianceEvent(String allianceId, String eventId);
	public AllianceEvent getAllianceEvent(String allianceId, String eventId);
	public List<AllianceEvent> getAllianceEvents(String allianceId, int limitCount);
	
}
