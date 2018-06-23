package com.tiny.game.common.server.main.bizlogic.alliance;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.alliance.Alliance;
import com.tiny.game.common.domain.alliance.AllianceEvent;
import com.tiny.game.common.domain.alliance.AllianceJoinInType;
import com.tiny.game.common.domain.alliance.AllianceMember;
import com.tiny.game.common.domain.alliance.AllianceMemberTitle;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.GameRuntimeException;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.exception.InternalRuntimeException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.util.IdGenerator;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.C_ApproveJoinInAlliance;
import game.protocol.protobuf.GameProtocol.C_CreateAlliance;
import game.protocol.protobuf.GameProtocol.C_JoinAlliance;
import game.protocol.protobuf.GameProtocol.C_RejectJoinInAlliance;
import game.protocol.protobuf.GameProtocol.IntKeyParameter;
import game.protocol.protobuf.GameProtocol.S_AllianceEvent;

public class AllianceService {
	
	private static final Logger logger = LoggerFactory.getLogger(AllianceService.class);
	
	public static void createAlliance(Role role, NetSession session, C_CreateAlliance req) {
		Alliance alliance = buildAlliance(req);
		DaoFactory.getInstance().getAllianceDao().createAlliance(alliance);
		AllianceMember am = factoryAllianceMember(alliance.getId(), role.getRoleId(), AllianceMemberTitle.Leader);
		DaoFactory.getInstance().getAllianceDao().createAllianceMember(am);
		AllianceEvent ae = factoryAllianceEvent(alliance.getId(),GameConst.ALLIANCE_EVENT_CREATE_ALLIANCE,role.getRoleId(),role.getRoleName());
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, NetMessageUtil.convertAllianceEvent(ae).build());
	}
	
	public static void joinInAlliance(Role role, NetSession session, C_JoinAlliance req) {
		String allianceId = req.getAllianceId();
		Alliance alliance = DaoFactory.getInstance().getAllianceDao().getAllianceById(allianceId);
		if(alliance == null) {
			throw new GameRuntimeException(GameConst.Error_Alliance_Not_Exist, "Not found alliance by id: " + allianceId);
		}
		AllianceJoinInType joinInType = alliance.getJoinType();
		if(joinInType == AllianceJoinInType.Any) {
			allowRoleJoinInAlliace(allianceId, role.getRoleId(), role.getRoleName());
		} else if (joinInType == AllianceJoinInType.Approve) {
			AllianceEvent ae = factoryAllianceEvent(alliance.getId(),GameConst.ALLIANCE_EVENT_NEED_JOIN_IN,role.getRoleId(),role.getRoleName());
			DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
			broadcasetAllianceEvent(allianceId, NetMessageUtil.convertAllianceEvent(ae).build());
		} else { // reject
			throw new GameRuntimeException(GameConst.Error_Alliance_Not_Allow_To_Join, "Alliance not allowed to join in: " + allianceId);
		}
	}
	
	// TODO: detail each invalid parameter cases
	public static void approveJoinInAlliance(Role role, NetSession session, C_ApproveJoinInAlliance req) {
		Role joinInRole = validateOperRoleJoinInData(role, session, req.getRoleId(), req.getAllianceId());
		DaoFactory.getInstance().getAllianceDao().deleteAllianceEvent(req.getAllianceId(), req.getEventId());
		approveRoleJoinInAlliace(req.getAllianceId(), joinInRole.getRoleId(), joinInRole.getRoleName(), role.getRoleId(), role.getRoleName());
	}
	
	private static Role validateOperRoleJoinInData(Role role, NetSession session, String needJoinInRoleId, String allianceId) {
		Role needJoinInRole = DaoFactory.getInstance().getUserDao().getRole(needJoinInRoleId);
		if(needJoinInRole == null) {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not found role by id: " + needJoinInRoleId);
		}
		
		Alliance alliance = DaoFactory.getInstance().getAllianceDao().getAllianceById(allianceId);
		if(alliance == null) {
			throw new GameRuntimeException(GameConst.Error_Alliance_Not_Exist, "Not found alliance by id: " + allianceId);
		}
		
		AllianceMember am = DaoFactory.getInstance().getAllianceDao().getAllianceMember(role.getRoleId());
		if(am==null) {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "role : " + role.getRoleId()+" not in alliance");
		}
		
		if(am.getAllianceId() != allianceId) {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "role : " + role.getRoleId()+" in alliance: "+am.getAllianceId()+", not in alliance : " + allianceId);
		}
		
		if(am.getTitle() == AllianceMemberTitle.Member) {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "role : " + role.getRoleId()+" doesn't have permission to approve join in alliance.");
		}
		return needJoinInRole;
	}
	
	public static void rejectJoinInAlliance(Role role, NetSession session, C_RejectJoinInAlliance req) {
		Role joinInRole = validateOperRoleJoinInData(role, session, req.getRoleId(), req.getAllianceId());
		DaoFactory.getInstance().getAllianceDao().deleteAllianceEvent(req.getAllianceId(), req.getEventId());
		rejectRoleJoinInAlliace(req.getAllianceId(), joinInRole.getRoleId(), joinInRole.getRoleName(), role.getRoleId(), role.getRoleName());
	}
	
	private static void approveRoleJoinInAlliace(String allianceId, String roleId, String roleName, String byRoleId, String byRoleName) {
		AllianceMember am = factoryAllianceMember(allianceId, roleId, AllianceMemberTitle.Member);
		DaoFactory.getInstance().getAllianceDao().createAllianceMember(am);
		AllianceEvent ae = factoryAllianceEvent(allianceId,GameConst.ALLIANCE_EVENT_JOIN_IN_ALLIANCE,roleId,roleName);
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_ID, byRoleId);
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_NAME, byRoleName);
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
		broadcasetAllianceEvent(allianceId, NetMessageUtil.convertAllianceEvent(ae).build());
	}
	
	private static void rejectRoleJoinInAlliace(String allianceId, String roleId, String roleName, String byRoleId, String byRoleName) {
		AllianceEvent ae = factoryAllianceEvent(allianceId,GameConst.ALLIANCE_EVENT_REJECT_JOIN_IN,roleId,roleName);
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_ID, byRoleId);
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_NAME, byRoleName);
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
		broadcasetAllianceEvent(allianceId, NetMessageUtil.convertAllianceEvent(ae).build());
	}
	
	private static void allowRoleJoinInAlliace(String allianceId, String roleId, String roleName) {
		AllianceMember am = factoryAllianceMember(allianceId, roleId, AllianceMemberTitle.Member);
		DaoFactory.getInstance().getAllianceDao().createAllianceMember(am);
		AllianceEvent ae = factoryAllianceEvent(allianceId,GameConst.ALLIANCE_EVENT_JOIN_IN_ALLIANCE,roleId,roleName);
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
		broadcasetAllianceEvent(allianceId, NetMessageUtil.convertAllianceEvent(ae).build());
	}
	
	private static void broadcasetAllianceEvent(String allianceId, S_AllianceEvent event) {
		
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
	
	private static Alliance buildAlliance(C_CreateAlliance req) {
		Alliance alliance = new Alliance();
		alliance.setId(IdGenerator.genUniqueAllianceId());
		alliance.setDescription(req.getDescription());
		alliance.setFightRate(req.getFightRate());
		alliance.setJoinNeedPrize(req.getJoinNeedPrize());
		alliance.setJoinType(AllianceJoinInType.valueOf(req.getJoinType()));
		alliance.setLevel(1);
		alliance.setLocation(req.getLocation());
		alliance.setMaxMemebers(50); // TODO: const 
		alliance.setName(req.getName());
		alliance.setPublicFightLog(req.getPublicFightLog());
		alliance.setLastUpdateTime(Calendar.getInstance().getTime());
		return alliance;
	}
	
	private static AllianceMember factoryAllianceMember(String allianceId, String roleId, AllianceMemberTitle title) {
		AllianceMember am = new AllianceMember();
		am.setAllianceId(allianceId);
		am.setRoleId(roleId);
		am.setDonated(0);
		am.setTitle(title);
		am.setLastUpdateTime(Calendar.getInstance().getTime());
		return am;
	}
	
}
