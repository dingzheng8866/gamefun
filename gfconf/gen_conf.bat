@echo off

SET GAME_C_ROOT=%~sdp0\..\gfclient
SET GAME_S_ROOT=%~sdp0\..\gfserver

for %%i in (conf\*.xlsx) do (
  echo Converting %%i to %GAME_C_ROOT%\Assets\Game\Config\%%~ni.csv...
  rem xlsx2csv\xlsx2csv -i -d ; -s 1 %%i %GAME_C_ROOT%\GameCR\Assets\Game\Config\%%~ni.csv
  echo Converting %%i to %GAME_S_ROOT%\resources\config\%%~ni.csv...
  xlsx2csv\xlsx2csv -i -d ; -s 1 %%i %GAME_S_ROOT%\resources\config\%%~ni.csv
)

for %%i in (conf\*.json) do (
  echo Copying %%i to %GAME_C_ROOT%\Assets\Game\Config\%%~ni.json...
  copy %%i %GAME_C_ROOT%\Assets\Game\Config\%%~ni.json
)

pause