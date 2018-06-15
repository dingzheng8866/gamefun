
DROP KEYSPACE "gamefun";

CREATE KEYSPACE if not exists gamefun WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};

CREATE TABLE if not exists gamefun.user (userId text,loginAccountId text,loginDeviceId text,loginIp text,channel text,platform text,platformAccountId text,platformAccountPassword text,createTime timestamp,lastUpdateTime timestamp,loginDeviceInfo text,PRIMARY KEY (userId));

CREATE TABLE if not exists gamefun.user_bind (bindId text,userId text,lastUpdateTime timestamp,PRIMARY KEY (bindId));

CREATE TABLE if not exists gamefun.user_online (userId text,loginServerId text,lastUpdateTime timestamp,PRIMARY KEY (userId));

CREATE TABLE if not exists gamefun.role (roleId text,roleData blob,lastUpdateTime timestamp,PRIMARY KEY (roleId));



