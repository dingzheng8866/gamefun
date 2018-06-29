@echo off

SET GAME_C_ROOT=%~sdp0\..\gfclient
SET GAME_S_ROOT=%~sdp0\..\gfserver

for %%i in (conf\*.xlsx) do (
  echo Converting %%i to %GAME_C_ROOT%\unityproject\Assets\BundleResources\config\%%~ni.csv...
  rem xlsx2csv\xlsx2csv -i -d ; -s 1 %%i %GAME_C_ROOT%\unityproject\Assets\BundleResources\config\%%~ni.csv
  echo Converting %%i to %GAME_S_ROOT%\resources\config\%%~ni.csv...
  rem xlsx2csv\xlsx2csv -i -d ; -s 1 %%i %GAME_S_ROOT%\resources\config\%%~ni.csv
)

rem for %%i in (conf\*.json) do (
rem   echo Copying %%i to %GAME_C_ROOT%\Assets\Game\Config\%%~ni.json...
rem  copy %%i %GAME_C_ROOT%\Assets\Game\Config\%%~ni.json
rem )


%~d0
cd %~sdp0
cd gencs

python genconst_server.py
python genitemattr_server.py
python genitemid_server.py

python genconst_client.py
python genitemattr_client.py
python genitemid_client.py

pause