package com.tiny.game.common.server.main.bizlogic.alliance;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.alliance.Alliance;
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
		// notify user alliance event
		S_AllianceEvent.Builder builder = factoryS_AllianceEventBuilder(alliance.getId(),GameConst.ALLIANCE_EVENT_CREATE_ALLIANCE,role.getRoleId(),role.getRoleName());
		// TODO: persist alliance event
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, builder.build());
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
			S_AllianceEvent.Builder builder = factoryS_AllianceEventBuilder(alliance.getId(),GameConst.ALLIANCE_EVENT_NEED_JOIN_IN,role.getRoleId(),role.getRoleName());
			broadcasetAllianceEvent(allianceId, builder.build());
		} else { // reject
			throw new GameRuntimeException(GameConst.Error_Alliance_Not_Allow_To_Join, "Alliance not allowed to join in: " + allianceId);
		}
	}
	
	// TODO: detail each invalid parameter cases
	public static void approveJoinInAlliance(Role role, NetSession session, C_ApproveJoinInAlliance req) {
		Role joinInRole = validateOperRoleJoinInData(role, session, req.getRoleId(), req.getAllianceId());
		S_AllianceEvent event = null; // req.getEventId();
		// TODO: delete event from db
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
		S_AllianceEvent event = null; // req.getEventId();
		// TODO: delete event from db
		rejectRoleJoinInAlliace(req.getAllianceId(), joinInRole.getRoleId(), joinInRole.getRoleName(), role.getRoleId(), role.getRoleName());
	}
	
	private static void approveRoleJoinInAlliace(String allianceId, String roleId, String roleName, String byRoleId, String byRoleName) {
		AllianceMember am = factoryAllianceMember(allianceId, roleId, AllianceMemberTitle.Member);
		DaoFactory.getInstance().getAllianceDao().createAllianceMember(am);
		// notify user alliance event
		S_AllianceEvent.Builder builder = factoryS_AllianceEventBuilder(allianceId,GameConst.ALLIANCE_EVENT_JOIN_IN_ALLIANCE,roleId,roleName);
		builder.addParameter(buildIntKeyParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_ID, byRoleId));
		builder.addParameter(buildIntKeyParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_NAME, byRoleName));
		broadcasetAllianceEvent(allianceId, builder.build());
	}
	
	private static void rejectRoleJoinInAlliace(String allianceId, String roleId, String roleName, String byRoleId, String byRoleName) {
		S_AllianceEvent.Builder builder = factoryS_AllianceEventBuilder(allianceId,GameConst.ALLIANCE_EVENT_REJECT_JOIN_IN,roleId,roleName);
		builder.addParameter(buildIntKeyParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_ID, byRoleId));
		builder.addParameter(buildIntKeyParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_NAME, byRoleName));
		broadcasetAllianceEvent(allianceId, builder.build());
	}
	
	private static void allowRoleJoinInAlliace(String allianceId, String roleId, String roleName) {
		AllianceMember am = factoryAllianceMember(allianceId, roleId, AllianceMemberTitle.Member);
		DaoFactory.getInstance().getAllianceDao().createAllianceMember(am);
		// notify user alliance event
		S_AllianceEvent.Builder builder = factoryS_AllianceEventBuilder(allianceId,GameConst.ALLIANCE_EVENT_JOIN_IN_ALLIANCE,roleId,roleName);
		// TODO: persist alliance event
		broadcasetAllianceEvent(allianceId, builder.build());
	}
	
	private static void broadcasetAllianceEvent(String allianceId, S_AllianceEvent event) {
		
	}
	
	private static S_AllianceEvent.Builder factoryS_AllianceEventBuilder(String allianceId, int eventType, String actionRoleId, String actionRoleName){
		S_AllianceEvent.Builder builder = S_AllianceEvent.newBuilder();
		builder.setAllianceId(allianceId);
		builder.setAllianceEventType(eventType);
		builder.setEventId(IdGenerator.genUniqueAllianceEventId());
		builder.setTime(System.currentTimeMillis()+"");
		builder.addParameter(buildIntKeyParameter(GameConst.ALLIANCE_PARA_ACTION_ROLE_ID, actionRoleId));
		builder.addParameter(buildIntKeyParameter(GameConst.ALLIANCE_PARA_ACTION_ROLE_NAME, actionRoleName));
		return builder;
	}
	
	private static IntKeyParameter buildIntKeyParameter(int key, String value) {
		IntKeyParameter.Builder builder = IntKeyParameter.newBuilder();
		builder.setKey(key);
		builder.setValue(value);
		return builder.build();
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
