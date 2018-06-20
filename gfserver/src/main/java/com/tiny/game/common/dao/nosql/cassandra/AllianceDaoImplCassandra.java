package com.tiny.game.common.dao.nosql.cassandra;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.tiny.game.common.dao.AllianceDao;
import com.tiny.game.common.domain.alliance.Alliance;
import com.tiny.game.common.domain.alliance.AllianceJoinInType;
import com.tiny.game.common.domain.alliance.AllianceMember;
import com.tiny.game.common.domain.alliance.AllianceMemberTitle;

public class AllianceDaoImplCassandra implements AllianceDao {

	private static final Logger logger = LoggerFactory.getLogger(AllianceDaoImplCassandra.class);
	
	private Session session = CassandraManager.getInstance().getSession();
	
	private static class SingletonHolder {
		private static AllianceDaoImplCassandra instance = new AllianceDaoImplCassandra();
	}

	public static AllianceDaoImplCassandra getInstance() {
		return SingletonHolder.instance;
	}
	
	
	private AllianceRSH allianceRSH = new AllianceRSH();
	private static class AllianceRSH extends CqlAbstractResultSetHandler<Alliance> {
		public Alliance factoryBeanObject(Row rs) {
			Alliance bean = new Alliance();
			bean.setId(rs.getString("id"));
			bean.setName(rs.getString("name"));
			bean.setDescription(rs.getString("description"));
			bean.setLocation(rs.getInt("location"));
			bean.setJoinType(AllianceJoinInType.valueOf(rs.getInt("joinType")));
			bean.setJoinNeedPrize(rs.getInt("joinNeedPrize"));
			bean.setFightRate(rs.getInt("fightRate"));
			bean.setPublicFightLog(rs.getInt("publicFightLog"));
			bean.setLevel(rs.getInt("level"));
			bean.setMaxMemebers(rs.getInt("maxMemebers"));
			bean.setLastUpdateTime(rs.getTimestamp("lastUpdateTime"));
			return bean;
		}
	}
	
	private AllianceMemberRSH allianceMemberRSH = new AllianceMemberRSH();
	private static class AllianceMemberRSH extends CqlAbstractResultSetHandler<AllianceMember> {
		public AllianceMember factoryBeanObject(Row rs) {
			AllianceMember bean = new AllianceMember();
			bean.setAllianceId(rs.getString("allianceId"));
			bean.setRoleId(rs.getString("roleId"));
			bean.setTitle(AllianceMemberTitle.valueOf(rs.getInt("title")));
			bean.setDonated(rs.getInt("donated"));
			bean.setLastUpdateTime(rs.getTimestamp("lastUpdateTime"));
			return bean;
		}
	}
	
	@Override
	public void createAlliance(Alliance alliance) {
		//CREATE TABLE if not exists gamefun.alliance (id text,name text,description text,location int,joinType int,joinNeedPrize int,fightRate int,publicFightLog int,level int,maxMemebers int,lastUpdateTime timestamp,PRIMARY KEY (id));
		String cql = "INSERT INTO gamefun.alliance (id,name,description,location,joinType,joinNeedPrize,fightRate,publicFightLog,level,maxMemebers,lastUpdateTime) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
		session.execute(cql, alliance.getId(),alliance.getName(),alliance.getDescription(),alliance.getLocation(),alliance.getJoinType().getValue(),alliance.getJoinNeedPrize(),alliance.getFightRate(),alliance.getPublicFightLog(), alliance.getLevel(),alliance.getMaxMemebers(),alliance.getLastUpdateTime().getTime());
	}

	@Override
	public void updateAlliance(Alliance alliance) {
		String cql = "UPDATE gamefun.alliance SET description=?,location=?,joinType=?,joinNeedPrize=?,fightRate=?,publicFightLog=?,level=?,maxMemebers=?,lastUpdateTime=? where id=?;";
		session.execute(cql, alliance.getDescription(),alliance.getLocation(),alliance.getJoinType().getValue(),alliance.getJoinNeedPrize(),alliance.getFightRate(),alliance.getPublicFightLog(), alliance.getLevel(),alliance.getMaxMemebers(),alliance.getLastUpdateTime().getTime(), alliance.getId());
	}

	@Override
	public void removeAlliance(String allianceId) {
		String cql = "DELETE FROM gamefun.alliance WHERE id=?;";
		session.execute(cql, allianceId);
	}

	@Override
	public Alliance getAllianceById(String allianceId) {
		String cql = "SELECT * FROM gamefun.alliance where id=?;";
		ResultSet rs = session.execute(cql, allianceId);
		return allianceRSH.buildSingle(rs);
	}

	@Override
	public List<Alliance> getAlliances(String allianceName) {
		String cql = "SELECT * FROM gamefun.alliance where name=?";
		ResultSet rs = session.execute(cql, allianceName);
		return allianceRSH.buildMultiple(rs);
	}

	@Override
	public List<Alliance> getAlliances(int maxMembers, int location, int prize, int limitCount) {
		String cql = "SELECT * FROM gamefun.alliance where maxMemebers>=? AND joinNeedPrize>=? AND location";
		if(location >0) {
			cql+="=";
		} else {
			cql+=">=";
		}
		cql+="? LIMIT ? ALLOW FILTERING;";
		
		ResultSet rs = session.execute(cql, maxMembers, prize, location, limitCount);
		return allianceRSH.buildMultiple(rs);
	}

	@Override
	public void createAllianceMember(AllianceMember allianceMember) {
		//CREATE TABLE if not exists gamefun.alliance_memeber (allianceId text,roleId text,title int, donated int, lastUpdateTime timestamp,PRIMARY KEY (roleId));
		String cql = "INSERT INTO gamefun.alliance_memeber (allianceId,roleId,title,donated,lastUpdateTime) VALUES (?,?,?,?,?);";
		session.execute(cql, allianceMember.getAllianceId(),allianceMember.getRoleId(),allianceMember.getTitle().getValue(),allianceMember.getDonated(),allianceMember.getLastUpdateTime().getTime());
	}

	@Override
	public void updateAllianceMember(AllianceMember allianceMember) {
		String cql = "UPDATE gamefun.alliance_memeber SET title=?,donated=?,lastUpdateTime=? where roleId=?;";
		session.execute(cql, allianceMember.getTitle().getValue(),allianceMember.getDonated(),allianceMember.getLastUpdateTime().getTime(), allianceMember.getRoleId());
	}

	@Override
	public void removeAllianceMember(String roleId) {
		String cql = "DELETE FROM gamefun.alliance_memeber WHERE roleId=?;";
		session.execute(cql, roleId);
	}

	@Override
	public List<AllianceMember> getAllianceMembers(String allianceId) {
		String cql = "SELECT * FROM gamefun.alliance_memeber where allianceId=?;";
		ResultSet rs = session.execute(cql, allianceId);
		return allianceMemberRSH.buildMultiple(rs);
	}
	
	@Override
	public AllianceMember getAllianceMember(String roleId) {
		String cql = "SELECT * FROM gamefun.alliance_memeber where roleId=?;";
		ResultSet rs = session.execute(cql, roleId);
		return allianceMemberRSH.buildSingle(rs);
	}
	
}
