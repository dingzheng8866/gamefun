package com.tiny.game.common.server.main.bizlogic.alliance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.tiny.game.common.GameConst;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.alliance.Alliance;
import com.tiny.game.common.domain.alliance.AllianceEvent;
import com.tiny.game.common.domain.alliance.AllianceJoinInType;
import com.tiny.game.common.domain.alliance.AllianceMember;
import com.tiny.game.common.domain.alliance.AllianceMemberTitle;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.UserOnlineInfo;
import com.tiny.game.common.exception.GameRuntimeException;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.exception.InternalRuntimeException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.broadcast.RouterService;
import com.tiny.game.common.util.IdGenerator;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.AllianceShortInfo;
import game.protocol.protobuf.GameProtocol.C_ApproveJoinInAlliance;
import game.protocol.protobuf.GameProtocol.C_ChangeAllianceLeader;
import game.protocol.protobuf.GameProtocol.C_CreateAlliance;
import game.protocol.protobuf.GameProtocol.C_DoAlliaceDonation;
import game.protocol.protobuf.GameProtocol.C_DownAllianceMemberTitle;
import game.protocol.protobuf.GameProtocol.C_FavoriteAlliance;
import game.protocol.protobuf.GameProtocol.C_GetAllianceDetail;
import game.protocol.protobuf.GameProtocol.C_JoinAlliance;
import game.protocol.protobuf.GameProtocol.C_KickoutAllianceMember;
import game.protocol.protobuf.GameProtocol.C_LeaveAlliance;
import game.protocol.protobuf.GameProtocol.C_RejectJoinInAlliance;
import game.protocol.protobuf.GameProtocol.C_RequestAllianceDonation;
import game.protocol.protobuf.GameProtocol.C_SearchAlliance;
import game.protocol.protobuf.GameProtocol.C_UpAllianceMemberTitle;
import game.protocol.protobuf.GameProtocol.I_RouteMessage;
import game.protocol.protobuf.GameProtocol.NetAllianceDetailInfo;
import game.protocol.protobuf.GameProtocol.NetAllianceMemberInfo;
import game.protocol.protobuf.GameProtocol.OwnItemNotification;
import game.protocol.protobuf.GameProtocol.S_AllianceEvent;
import game.protocol.protobuf.GameProtocol.S_AllianceMemberInfoUpdate;
import game.protocol.protobuf.GameProtocol.S_AllianceNotification;
import game.protocol.protobuf.GameProtocol.S_BatchOwnItemNotification;
import game.protocol.protobuf.GameProtocol.S_GetAllianceDetail;
import game.protocol.protobuf.GameProtocol.S_SearchAlliance;

public class AllianceService {
	
	private static final Logger logger = LoggerFactory.getLogger(AllianceService.class);
	
	public static void kickoutAllianceMember(Role role, NetSession session, C_KickoutAllianceMember req){
		AllianceMember m1 = DaoFactory.getInstance().getAllianceDao().getAllianceMember(role.getRoleId());
		AllianceMember m2 = DaoFactory.getInstance().getAllianceDao().getAllianceMember(req.getCandidateRoleId());
		if(!m1.getAllianceId().equals(m2.getAllianceId()) || m1.getTitle().getValue() <= m2.getTitle().getValue()){
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not allow to change leader: " + req.getCandidateRoleId());
		}
		
		DaoFactory.getInstance().getAllianceDao().removeAllianceMember(m2.getRoleId());

		AllianceEvent ae = factoryAllianceEvent(m2.getAllianceId(),GameConst.ALLIANCE_EVENT_KICKOUT_MEMBER,m2.getRoleId(),m2.getRoleName());
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_ID, role.getRoleId());
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_NAME, role.getRoleName());
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);

		S_AllianceNotification.Builder notification = S_AllianceNotification.newBuilder();
		notification.addAllianceEvent(NetMessageUtil.convertAllianceEvent(ae).build());
		
		broadcasetAllianceNotification(m2.getAllianceId(), notification.build());
	}
	
	
	public static void changeAllianceLeader(Role role, NetSession session, C_ChangeAllianceLeader req){
		AllianceMember m1 = DaoFactory.getInstance().getAllianceDao().getAllianceMember(role.getRoleId());
		AllianceMember m2 = DaoFactory.getInstance().getAllianceDao().getAllianceMember(req.getCandidateRoleId());
		if(m1.getTitle() != AllianceMemberTitle.Leader){
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not allow to change leader: " + req.getCandidateRoleId());
		}
		m1.setTitle(AllianceMemberTitle.CoLeader);
		m1.setLastUpdateTime(Calendar.getInstance().getTime());
		DaoFactory.getInstance().getAllianceDao().updateAllianceMember(m1);
		
		m2.setTitle(AllianceMemberTitle.Leader);
		m2.setLastUpdateTime(Calendar.getInstance().getTime());
		DaoFactory.getInstance().getAllianceDao().updateAllianceMember(m2);

		AllianceEvent ae = factoryAllianceEvent(m2.getAllianceId(),GameConst.ALLIANCE_EVENT_CHANGE_LEADER,m2.getRoleId(),m2.getRoleName());
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_ID, role.getRoleId());
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_NAME, role.getRoleName());
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);

		S_AllianceNotification.Builder notification = S_AllianceNotification.newBuilder();
		notification.addMemberUpdate(S_AllianceMemberInfoUpdate.newBuilder().addMember(convertAllianceMember(m1)).build());
		notification.addMemberUpdate(S_AllianceMemberInfoUpdate.newBuilder().addMember(convertAllianceMember(m2)).build());
		notification.addAllianceEvent(NetMessageUtil.convertAllianceEvent(ae).build());
		
		broadcasetAllianceNotification(m2.getAllianceId(), notification.build());
	}
	
	public static void upAllianceMemberTitle(Role role, NetSession session, C_UpAllianceMemberTitle req){
		AllianceMember m1 = DaoFactory.getInstance().getAllianceDao().getAllianceMember(role.getRoleId());
		AllianceMember m2 = DaoFactory.getInstance().getAllianceDao().getAllianceMember(req.getCandidateRoleId());
		if(m1.getTitle().getValue() <= m2.getTitle().getValue()){
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not allow to up title: " + req.getCandidateRoleId());
		}
		m2.setTitle(AllianceMemberTitle.valueOf(m2.getTitle().getValue()+1));
		m2.setLastUpdateTime(Calendar.getInstance().getTime());
		DaoFactory.getInstance().getAllianceDao().updateAllianceMember(m2);
		
		S_AllianceNotification.Builder notification = S_AllianceNotification.newBuilder();
		
		if(m2.getTitle() == AllianceMemberTitle.Leader){
			m1.setTitle(AllianceMemberTitle.valueOf(m1.getTitle().getValue()-1));
			m1.setLastUpdateTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getAllianceDao().updateAllianceMember(m1);
			
			notification.addMemberUpdate(S_AllianceMemberInfoUpdate.newBuilder().addMember(convertAllianceMember(m1)).build());
			
			AllianceEvent ae = factoryAllianceEvent(m1.getAllianceId(),GameConst.ALLIANCE_EVENT_DOWN_TITLE,role.getRoleId(),role.getRoleName());
			DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
			
			notification.addAllianceEvent(NetMessageUtil.convertAllianceEvent(ae).build());
		}
		
		AllianceEvent ae = factoryAllianceEvent(m2.getAllianceId(),GameConst.ALLIANCE_EVENT_UP_TITLE,m2.getRoleId(),m2.getRoleName());
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
		
		notification.addMemberUpdate(S_AllianceMemberInfoUpdate.newBuilder().addMember(convertAllianceMember(m2)).build());
		notification.addAllianceEvent(NetMessageUtil.convertAllianceEvent(ae).build());

		broadcasetAllianceNotification(m2.getAllianceId(), notification.build());
	}
	
	public static void downAllianceMemberTitle(Role role, NetSession session, C_DownAllianceMemberTitle req){
		AllianceMember m1 = DaoFactory.getInstance().getAllianceDao().getAllianceMember(role.getRoleId());
		AllianceMember m2 = DaoFactory.getInstance().getAllianceDao().getAllianceMember(req.getCandidateRoleId());
		if(m1.getTitle().getValue() <= m2.getTitle().getValue() || m2.getTitle() == AllianceMemberTitle.Member){
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not allow to down title: " + req.getCandidateRoleId());
		}
		m2.setTitle(AllianceMemberTitle.valueOf(m2.getTitle().getValue()-1));
		m2.setLastUpdateTime(Calendar.getInstance().getTime());
		DaoFactory.getInstance().getAllianceDao().updateAllianceMember(m2);

		AllianceEvent ae = factoryAllianceEvent(m2.getAllianceId(),GameConst.ALLIANCE_EVENT_DOWN_TITLE,m2.getRoleId(),m2.getRoleName());
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_ID, role.getRoleId());
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_NAME, role.getRoleName());
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);

		S_AllianceNotification.Builder notification = S_AllianceNotification.newBuilder();
		notification.addMemberUpdate(S_AllianceMemberInfoUpdate.newBuilder().addMember(convertAllianceMember(m2)).build());
		notification.addAllianceEvent(NetMessageUtil.convertAllianceEvent(ae).build());
		
		broadcasetAllianceNotification(m2.getAllianceId(), notification.build());
	}
	
	public static void getRecommendAlliances(Role role, NetSession session) {
		List<Alliance> list = DaoFactory.getInstance().getAllianceDao().getRecommendAlliancesByRoleLeaguePrize(role.getLeaguePrize());
		if(list == null) {
			list = new ArrayList<Alliance>();
		}
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, buildS_SearchAlliance(list));
	}
	
	public static void searchAlliance(Role role, NetSession session, C_SearchAlliance req) {
		List<Alliance> list = null;
		String allianceName = req.getName();
		if(allianceName!=null && allianceName.trim().length() > 0){
			list = DaoFactory.getInstance().getAllianceDao().getAlliances(allianceName);
		} else { // TODO: finish me, improve search performance
			list = DaoFactory.getInstance().getAllianceDao().getAlliances(req.getMaxMember(), req.getLocation(), req.getJoinNeedPrize(), 50);
		}
		if(list == null) {
			list = new ArrayList<Alliance>();
		}
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, buildS_SearchAlliance(list));
	}
	
	private static S_SearchAlliance buildS_SearchAlliance(List<Alliance> list){
		S_SearchAlliance.Builder builder = S_SearchAlliance.newBuilder();
		for(Alliance al : list){
			AllianceShortInfo.Builder ab = AllianceShortInfo.newBuilder();
			ab.setLevel(al.getLevel());
			ab.setLogo(al.getLogo());
			ab.setMaxMemberSize(al.getMaxMemebers());
			ab.setMemberSize(al.getCurrentMemberSize());
			ab.setName(al.getName());
			ab.setPoints(al.getPoint());
			builder.addAlliance(ab.build());
		}
		return builder.build();
	}
	
	public static void doAllianceDonation(Role role, NetSession session, C_DoAlliaceDonation req) {
		AllianceMember am = DaoFactory.getInstance().getAllianceDao().getAllianceMember(role.getRoleId());
		if(am == null) {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not in alliance to role id: " + role.getRoleId());
		}
		
		AllianceEvent reqEvent = DaoFactory.getInstance().getAllianceDao().getAllianceEvent(am.getAllianceId(), req.getReqReinforceEventId());
		if(reqEvent==null || reqEvent.getAllianceEventType()!=GameConst.ALLIANCE_EVENT_REQ_REINFORCE){
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not found req reinforce event: " + req.getReqReinforceEventId());
		}
		
		String roleId = reqEvent.getParameter(GameConst.ALLIANCE_PARA_ACTION_ROLE_ID);
		if(roleId == null){
			throw new InternalBugException("Not found parameter role on req reinforce event: " + req.getReqReinforceEventId());
		}
		
		Role reqRole = DaoFactory.getInstance().getUserDao().getRole(roleId);
		boolean donationIsFull = false; // TODO
		
		req.getCount();
		req.getItemId();
		// TODO: donate check item is valid or not
		// TODO: two role to transfer item
		
		
//		Date lastReqTime = am.getLastReqReinforceTime();
//		Date curTime = Calendar.getInstance().getTime();
//		if(lastReqTime==null || curTime.getTime() - lastReqTime.getTime() >= role.getReqReinforceTimeInterval()){
//			am.setRequested(am.getRequested() + 1);
//			am.setLastUpdateTime(Calendar.getInstance().getTime());
//			am.setLastReqReinforceTime(Calendar.getInstance().getTime());
//			DaoFactory.getInstance().getAllianceDao().updateAllianceMember(am);
//			
//			AllianceEvent ae = factoryAllianceEvent(am.getAllianceId(),GameConst.ALLIANCE_EVENT_REQ_REINFORCE,role.getRoleId(),role.getRoleName());
//			ae.setParameter(GameConst.ALLIANCE_PARA_REQ_REINFORCE_MSG, req.getHelpContent());
//			DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
//			broadcasetAllianceEvent(am.getAllianceId(), NetMessageUtil.convertAllianceEvent(ae).build());
//		} else {
//			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not allow to request reinforce by time limit: " + role.getReqReinforceTimeInterval());
//		}
	}
	
	public static void requestAllianceDonation(Role role, NetSession session, C_RequestAllianceDonation req) {
		AllianceMember am = DaoFactory.getInstance().getAllianceDao().getAllianceMember(role.getRoleId());
		if(am == null) {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not in alliance to role id: " + role.getRoleId());
		}
		Date lastReqTime = am.getLastReqReinforceTime();
		Date curTime = Calendar.getInstance().getTime();
		if(lastReqTime==null || curTime.getTime() - lastReqTime.getTime() >= role.getReqReinforceTimeInterval()){
			am.setRequested(am.getRequested() + 1);
			am.setLastUpdateTime(Calendar.getInstance().getTime());
			am.setLastReqReinforceTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getAllianceDao().updateAllianceMember(am);
			
			AllianceEvent ae = factoryAllianceEvent(am.getAllianceId(),GameConst.ALLIANCE_EVENT_REQ_REINFORCE,role.getRoleId(),role.getRoleName());
			ae.setParameter(GameConst.ALLIANCE_PARA_REQ_REINFORCE_MSG, req.getHelpContent());
			DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
			broadcasetAllianceNotification(am.getAllianceId(), NetMessageUtil.convertAllianceEvent(ae).build());
		} else {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not allow to request reinforce by time limit: " + role.getReqReinforceTimeInterval());
		}
	}
	
	public static void getAllianceDetailAlliance(Role role, NetSession session, C_GetAllianceDetail req) {
		String allianceId = req.getAllianceId();
		Alliance alliance = DaoFactory.getInstance().getAllianceDao().getAllianceById(allianceId);
		if(alliance == null) {
			throw new GameRuntimeException(GameConst.Error_Alliance_Not_Exist, "Not found alliance by id: " + allianceId);
		}
		
		List<AllianceMember> members = DaoFactory.getInstance().getAllianceDao().getAllianceMembers(allianceId);
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, buildS_GetAllianceDetail(alliance, members));
	}
	
	private static S_GetAllianceDetail buildS_GetAllianceDetail(Alliance alliance, List<AllianceMember> members){
		NetAllianceDetailInfo.Builder nab = NetAllianceDetailInfo.newBuilder();
		nab.setAllianceId(alliance.getId());
		nab.setName(alliance.getName());
		nab.setConsecutiveWin(alliance.getConsecutiveWin()); 
		nab.setDescription(alliance.getDescription());
		nab.setFightRate(alliance.getFightRate());
		nab.setJoinNeedPrize(alliance.getJoinNeedPrize());
		nab.setJoinType(alliance.getJoinType().getValue());
		nab.setLocation(alliance.getLocation());
		nab.setLogo(alliance.getLogo());
		nab.setPublicFightLog(alliance.getPublicFightLog());
		
		for(AllianceMember am : members){
			nab.addMember(convertAllianceMember(am));
		}
		
		S_GetAllianceDetail.Builder builder = S_GetAllianceDetail.newBuilder();
		builder.setAlliance(nab);
		return builder.build();
	}
	
	private static NetAllianceMemberInfo convertAllianceMember(AllianceMember am) {
		// note: when role key info changed need to update alliance member info
		NetAllianceMemberInfo.Builder nmb = NetAllianceMemberInfo.newBuilder();
		nmb.setPoint(am.getPoint()); 
		nmb.setRecentDonated(am.getDonated());
		nmb.setRecentRequested(am.getRequested());
		nmb.setRoleId(am.getRoleId());
		nmb.setRoleLevel(am.getRoleLevel());
		nmb.setRoleName(am.getRoleName());
		nmb.setTitle(am.getTitle().getValue());
		return nmb.build();
	}
	
	public static void favoriteAlliance(Role role, NetSession session, C_FavoriteAlliance req) {
		String allianceId = req.getAllianceId();
		Alliance alliance = DaoFactory.getInstance().getAllianceDao().getAllianceById(allianceId);
		if(alliance == null) {
			throw new GameRuntimeException(GameConst.Error_Alliance_Not_Exist, "Not found alliance by id: " + allianceId);
		}
		
		if(!role.hasItemContainsSubExtendAttributeValue(ItemId.favoriteAlliance, ItemId.favoriteAlliance.name(), allianceId)){
			role.addItemSubExtendAttributeValue(ItemId.favoriteAlliance, ItemId.favoriteAlliance.name(), allianceId);
			role.setLastUpdateTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getUserDao().updateRole(role);
			logger.info("Role "+ role.getRoleId() +" favoriate alliance: " + req.getAllianceId());
			S_BatchOwnItemNotification.Builder builder = S_BatchOwnItemNotification.newBuilder();
			builder.addNotification(NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Set, role.getOwnItem(ItemId.favoriteAlliance)));
			NetLayerManager.getInstance().asyncSendOutboundMessage(session, builder.build());
		}
	}
	
	public static void leaveAlliance(Role role, NetSession session, C_LeaveAlliance req) {
//		String allianceId = req.getAllianceId();
		logger.info("Role " + role.getRoleId() + " want to leave alliance: " + req.getAllianceId());
		AllianceMember am = DaoFactory.getInstance().getAllianceDao().getAllianceMember(role.getRoleId());
		if(am!=null){
			//check am is leader or not
			DaoFactory.getInstance().getAllianceDao().removeAllianceMember(role.getRoleId());
			
			boolean needToRemoveAlliance = false;
			AllianceMember newLeader = null;
			if(am.getTitle() == AllianceMemberTitle.Leader){
				List<AllianceMember> members = DaoFactory.getInstance().getAllianceDao().getAllianceMembers(am.getAllianceId());
				if(members.size() == 1){
					needToRemoveAlliance = true;
				} else {
					members.remove(am);
			        Collections.sort(members,new Comparator<AllianceMember>() {
			            @Override
			            public int compare(AllianceMember o1, AllianceMember o2) {
			                return o2.getTitle().getValue()-o1.getTitle().getValue();
			            }
			        });
			        newLeader = members.get(0); // Note: client need to check this
			        newLeader.setTitle(AllianceMemberTitle.Leader);
			        newLeader.setLastUpdateTime(Calendar.getInstance().getTime());
				}
			}
			
			if(needToRemoveAlliance){
				logger.info("Alliance " + am.getAllianceId() + " deleted");
				DaoFactory.getInstance().getAllianceDao().removeAlliance(am.getAllianceId());
			} else {
				logger.info("Role " + role.getRoleId() + " leaved alliance: " + am.getAllianceId());
				AllianceEvent ae = factoryAllianceEvent(am.getAllianceId(),GameConst.ALLIANCE_EVENT_JOIN_OUT_ALLIANCE,role.getRoleId(),role.getRoleName());
				DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
				broadcasetAllianceNotification(am.getAllianceId(), NetMessageUtil.convertAllianceEvent(ae).build());
				
				// TODO: batch send alliance events
				if(newLeader!=null){
					logger.info("Role " + newLeader.getRoleId() + " changed to leader to alliance: " + am.getAllianceId());
					DaoFactory.getInstance().getAllianceDao().updateAllianceMember(newLeader);
					Role leaderRole = DaoFactory.getInstance().getUserDao().getRole(newLeader.getRoleId());
					ae = factoryAllianceEvent(am.getAllianceId(),GameConst.ALLIANCE_EVENT_CHANGE_LEADER,newLeader.getRoleId(),leaderRole.getRoleName());
					DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
					broadcasetAllianceNotification(am.getAllianceId(), NetMessageUtil.convertAllianceEvent(ae).build());
				}
			}
		}
	}
	
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
			broadcasetAllianceNotification(allianceId, NetMessageUtil.convertAllianceEvent(ae).build());
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
		broadcasetAllianceNotification(allianceId, NetMessageUtil.convertAllianceEvent(ae).build());
	}
	
	private static void rejectRoleJoinInAlliace(String allianceId, String roleId, String roleName, String byRoleId, String byRoleName) {
		AllianceEvent ae = factoryAllianceEvent(allianceId,GameConst.ALLIANCE_EVENT_REJECT_JOIN_IN,roleId,roleName);
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_ID, byRoleId);
		ae.setParameter(GameConst.ALLIANCE_PARA_ACTION_BY_ROLE_NAME, byRoleName);
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
		broadcasetAllianceNotification(allianceId, NetMessageUtil.convertAllianceEvent(ae).build());
	}
	
	private static void allowRoleJoinInAlliace(String allianceId, String roleId, String roleName) {
		AllianceMember am = factoryAllianceMember(allianceId, roleId, AllianceMemberTitle.Member);
		DaoFactory.getInstance().getAllianceDao().createAllianceMember(am);
		AllianceEvent ae = factoryAllianceEvent(allianceId,GameConst.ALLIANCE_EVENT_JOIN_IN_ALLIANCE,roleId,roleName);
		DaoFactory.getInstance().getAllianceDao().createAllianceEvent(ae);
		broadcasetAllianceNotification(allianceId, NetMessageUtil.convertAllianceEvent(ae).build());
	}
	
	private static void broadcasetAllianceNotification(String allianceId, GeneratedMessage event) {
		List<AllianceMember> members = DaoFactory.getInstance().getAllianceDao().getAllianceMembers(allianceId);
		for(AllianceMember am : members){
//			Role role = DaoFactory.getInstance().getUserDao().getRole(am.getRoleId());
//			User user = DaoFactory.getInstance().getUserDao().getUserById(am.getRoleId());
			UserOnlineInfo userOnlineInfo = DaoFactory.getInstance().getUserDao().getUserOnlineInfo(am.getRoleId());
			if(userOnlineInfo!=null) {
				I_RouteMessage.Builder req = NetMessageUtil.buildRouteMessage(new NetCmd(event), userOnlineInfo.getLoginServerId(), false, am.getRoleId(), ServerContext.getInstance().getServerUniqueTag());
				RouterService.routeToTarget(req.build());
			}
		}
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
