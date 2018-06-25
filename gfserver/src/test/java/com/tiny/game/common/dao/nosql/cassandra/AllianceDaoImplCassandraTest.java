package com.tiny.game.common.dao.nosql.cassandra;


import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.domain.alliance.Alliance;
import com.tiny.game.common.domain.alliance.AllianceEvent;
import com.tiny.game.common.domain.alliance.AllianceJoinInType;
import com.tiny.game.common.domain.alliance.AllianceMember;
import com.tiny.game.common.domain.alliance.AllianceMemberTitle;
import com.tiny.game.common.util.IdGenerator;

public class AllianceDaoImplCassandraTest extends CassandraManagerTestBase {

	private AllianceMember buildAllianceMember(String roleId) {
		AllianceMember am = new AllianceMember();
		am.setAllianceId("a123456");
		am.setRoleId(roleId);
		am.setDonated(10);
		am.setTitle(AllianceMemberTitle.Leader);
		am.setLastUpdateTime(Calendar.getInstance().getTime());
		return am;
	}
	
	private Alliance buildAlliance(String id) {
		Alliance am = new Alliance();
		am.setId(id);
		am.setDescription("");
		am.setFightRate(1);
		am.setJoinNeedPrize(100);
		am.setJoinType(AllianceJoinInType.Any);
		am.setLevel(1);
		am.setLocation(10);
		am.setMaxMemebers(50);
		am.setName("中国fun");
		am.setPublicFightLog(1);
		am.setLastUpdateTime(Calendar.getInstance().getTime());
		return am;
	}
	
	@Test
	public void testAlliance() {
		AllianceDaoImplCassandra.getInstance().removeAlliance("a123456");
		AllianceDaoImplCassandra.getInstance().removeAlliance("b123456");
		Alliance alliance = buildAlliance("a123456");
		AllianceDaoImplCassandra.getInstance().createAlliance(alliance);
		alliance = AllianceDaoImplCassandra.getInstance().getAllianceById("a123456");
		assertTrue(alliance.getId().equals("a123456"));
		
		alliance.setDescription("&*dfdafy");
		alliance.setLevel(2);
		AllianceDaoImplCassandra.getInstance().updateAlliance(alliance);
		alliance = AllianceDaoImplCassandra.getInstance().getAllianceById("a123456");
		assertTrue(alliance.getDescription().equals("&*dfdafy"));
		assertTrue(alliance.getLevel()==2);
		assertTrue(alliance.getName().equals("中国fun"));
		
		List<Alliance> list = AllianceDaoImplCassandra.getInstance().getAlliances("中国fun");
		assertTrue(list.size() == 1);
		list = AllianceDaoImplCassandra.getInstance().getAlliances(1, 0, 0, 10);
		assertTrue(list.size() == 1);
		
		list = AllianceDaoImplCassandra.getInstance().getAlliances(1, 1, 0, 10);
		assertTrue(list.size() == 0);
		
		list = AllianceDaoImplCassandra.getInstance().getAlliances(60, 0, 0, 10);
		assertTrue(list.size() == 0);
		
		list = AllianceDaoImplCassandra.getInstance().getAlliances(1, 10, 0, 10);
		assertTrue(list.size() == 1);
		
		list = AllianceDaoImplCassandra.getInstance().getAlliances(1, 10, 200, 10);
		assertTrue(list.size() == 0);
		
		alliance = buildAlliance("b123456");
		alliance.setLocation(5);
		alliance.setJoinNeedPrize(50);
		AllianceDaoImplCassandra.getInstance().createAlliance(alliance);
		
		list = AllianceDaoImplCassandra.getInstance().getAlliances(1, 0, 10, 10);
		assertTrue(list.size() == 2);
		
		list = AllianceDaoImplCassandra.getInstance().getAlliances(1, 0, 70, 10);
		assertTrue(list.size() == 1);
		
		list = AllianceDaoImplCassandra.getInstance().getAlliances(1, 10, 0, 10);
		assertTrue(list.size() == 1);
		
		AllianceDaoImplCassandra.getInstance().removeAlliance("a123456");
		alliance = AllianceDaoImplCassandra.getInstance().getAllianceById("a123456");
		assertTrue(alliance==null);
		AllianceDaoImplCassandra.getInstance().removeAlliance("b123456");
	}
	
	@Test
	public void testAllianceMember() {
		AllianceMember am = buildAllianceMember("user3");
		AllianceDaoImplCassandra.getInstance().createAllianceMember(am);
		
		am = AllianceDaoImplCassandra.getInstance().getAllianceMember("user3");
		assertTrue(am.getAllianceId().equals("a123456"));
		
		am.setDonated(20);
		AllianceDaoImplCassandra.getInstance().updateAllianceMember(am);
		assertTrue(am.getDonated()==20);
		
		am = buildAllianceMember("user2");
		AllianceDaoImplCassandra.getInstance().createAllianceMember(am);
		
		List<AllianceMember> list = AllianceDaoImplCassandra.getInstance().getAllianceMembers("a123456");
		assertTrue(list.size() == 2);
		
		AllianceDaoImplCassandra.getInstance().removeAllianceMember("user3");
		am = AllianceDaoImplCassandra.getInstance().getAllianceMember("user3");
		assertTrue(am==null);
		
		list = AllianceDaoImplCassandra.getInstance().getAllianceMembers("a123456");
		assertTrue(list.size() == 1);
		
		AllianceDaoImplCassandra.getInstance().removeAllianceMember("user2");
		list = AllianceDaoImplCassandra.getInstance().getAllianceMembers("a123456");
		assertTrue(list.size() == 0);
	}
	
	@Test
	public void testAllianceEvent() {
		AllianceEvent ae = factoryAllianceEvent("a123456",GameConst.ALLIANCE_EVENT_CREATE_ALLIANCE, "user3", "user3");
		AllianceDaoImplCassandra.getInstance().createAllianceEvent(ae);
		
		String allianceId = ae.getAllianceId();
		String eventId = ae.getEventId();
		ae = AllianceDaoImplCassandra.getInstance().getAllianceEvent(ae.getAllianceId(), ae.getEventId());
		assertTrue(ae.getAllianceId().equals(allianceId));
		assertTrue(ae.getParameter(GameConst.ALLIANCE_PARA_ACTION_ROLE_ID).equals("user3"));
		
		List<AllianceEvent> list = AllianceDaoImplCassandra.getInstance().getAllianceEvents(allianceId, 10);
		assertTrue(list.size() == 1);
		
		AllianceDaoImplCassandra.getInstance().deleteAllianceEventByEventId(allianceId, eventId);
		list = AllianceDaoImplCassandra.getInstance().getAllianceEvents(allianceId, 10);
		assertTrue(list.size() == 0);
	}
	
	private static AllianceEvent factoryAllianceEvent(String allianceId, int eventType, String actionRoleId, String actionRoleName) {
		AllianceEvent ae = new AllianceEvent();
		ae.setAllianceId(allianceId);
		ae.setAllianceEventType(eventType);
		ae.setEventId(IdGenerator.genUniqueAllianceEventId());
		ae.setTime(Calendar.getInstance().getTime());
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_ROLE_ID, actionRoleId);
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_ROLE_NAME, actionRoleName);
		return ae;
	}
}
