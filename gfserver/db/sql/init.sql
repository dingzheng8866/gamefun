drop database if exists gamefun;
create database gamefun;
use gamefun;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` varchar(128) NOT NULL,
  `loginAccountId` varchar(128) NOT NULL,
  `loginDeviceId` varchar(128) DEFAULT NULL,
  `loginIp` varchar(50) DEFAULT NULL,
  `channel` varchar(128) DEFAULT NULL,
  `platform` varchar(128) DEFAULT NULL,
  `platformAccountId` varchar(128) DEFAULT NULL,
  `platformAccountPassword` varchar(128) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `lastUpdateTime` datetime DEFAULT NULL,
  `loginDeviceInfo` text,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_bind`;
CREATE TABLE `user_bind` (
  `bindId` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `lastUpdateTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`bindId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_online`;
CREATE TABLE `user_online` (
  `userId` varchar(128) NOT NULL,
  `loginServerId` varchar(128) NOT NULL,
  `lastUpdateTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `roleId` varchar(128) NOT NULL,
  `roleData` mediumblob NULL DEFAULT NULL,
  `lastUpdateTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



