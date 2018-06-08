-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: kcj
-- ------------------------------------------------------
-- Server version	5.7.19-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `payment_order`
--

DROP TABLE IF EXISTS `payment_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_order` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `serverId` int(11) NOT NULL,
  `orderId` varchar(128) DEFAULT NULL,
  `platform` varchar(128) DEFAULT NULL,
  `cash` int(11) DEFAULT '0' COMMENT '充值金额',
  `money` int(11) DEFAULT '0' COMMENT '获得游戏币',
  `pay_status` tinyint(4) DEFAULT '0' COMMENT '0.失败 1.成功',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '充值时间',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `idx_roleId_orderId` (`roleId`,`orderId`),
  KEY `time_platform` (`pay_time`,`platform`)
) ENGINE=InnoDB AUTO_INCREMENT=999 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_activity`
--

DROP TABLE IF EXISTS `role_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_activity` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `activityId` int(11) NOT NULL,
  `status_value1` int(11) DEFAULT '0' COMMENT '状态值1',
  `status_value2` int(11) DEFAULT '0' COMMENT '状态值2',
  `status_value3` int(11) DEFAULT '0' COMMENT '状态值3',
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=999 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_base`
--

DROP TABLE IF EXISTS `role_base`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_base` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL,
  `gsId` int(11) DEFAULT NULL,
  `platform` varchar(128) DEFAULT NULL,
  `channel` int(11) DEFAULT '0' COMMENT '所在平台: 0.android 1.ios',
  `leagueId` int(11) DEFAULT '0' COMMENT '公会ID.默认0表示没有加入公会',
  `league_contestId` int(11) DEFAULT '0' COMMENT '公会联赛ID.默认0:没有联赛 1.青铜联赛 2.白银联赛 3.黄金联赛 4.铂金联赛 5.钻石联赛 6.大师联赛 7.王者联赛',
  `teamId` int(11) DEFAULT '0' COMMENT '战队ID.默认0表示没有加入战队',
  `icon` int(11) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `level` smallint(6) DEFAULT NULL COMMENT '等级',
  `level_exp` int(11) DEFAULT NULL COMMENT '经验',
  `coins` int(11) DEFAULT '0',
  `money` int(11) DEFAULT '0',
  `payment_money` int(11) DEFAULT '0' COMMENT '充值元宝',
  `cash` int(11) DEFAULT '0' COMMENT '充值金额',
  `prize` int(11) DEFAULT '0' COMMENT '奖杯',
  `honor` int(11) DEFAULT '0' COMMENT '奖章',
  `double_honor` int(11) DEFAULT '0' COMMENT '团章',
  `point` int(11) DEFAULT '0' COMMENT '2v2积分',
  `daily_activity` INT(11) DEFAULT '0' COMMENT '日活跃度',
  `weekly_activity` INT(11) DEFAULT '0' COMMENT '周活跃度',
  `excite` int(11) DEFAULT '1' COMMENT '兴奋点',
  `excite_2v2` int(11) DEFAULT '1' COMMENT '2v2兴奋点',
  `stage_key` int(11) DEFAULT '3' COMMENT '关卡钥匙',
  `skin_money` int(11) DEFAULT '0' COMMENT '皮肤货币',
  `skin_chip` int(11) DEFAULT '0' COMMENT '皮肤碎片',
  `new_guide_step` int(11) DEFAULT '0' COMMENT '新手引导步骤1-8',
  `other_guide_setp` varchar(256) NOT NULL DEFAULT '' COMMENT '引导步骤',
  `invite_roleId` int(11) DEFAULT '0' COMMENT '邀请玩家ID',
  `status_mark` int(11) DEFAULT '0' COMMENT '状态标示位:1.是否改名  2.引导礼包是否领取  3.引导是否非完胜  4.是否完成首充  5.是否引导解锁  6.是否禁言 7.是否禁号',
  `battle_team_blob` mediumblob COMMENT '出战卡组列表',
  `attend_league_time` timestamp NULL DEFAULT NULL COMMENT '加入联盟时间',
  `upgrade_time` datetime DEFAULT NULL COMMENT '上次更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `location` varchar(64) NOT NULL DEFAULT '' COMMENT '位置信息',
  `descSelf` varchar(128) NOT NULL DEFAULT '' COMMENT '描述',
  `sex` int(11) NOT NULL DEFAULT '0' COMMENT '性别',
  `show_location` int(11) NOT NULL DEFAULT '0' COMMENT '是否显示位置',
  `update_key_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '钥匙回复时间',
  `rune_chip` int(11) NOT NULL DEFAULT '0' COMMENT '符文碎片',
  `rune_cur_page` int(11) NOT NULL DEFAULT '0' COMMENT '当前符文页',
  `rune_buy_pos` blob COMMENT '已经购买的符文位置',
  `rune_coin_lottery` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '符文铜钱抽奖次数',
  `rune_money_lottery` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '符文元宝抽奖次数',
  `rune_free_lottery_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '符文免费抽奖时间',
  `daily_reset_time` timestamp NULL DEFAULT NULL COMMENT '每日数据重置时间',
  `dungeon_star_reward` varchar(256) NOT NULL DEFAULT '' COMMENT '领取的副本星级奖励',
  `menu_opens_mark` int(11) DEFAULT '0' COMMENT '模块功能开启列表',
  `bind_socialId` int(11) DEFAULT '0' COMMENT '绑定社交平台ID',
  `rune_free_coin_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '符文银币免费抽奖时间',
  `rune_free_coin_count` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '符文银币免费抽奖次数',
  `rank2v2_game_times` int(11) NOT NULL,
  `placement_prize` int(11) NOT NULL,
  `energystar_keys` int(11) NOT NULL,
  `teamIndex` int(11) NOT NULL DEFAULT '1',
  `is_step_placement_des` int(11) NOT NULL,
  `placement_win_num` int(11) NOT NULL,
  `placement_lose_num` int(11) NOT NULL,
  `team_create_invited_blob` mediumblob COMMENT '战队邀请信息',
  `haveNewTeamInvitedInfo` int(11) NOT NULL COMMENT '是否有战队创建新申请信息',
  `isTeamApplyAgree` int(11) NOT NULL COMMENT '战队申请是否被同意',
  `isFinishCreateTeamUpdateTask` int(11) NOT NULL,
  `rb2v2num_af_pl` int(11) NOT NULL COMMENT '定位赛结束后进行的2v2排位赛的场次',
  `prize_2v2_rank` int(11) NOT NULL COMMENT '2v2排位赛奖杯',
  `team_state` int(11) NOT NULL COMMENT '战队状态',
  `isAcceptStrangeInvite` int(11) NOT NULL COMMENT '是否接受陌生人邀请',
  
	
  PRIMARY KEY (`Id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=2001 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_card`
--

DROP TABLE IF EXISTS `role_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_card` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) DEFAULT NULL,
  `cardId` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `baseId` int(11) DEFAULT NULL,
  `energyStarOpen` varchar(20) DEFAULT '10000000',
  `skill_level` int(11) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `idx_roleId_cardId` (`roleId`,`cardId`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_chest`
--

DROP TABLE IF EXISTS `role_chest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_chest` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) DEFAULT NULL,
  `online_chest_count` int(11) DEFAULT NULL COMMENT '在线奖励宝箱数量',
  `online_chest_countdown_time` timestamp NULL DEFAULT NULL COMMENT '在线奖励宝箱倒计时起始时间',
  `star_chest_starcount` int(11) DEFAULT NULL COMMENT '星星宝箱星星数量',
  `star_chest_isopen` tinyint(4) DEFAULT NULL COMMENT '星星宝箱是否开启',
  `double_star_chest_starcount` int(11) DEFAULT NULL COMMENT '多人星星宝箱星星数量',
  `double_star_chest_isopen` tinyint(4) DEFAULT NULL COMMENT '多人星星宝箱是否开启',
  `reset_time` timestamp NULL DEFAULT NULL COMMENT '重置时间',
  `drop_chest_blob` mediumblob COMMENT '战斗掉落宝箱',
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=3234 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_customicon`
--

DROP TABLE IF EXISTS `role_customicon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_customicon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customIcon` mediumblob,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000001 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_dungeon`
--

DROP TABLE IF EXISTS `role_dungeon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_dungeon` (
  `roleId` int(11) NOT NULL,
  `stageId` int(11) NOT NULL DEFAULT '0' COMMENT '副本章节ID',
  `star` int(11) NOT NULL DEFAULT '0' COMMENT '星星',
  `reward_1` int(11) NOT NULL DEFAULT '0' COMMENT '奖励1',
  `reward_2` int(11) NOT NULL DEFAULT '0' COMMENT '奖励2',
  `reward_3` int(11) NOT NULL DEFAULT '0' COMMENT '奖励3',
  PRIMARY KEY (`roleId`,`stageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_friend`
--

DROP TABLE IF EXISTS `role_friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_friend` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `friends_blob` mediumblob COMMENT '好友数据:关注列表,粉丝列表,黑名单列表',
  `teams_blob` mediumblob COMMENT '战队数据:邀请列表,关注列表',
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=999 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_invite`
--

DROP TABLE IF EXISTS `role_invite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_invite` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `daily_click_count` int(11) DEFAULT '0' COMMENT '本日邀请点击人次',
  `daily_active_count` int(11) DEFAULT '0' COMMENT '本日邀请活跃人次',
  `daily_reward_money` int(11) DEFAULT '0' COMMENT '本日邀请点击奖励元宝数量',
  `weekly_reward_money` int(11) DEFAULT '0' COMMENT '本周邀请点击奖励元宝数量',
  `daily_total_money` int(11) DEFAULT '0' COMMENT '本日总共奖励元宝数量',
  `total_drawable_money` int(11) DEFAULT '0' COMMENT '累计可领取元宝数量',
  `total_reward_money` int(11) DEFAULT '0' COMMENT '累计已领取元宝数量',
  `daily_reset_time` timestamp NULL DEFAULT NULL COMMENT '每日重置时间',
  `weekly_reset_time` timestamp NULL DEFAULT NULL COMMENT '每周重置时间',
  `invite_blob` mediumblob COMMENT '邀请列表',
  `daily_mask` int(11) DEFAULT '0' COMMENT '今日标记 1 分享社交奖励是否领取',
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=999 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_mail`
--

DROP TABLE IF EXISTS `role_mail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_mail` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `mailId` int(11) DEFAULT NULL COMMENT '邮件模板ID',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `is_read` tinyint(4) DEFAULT '0' COMMENT '邮件是否已读：0否，1是',
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `attach` mediumblob COMMENT '附件',
  `GmMailId` bigint default '0' comment 'GM发送的邮件ID',
  `mailEffectiveTime` bigint default null comment '邮件有效时间',
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=999 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_reinforce`
--

DROP TABLE IF EXISTS `role_reinforce`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_reinforce` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `donate_card_value` int(11) DEFAULT NULL COMMENT '捐赠卡牌价值',
  `ask_reinforce_time` timestamp NULL DEFAULT NULL COMMENT '请求卡牌捐赠时间',
  `reset_time` timestamp NULL DEFAULT NULL COMMENT '重置时间',
  `offline_donate_blob` mediumblob COMMENT '离线卡牌捐赠',
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=999 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_rune`
--

DROP TABLE IF EXISTS `role_rune`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_rune` (
  `roleId` int(11) unsigned NOT NULL,
  `runeId` int(11) unsigned NOT NULL,
  `count` int(11) unsigned NOT NULL,
  KEY `info` (`roleId`,`runeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_rune_page`
--

DROP TABLE IF EXISTS `role_rune_page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_rune_page` (
  `roleId` int(11) unsigned NOT NULL,
  `pageId` int(11) unsigned NOT NULL,
  `name` varchar(32) DEFAULT '\0',
  `content` blob,
  KEY `info` (`roleId`,`pageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_shop`
--

DROP TABLE IF EXISTS `role_shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_shop` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `refresh_time` timestamp NULL DEFAULT NULL COMMENT '刷新时间',
  `sell_items_blob` mediumblob COMMENT '商店出售物品列表',
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=777 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_sign`
--

DROP TABLE IF EXISTS `role_sign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_sign` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `sign_day` int(11) DEFAULT NULL COMMENT '签到天数',
  `last_sign_time` timestamp NULL DEFAULT NULL COMMENT '最新签到时间',
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_soldier`
--

DROP TABLE IF EXISTS `role_soldier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_soldier` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) DEFAULT NULL,
  `soldierId` int(11) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `idx_roleId_soldierId` (`roleId`,`soldierId`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=870 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_statistic`
--

DROP TABLE IF EXISTS `role_statistic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_statistic` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `total_login_days` int(11) DEFAULT '1' COMMENT '累计登录天数',
  `max_prize` int(11) DEFAULT '0' COMMENT '最高奖杯数',
  `arena_fight_win` int(11) DEFAULT '0' COMMENT '竞技场胜利场数',
  `arena_fight_win_today` int(11) DEFAULT '0' COMMENT '今日胜利场次',
  `arena_perfect_win` int(11) DEFAULT '0' COMMENT '竞技场三星胜利场数',
  `arena_fight_count` int(11) DEFAULT '0' COMMENT '竞技场战斗场数',
  `arena_streak_victory` int(11) DEFAULT '0' COMMENT '竞技场连胜场次(为负数表示连败)',
  `often_battle_card` int(11) DEFAULT '0' COMMENT '近期常使用卡牌',
  `total_card_count` int(11) DEFAULT '0' COMMENT '拥有卡牌数量',
  `donate_card_count` int(11) DEFAULT '0' COMMENT '捐赠卡牌数量',
  `nodrop_newcard_count` int(11) DEFAULT '0' COMMENT '连续不掉落新卡数量',
  `nodrop_legendcard_count` int(11) DEFAULT '0' COMMENT '连续不掉落传奇卡数量',
  `nodrop_purplecard_count` int(11) DEFAULT '0' COMMENT '连续不掉落紫卡数量',
  `arena_exp_today` int(11) DEFAULT '0' COMMENT '竞技场今日获取的经验',
  `arena_coin_today` int(11) DEFAULT '0' COMMENT '竞技场今日获取的铜钱',
  `arena_honor_today` int(11) DEFAULT '0' COMMENT '竞技场今日获取的功勋',
  `arena_droprune_today` int(11) DEFAULT '0' COMMENT '竞技场今日获取的符文数',
  `like_count_today` int(11) DEFAULT '0' COMMENT '今日喜欢次数',
  `kickleague_honor_today` int(11) DEFAULT '0' COMMENT '诸侯争霸今日获取的功勋',
  `costmoney_honor_today` int(11) DEFAULT '0' COMMENT '消耗元宝今日获取的功勋',
  `shop_hugechest_buy_today` int(11) DEFAULT '0' COMMENT '商店超大宝箱今日购买次数',
  `shop_legendchest_buy_today` int(11) DEFAULT '0' COMMENT '商店传奇宝箱今日购买次数',
  `shop_magicchest_buy_today` int(11) DEFAULT '0' COMMENT '商店魔法宝箱今日购买次数',
  `last_arena_stage` int(11) DEFAULT '0' COMMENT '上次随机到的竞技场关卡ID',
  `reset_time` timestamp NULL DEFAULT NULL COMMENT '重置时间',
  `card_battlecount_blob` mediumblob COMMENT '出战卡牌统计',
  `placement_streak_victory` int(11) DEFAULT '0' COMMENT '定位赛连胜连负场次',
  `rank_2v2_streak_victory` int(11) DEFAULT '0' COMMENT '定位赛之后2v2排位连胜连负场次',
	
  PRIMARY KEY (`Id`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=999 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_task`
--

DROP TABLE IF EXISTS `role_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_task` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `taskId` int(11) DEFAULT NULL,
  `task_type` int(11) DEFAULT NULL,
  `task_condition` int(11) DEFAULT NULL,
  `task_progress` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '0' COMMENT '0:未开放  1:进行中  2.完成未领奖  3.完成已领奖',
  `reset_time` timestamp NULL DEFAULT NULL COMMENT '重置时间',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `idx_roleId_taskId` (`roleId`,`taskId`),
  KEY `roleId` (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` varchar(128) DEFAULT NULL COMMENT '设备Mac地址',
  'login_ip' varchar(128) DEFAULT NULL COMMENT '登录IP地址',
  `platform` varchar(128) DEFAULT NULL COMMENT '渠道名称',
  `channel` int(11) DEFAULT '0' COMMENT '所在平台: 0.android 1.ios',
  `pt_account` varchar(128) DEFAULT NULL COMMENT '渠道账号',
  `mb_account` varchar(128) DEFAULT NULL COMMENT '官方账号',
  `mb_pwd` varchar(128) DEFAULT NULL COMMENT '官方密码',
  `device_info` text COMMENT '设备信息',
  `gsId` int(11) DEFAULT NULL COMMENT '逻辑服ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=2001 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-12-29 18:01:46
