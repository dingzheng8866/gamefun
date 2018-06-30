@echo off

rem gen server pb
%~d0
cd %~sdp0
cd ..\proto\protobuf
for %%i in (*.proto) do (
"..\..\bin\protoc.exe" --java_out=..\..\src\main\java %%i
)

rem gen client pb
cd ../../../gfclient/protobufgen
genprotobuf_win.bat

cd ../../gfserver/bin
