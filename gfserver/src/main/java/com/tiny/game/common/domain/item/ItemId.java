package com.tiny.game.common.domain.item;

import com.tiny.game.common.exception.InternalBugException;

public enum ItemId {

    roleLevel(100000),
    roleExp(100001),
    systemDonateGold(100002),
    buyGold(100003),
    systemDonateGem(100004),
    buyGem(100005),
    monthCard(100006),
    seasonCard(100007),
    yearCard(100008),
    permanentCard(100009),
    doubleRoleExp(100010),
    doubleRoleGold(100011),
    speedTimeFiveMin(100012),
    speedTimeTenMin(100013),
    speedTimeThirtyMin(100014),
    speedTimeOneHour(100015),
    speedTimeTwoHour(100016),
    speedTimeSixHour(100017),
    speedTimeTwelveHour(100018),
    speedTimeOneDay(100019),
    itemBox(100020),
    mainBase(200001),
    defenseTowerLeft(200002),
    defenseTowerRight(200003),
    wishTower(200004),
    allianceTower(200005),
    materialWorkshop(200006),
    researchCenter(200007),
    barbarian(300001),
    archer(300002),
    giant(300003),
    wizard(300004),
    bomber(300005),
    angel(300006),
    armorman(300007),
    stoneman(300008),
    barbarianKing(300009),
    archerKing(300010),
    skeletons(300011),
    skeletonsKing(300012),
    riderman(300013),
    darkriderman(300014),
    beeman(300015),
    beemanKing(300016),
    babyDragon(300017),
    fireDragon(300018),
    ballonman(300019),
    witch(300020),
    steelman(300021),
    iceSpirit(300022),
    iceWizard(300023),
    lightningWizard(300024),
    wolfman(300025),
    wolfmanKing(300026),
    whirlwindman(300027),
    shockworker(300028),
    redspiderman(300029),
    greenspiderman(300030),
    ghostPrince(300031),
    ghostRiderman(300032),
    ghostGunner(300033),
    magicRage(300034),
    magicHeal(300035),
    magicLightning(300036),
    magicPoison(300037),
    magicArrows(300038),
    magicClone(300039),
    magicTornado(300040),
    magicIce(300041),
    magicFireball(300042),
    magicFireBurn(300043),
    magicMissile(300044),
    magicSignal(300045),
    bomb(300046),
    superBomb(300047),
    spring(300048),
    superSpring(300049),
    barbarianHouse(300050),
    archerHouse(300051),
    bomberTower(300052),
    wizardTower(300053),
    archerTower(300054),
    skeletonsHouse(300055),
    workerHouse(300056),
    mortarTower(300057),
    teslaTower(300058),
    balloonHouse(300059),
    blueDiamond(600001),
    redDiamond(600002),
    purpleDiamond(600003),
    goldDiamond(600004),
    darkGoldDiamond(600005),
    musicSwitchFlag(700001),
    audioEffectSwitchFlag(700002),
    languageOption(700003);
	
	private int value = 0;
	private ItemId(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	
	public static ItemId valueOf(int val) {
		switch (val) {
        case 100000: return roleLevel;
        case 100001: return roleExp;
        case 100002: return systemDonateGold;
        case 100003: return buyGold;
        case 100004: return systemDonateGem;
        case 100005: return buyGem;
        case 100006: return monthCard;
        case 100007: return seasonCard;
        case 100008: return yearCard;
        case 100009: return permanentCard;
        case 100010: return doubleRoleExp;
        case 100011: return doubleRoleGold;
        case 100012: return speedTimeFiveMin;
        case 100013: return speedTimeTenMin;
        case 100014: return speedTimeThirtyMin;
        case 100015: return speedTimeOneHour;
        case 100016: return speedTimeTwoHour;
        case 100017: return speedTimeSixHour;
        case 100018: return speedTimeTwelveHour;
        case 100019: return speedTimeOneDay;
        case 100020: return itemBox;
        case 200001: return mainBase;
        case 200002: return defenseTowerLeft;
        case 200003: return defenseTowerRight;
        case 200004: return wishTower;
        case 200005: return allianceTower;
        case 200006: return materialWorkshop;
        case 200007: return researchCenter;
        case 300001: return barbarian;
        case 300002: return archer;
        case 300003: return giant;
        case 300004: return wizard;
        case 300005: return bomber;
        case 300006: return angel;
        case 300007: return armorman;
        case 300008: return stoneman;
        case 300009: return barbarianKing;
        case 300010: return archerKing;
        case 300011: return skeletons;
        case 300012: return skeletonsKing;
        case 300013: return riderman;
        case 300014: return darkriderman;
        case 300015: return beeman;
        case 300016: return beemanKing;
        case 300017: return babyDragon;
        case 300018: return fireDragon;
        case 300019: return ballonman;
        case 300020: return witch;
        case 300021: return steelman;
        case 300022: return iceSpirit;
        case 300023: return iceWizard;
        case 300024: return lightningWizard;
        case 300025: return wolfman;
        case 300026: return wolfmanKing;
        case 300027: return whirlwindman;
        case 300028: return shockworker;
        case 300029: return redspiderman;
        case 300030: return greenspiderman;
        case 300031: return ghostPrince;
        case 300032: return ghostRiderman;
        case 300033: return ghostGunner;
        case 300034: return magicRage;
        case 300035: return magicHeal;
        case 300036: return magicLightning;
        case 300037: return magicPoison;
        case 300038: return magicArrows;
        case 300039: return magicClone;
        case 300040: return magicTornado;
        case 300041: return magicIce;
        case 300042: return magicFireball;
        case 300043: return magicFireBurn;
        case 300044: return magicMissile;
        case 300045: return magicSignal;
        case 300046: return bomb;
        case 300047: return superBomb;
        case 300048: return spring;
        case 300049: return superSpring;
        case 300050: return barbarianHouse;
        case 300051: return archerHouse;
        case 300052: return bomberTower;
        case 300053: return wizardTower;
        case 300054: return archerTower;
        case 300055: return skeletonsHouse;
        case 300056: return workerHouse;
        case 300057: return mortarTower;
        case 300058: return teslaTower;
        case 300059: return balloonHouse;
        case 600001: return blueDiamond;
        case 600002: return redDiamond;
        case 600003: return purpleDiamond;
        case 600004: return goldDiamond;
        case 600005: return darkGoldDiamond;
        case 700001: return musicSwitchFlag;
        case 700002: return audioEffectSwitchFlag;
        case 700003: return languageOption;


		}
		throw new InternalBugException();
	}	
}
