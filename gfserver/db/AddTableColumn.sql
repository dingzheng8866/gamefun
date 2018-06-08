alter table role_base add rank2v2_game_times int NOT NULL;
alter table role_base add placement_prize int NOT NULL;
alter table role_statistic add placement_streak_victory int NOT NULL;

alter table role_card add energyStarOpen varchar(20) default '10000000';
alter table role_base add energystar_keys int NOT NULL;

alter table role_base add teamIndex int NOT NULL default 1;

ALTER TABLE `role_base` 
CHANGE COLUMN `dungeon_star_reward` `dungeon_star_reward` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '领取的副本星级奖励' ;

ALTER TABLE `role_dungeon` 
CHANGE COLUMN `chapterId` `stageId` INT(11) NOT NULL DEFAULT '0' COMMENT '副本章节ID' ;

alter table role_base add is_step_placement_des int NOT NULL;
alter table role_base add placement_win_num int NOT NULL;
alter table role_base add placement_lose_num int NOT NULL;

alter table role_base add team_create_invited_blob mediumblob;

alter table role_base add haveNewTeamInvitedInfo int NOT NULL default 0;

alter table role_base add isTeamApplyAgree int NOT NULL default 0;

alter table role_base add isFinishCreateTeamUpdateTask int NOT NULL default 0;

alter table role_base add rb2v2num_af_pl int NOT NULL default 0;

alter table role_base add prize_2v2_rank int NOT NULL default 0;

alter table role_statistic add rank_2v2_streak_victory int NOT NULL;

alter table role_base add team_state int NOT NULL default 0;

/*2018.3.23*/
ALTER TABLE `kcj`.`role_base` 
ADD COLUMN `daily_activity` INT(11) NULL DEFAULT '0' AFTER `point`,
ADD COLUMN `weekly_activity` INT(11) NULL DEFAULT '0' AFTER `daily_activity`;

/*2018.3.26*/
alter table `kcj`.`role_mail`
add column `GmMailId` bigint default '0',
add column `mailEffectiveTime` bigint default null;

/*2018.5.10 cyd*/
alter table role_card add skill_level int NOT NULL default 1;

/*2018.5.18 cyd*/
alter table role_base add isAcceptStrangeInvite int NOT NULL default 1;

/*2018.5.23 xyz*/
alter table user add login_ip varchar(128) DEFAULT NULL;