package com.tiny.game.common.dao.impl;

import java.util.List;

import com.tiny.game.common.dao.AllianceDao;
import com.tiny.game.common.dao.nosql.cassandra.AllianceDaoImplCassandra;
import com.tiny.game.common.domain.alliance.Alliance;
import com.tiny.game.common.domain.alliance.AllianceMember;

public class AllianceDaoImpl implements AllianceDao{

	@Override
	public void createAlliance(Alliance alliance) {
		AllianceDaoImplCassandra.getInstance().createAlliance(alliance);
	}

	@Override
	public void updateAlliance(Alliance alliance) {
		AllianceDaoImplCassandra.getInstance().updateAlliance(alliance);		
	}

	@Override
	public void removeAlliance(String allianceId) {
		AllianceDaoImplCassandra.getInstance().removeAlliance(allianceId);
	}

	@Override
	public Alliance getAllianceById(String allianceId) {
		return AllianceDaoImplCassandra.getInstance().getAllianceById(allianceId);
	}

	@Override
	public List<Alliance> getAlliances(String allianceName) {
		return AllianceDaoImplCassandra.getInstance().getAlliances(allianceName);
	}

	@Override
	public List<Alliance> getAlliances(int maxMembers, int location, int prize, int limitCount) {
		return AllianceDaoImplCassandra.getInstance().getAlliances(maxMembers, location, prize, limitCount);
	}

	@Override
	public void createAllianceMember(AllianceMember allianceMember) {
		AllianceDaoImplCassandra.getInstance().createAllianceMember(allianceMember);
	}

	@Override
	public void updateAllianceMember(AllianceMember allianceMember) {
		AllianceDaoImplCassandra.getInstance().updateAllianceMember(allianceMember);
	}

	@Override
	public void removeAllianceMember(String roleId) {
		AllianceDaoImplCassandra.getInstance().removeAlliance(roleId);
	}

	@Override
	public List<AllianceMember> getAllianceMembers(String allianceId) {
		return AllianceDaoImplCassandra.getInstance().getAllianceMembers(allianceId);
	}

	@Override
	public AllianceMember getAllianceMember(String roleId) {
		return AllianceDaoImplCassandra.getInstance().getAllianceMember(roleId);
	}

}
