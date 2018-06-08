
CREATE TABLE `t_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` int(10) unsigned NOT NULL,
  `type` varchar(32) DEFAULT '\0' COMMENT '统计类型',
  `content` varchar(2048) DEFAULT '\0' COMMENT '内容',
  PRIMARY KEY (`id`),
  KEY `info` (`type`,`time`)
) ENGINE=InnoDB AUTO_INCREMENT=1303 DEFAULT CHARSET=utf8;

CREATE TABLE `t_online` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(32) DEFAULT '\0',
  `time` int(11) unsigned NOT NULL,
  `svr_id` int(11) unsigned NOT NULL,
  `count` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `info` (`type`,`time`,`svr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=195 DEFAULT CHARSET=utf8;

CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT '\0',
  `content` varchar(2048) DEFAULT '\0',
  PRIMARY KEY (`id`),
  KEY `info` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

CREATE TABLE `t_role` (
  `role_id` int(11) unsigned NOT NULL,
  `content` varchar(2048) DEFAULT '\0',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

