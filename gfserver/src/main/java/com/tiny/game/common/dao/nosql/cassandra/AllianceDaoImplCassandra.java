package com.tiny.game.common.dao.nosql.cassandra;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.tiny.game.common.GameConst;
import com.tiny.game.common.dao.AllianceDao;
import com.tiny.game.common.domain.alliance.Alliance;
import com.tiny.game.common.domain.alliance.AllianceEvent;
import com.tiny.game.common.domain.alliance.AllianceJoinInType;
import com.tiny.game.common.domain.alliance.AllianceMember;
import com.tiny.game.common.domain.alliance.AllianceMemberTitle;
import com.tiny.game.common.util.NetMessageUtil;

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
			bean.setConsecutiveWin(rs.getInt("consecutiveWin"));
			bean.setCurrentMemberSize(rs.getInt("currentMemberSize"));
			bean.setLogo(rs.getString("logo"));
			bean.setPoint(rs.getInt("point"));
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
			bean.setLastReqReinforceTime(rs.getTimestamp("lastReqReinforceTime"));
			bean.setPoint(rs.getInt("point"));
			bean.setRequested(rs.getInt("requested"));
			bean.setRoleLevel(rs.getInt("roleLevel"));
			bean.setRoleName(rs.getString("roleName"));
			return bean;
		}
	}
	
	private AllianceEventRSH allianceEventRSH = new AllianceEventRSH();
	private static class AllianceEventRSH extends CqlAbstractResultSetHandler<AllianceEvent> {
		public AllianceEvent factoryBeanObject(Row rs) {
			AllianceEvent bean = new AllianceEvent();
			bean.setAllianceId(rs.getString("allianceId"));
			bean.setEventId(rs.getString("eventId"));
			bean.setAllianceEventType(rs.getInt("allianceEventType"));
			bean.setTime(rs.getTimestamp("lastUpdateTime"));
			byte[] paras = NetMessageUtil.getByteArrayFromByteBuffer(rs.getBytes("parameters"));
			bean.setParameters(NetMessageUtil.convertToAllianceEventParameters(paras));
			return bean;
		}
	}
	
	@Override
	public void createAlliance(Alliance alliance) {
		String cql = "INSERT INTO gamefun.alliance (id,name,description,location,joinType,joinNeedPrize,fightRate,publicFightLog,level,maxMemebers,lastUpdateTime,currentMemberSize,consecutiveWin,point,logo) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		session.execute(cql, alliance.getId(),alliance.getName(),alliance.getDescription(),alliance.getLocation(),alliance.getJoinType().getValue(),
				alliance.getJoinNeedPrize(),alliance.getFightRate(),alliance.getPublicFightLog(), alliance.getLevel(),alliance.getMaxMemebers(),
				alliance.getLastUpdateTime().getTime(),alliance.getCurrentMemberSize(), alliance.getConsecutiveWin(), alliance.getPoint(),alliance.getLogo());
	}

	@Override
	public void updateAlliance(Alliance alliance) {
		String cql = "UPDATE gamefun.alliance SET logo=?,point=?,consecutiveWin=?,currentMemberSize=?,description=?,location=?,joinType=?,joinNeedPrize=?,fightRate=?,publicFightLog=?,level=?,maxMemebers=?,lastUpdateTime=? where id=?;";
		session.execute(cql, alliance.getLogo(), alliance.getPoint(), alliance.getConsecutiveWin(), alliance.getCurrentMemberSize(), alliance.getDescription(),alliance.getLocation(),alliance.getJoinType().getValue(),alliance.getJoinNeedPrize(),alliance.getFightRate(),alliance.getPublicFightLog(), alliance.getLevel(),alliance.getMaxMemebers(),alliance.getLastUpdateTime().getTime(), alliance.getId());
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
	public List<Alliance> getRecommendAlliancesByRoleLeaguePrize(int prize){
		return null; // TODO: finish me
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
	public void createAllianceMember(AllianceMember am) {
		String cql = "INSERT INTO gamefun.alliance_memeber (allianceId,roleId,title,donated,lastUpdateTime,roleName,roleLevel,requested,point,lastReqReinforceTime) VALUES (?,?,?,?,?,?,?,?,?,?);";
		session.execute(cql, am.getAllianceId(),am.getRoleId(),am.getTitle().getValue(),am.getDonated(),am.getLastUpdateTime().getTime(),
				am.getRoleName(), am.getRoleLevel(), am.getRequested(), am.getPoint(), am.getLastReqReinforceTime());
	}

	@Override
	public void updateAllianceMember(AllianceMember am) {
		String cql = "UPDATE gamefun.alliance_memeber SET lastReqReinforceTime=?,point=?,requested=?,roleLevel=?,roleName=?,title=?,donated=?,lastUpdateTime=? where roleId=?;";
		session.execute(cql, am.getLastReqReinforceTime(), am.getPoint(), am.getRequested(), am.getRoleLevel(), am.getRoleName(), am.getTitle().getValue(),am.getDonated(),am.getLastUpdateTime().getTime(), am.getRoleId());
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

	@Override
	public void createAllianceEvent(AllianceEvent ae) {
		String cql = "INSERT INTO gamefun.alliance_event (allianceId,eventId,belongToRoleId,allianceEventType,lastUpdateTime,parameters) VALUES (?,?,?,?,?,?);";
		ByteBuffer bf = ByteBuffer.wrap(NetMessageUtil.convertToAllianceEventParameters(ae.getParameters()).toByteArray());
		session.execute(cql, ae.getAllianceId(),ae.getEventId(),ae.getBelongToRoleId(), ae.getAllianceEventType(),ae.getTime().getTime(),bf);
	}

	@Override
	public void deleteAllianceEventByEventId(String allianceId, String eventId) {
		String cql = "DELETE FROM gamefun.alliance_event WHERE allianceId=? and eventId=?;";
		session.execute(cql, allianceId, eventId);
	}
	
	@Override
	public List<AllianceEvent> getAllianceEvents(String allianceId, int limitCount) {
		int deltaTime = GameConst.USER_QUERY_ALLIANCE_EVENT_DELTA_TIME;
		String cql = "SELECT * FROM gamefun.alliance_event where allianceId=? and lastUpdateTime >=? LIMIT ? ALLOW FILTERING;";
		ResultSet rs = session.execute(cql, allianceId, Calendar.getInstance().getTimeInMillis() - deltaTime, limitCount);
//		String cql = "SELECT * FROM gamefun.alliance_event where allianceId=? LIMIT ? ALLOW FILTERING;";
//		ResultSet rs = session.execute(cql, allianceId, limitCount);
		return allianceEventRSH.buildMultiple(rs);
	}

	@Override
	public AllianceEvent getAllianceEvent(String allianceId, String eventId) {
		String cql = "SELECT * FROM gamefun.alliance_event where allianceId=? and eventId =?;";
		ResultSet rs = session.execute(cql, allianceId, eventId);
		return allianceEventRSH.buildSingle(rs);
	}
	
}
