
DROP KEYSPACE "gamefun";

CREATE KEYSPACE if not exists gamefun WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};

CREATE TABLE if not exists gamefun.user (userId text,loginAccountId text,loginDeviceId text,loginIp text,channel text,platform text,platformAccountId text,platformAccountPassword text,createTime timestamp,lastUpdateTime timestamp,loginDeviceInfo text,PRIMARY KEY (userId));

CREATE TABLE if not exists gamefun.user_bind (bindId text,userId text,lastUpdateTime timestamp,PRIMARY KEY (bindId));

CREATE TABLE if not exists gamefun.user_online (userId text,loginServerId text,lastUpdateTime timestamp,PRIMARY KEY (userId));

CREATE TABLE if not exists gamefun.role (roleId text,roleData blob,lastUpdateTime timestamp,PRIMARY KEY (roleId));


CREATE TABLE if not exists gamefun.alliance (id text,name text,description text,location int,joinType int,joinNeedPrize int,fightRate int,publicFightLog int,level int,maxMemebers int,lastUpdateTime timestamp,PRIMARY KEY (id));
CREATE INDEX IF NOT EXISTS ON gamefun.alliance (name);

CREATE TABLE if not exists gamefun.alliance_memeber (allianceId text,roleId text,title int, donated int, lastUpdateTime timestamp,PRIMARY KEY (roleId));
CREATE INDEX IF NOT EXISTS ON gamefun.alliance_memeber (allianceId);

CREATE TABLE if not exists gamefun.alliance_event (allianceId text,eventId text, allianceEventType int,lastUpdateTime timestamp,parameters blob,PRIMARY KEY (allianceId, eventId));

