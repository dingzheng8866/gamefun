#!/bin/bash

if [ "$1" = "test" ]
then
    TIME_OFFSET_PARAM='-agentpath:./libfaketime -XX:+UnlockDiagnosticVMOptions -XX:DisableIntrinsic=_currentTimeMillis -XX:CompileCommand=quiet -XX:CompileCommand=exclude,java/lang/System.currentTimeMillis'
fi

#nohup java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/log_logback.xml $TIME_OFFSET_PARAM -server -Xms64M -Xmx512M -cp GameServerCR-1.0-SNAPSHOT.jar com.tiny.game.common.server.gate.GateServer > /dev/null 2>&1 &
java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/log_logback.xml $TIME_OFFSET_PARAM -server -Xms512M -Xmx512M -cp GameServerCR-1.0-SNAPSHOT.jar com.tiny.game.common.server.gate.GateServer
