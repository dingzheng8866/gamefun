package com.tiny.game.common.server.fight.bizlogic;

public class StageWeightConfig {
	public int id;

	/** 产兵速度 */
	public float produceSpeed = 1;
	/** 升级时间倍数 */
	public float uplevelTime = 1;
	/** 势力经验乘的倍数 */
	public float legionExpMultiply = 1f;
	/** 技能生产速度 */
	public float skillProduceSpeed = 1f;
	/** 移动速度 */
	public float moveSpeed = 1f;

	public float ruinPhase1Time = 1;
	public float ruinPhase2Time = 1;
	public float changeBuildTime = 1;
	
	public int firstProduceSkill = 0;
	
	public int maxSendArmy = 5;
	
	public int enableNeutralOrbitAttack = 0;
	public int maxBotHP = 0;
	public float playerMaxMage = 0;
	
	public int summonBuildId = 0;
	
	public float minTimePercent = 0.75f;
	public float maxTimePercent = 1.25f;
	
	public boolean IsHaveSommonBuild() {
		return this.summonBuildId > 0;
	}
	
	public int userActionTriggerBotFlag = 1;
}
