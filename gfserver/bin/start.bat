start java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/log_logback.xml -server -Xms64M -Xmx128M -cp PardServerCR-1.0-SNAPSHOT.jar game.logserver.LogServer
start java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/login_logback.xml -server -Xms64M -Xmx128M -cp PardServerCR-1.0-SNAPSHOT.jar game.loginserver.LoginServer
start java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/zone_logback.xml -server -Xms64M -Xmx256M -cp PardServerCR-1.0-SNAPSHOT.jar game.zoneserver.ZoneServer
start java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/game_logback.xml -server -Xms64M -Xmx256M -cp PardServerCR-1.0-SNAPSHOT.jar game.gameserver.GameServer
start java -Duser.timezone=Asia/Shanghai -Dlogback.configurationFile=resources/fight_logback.xml -server -Xms64M -Xmx256M -cp PardServerCR-1.0-SNAPSHOT.jar game.fightserver.FightServer
pause