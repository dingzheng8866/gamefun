package com.tiny.game.common.server.fight.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.server.fight.bizlogic.FightContext;
import com.tiny.game.common.server.fight.bizlogic.FightRole;
import com.tiny.game.common.server.fight.bizlogic.FightRoleStatus;
import com.tiny.game.common.server.fight.bizlogic.FightStage;
import com.tiny.game.common.server.fight.bizlogic.SurrenderGroup;

import game.protocol.protobuf.FightProtocol.C_WarFightAction;

public class FightRoom {

	private static final Logger logger = LoggerFactory.getLogger(FightRoom.class);
	
	private int roomId;
	private Map<String, FightRole> players = new ConcurrentHashMap<String, FightRole>();
	private List<FightRole> playerList = new ArrayList<FightRole>();
	private FightContext ctx;
	
	private FightRoomState state = new FightRoomStateWaiting(this);
	
	public FightRoom(int roomId, List<FightRole> fightRoles, FightContext ctx) {
		this.roomId = roomId;
		this.playerList = fightRoles;
		this.ctx = ctx;
		for(FightRole fr : fightRoles) {
			players.put(fr.getRoleId(), fr);
		}
		//war = new FightStage(this);
	}
	
	public int getRoomId() {
		return roomId;
	}
	
	public FightRoomState getRoomState(){
		return state;
	}
	
	public List<FightRole> getPlayers(){
		return playerList;
	}
	
	public FightRole getPlayer(String roleId) {
		return players.get(roleId);
	}
	
	public void changeState(FightRoomState state) {
		if(this.state.getClass() == state.getClass()) {
			logger.error("Invalid to change to same state: " + state.getClass().getSimpleName());
		}
		if(logger.isInfoEnabled()) {
			logger.info("Room: " + getRoomId() + " change state " + this.state.getState() + " ==> " + state.getState());
		}
		this.state = state;
	}
	
	public synchronized boolean update() {
		return state.update();
	}
	
	public synchronized void onPlayerEnter(String roleId) {
		state.onPlayerEnter(roleId);
	}

	public synchronized void onPlayerLoad(String roleId, int progress) {
		state.onPlayerLoad(roleId, progress);
	}

	public synchronized void onPlayerDisconnect(String roleId) {
		state.onPlayerDisconnect(roleId);
	}
	
	public synchronized void onPlayerReconnect(String roleId) {
		state.onPlayerReconnect(roleId);
	}
	
	public void broadcastMessage(GeneratedMessage msg) {
		for (FightRole player : playerList) {
			if (!player.isRobot() && (player.getStatus() != FightRoleStatus.OFFLINE)) {
				if (logger.isInfoEnabled()) {
					logger.info("Room {} broadcast message {} to roleId={}", roomId, msg.getClass().getSimpleName(), player.getRoleId());
				}
				NetLayerManager.getInstance().asyncSendOutboundMessage(player.getSession(), msg);
			}
		}
	}
	
}
