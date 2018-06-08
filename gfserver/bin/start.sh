#!/bin/bash

if [ "$1" = "test" ]
then
    TIME_OFFSET_PARAM='-agentpath:./libfaketime -XX:+UnlockDiagnosticVMOptions -XX:DisableIntrinsic=_currentTimeMillis -XX:CompileCommand=quiet -XX:CompileCommand=exclude,java/lang/System.currentTimeMillis'
fi

nohup java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/log_logback.xml $TIME_OFFSET_PARAM -server -Xms64M -Xmx512M -cp PardServerCR-1.0-SNAPSHOT.jar game.logserver.LogServer > /dev/null 2>&1 &
nohup java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/login_logback.xml $TIME_OFFSET_PARAM -server -Xms64M -Xmx512M -cp PardServerCR-1.0-SNAPSHOT.jar game.loginserver.LoginServer > /dev/null 2>&1 &
nohup java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/zone_logback.xml $TIME_OFFSET_PARAM -server -Xms64M -Xmx1024M -cp PardServerCR-1.0-SNAPSHOT.jar game.zoneserver.ZoneServer > /dev/null 2>&1 &
nohup java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/game_logback.xml $TIME_OFFSET_PARAM -server -Xms64M -Xmx2048M -cp PardServerCR-1.0-SNAPSHOT.jar game.gameserver.GameServer > /dev/null 2>&1 &
nohup java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/fight_logback.xml $TIME_OFFSET_PARAM -server -Xms64M -Xmx1024M -cp PardServerCR-1.0-SNAPSHOT.jar game.fightserver.FightServer > /dev/null 2>&1 &
