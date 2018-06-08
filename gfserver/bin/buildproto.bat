@echo off

%~d0
cd %~sdp0
cd ..\proto\protobuf
for %%i in (*.proto) do (
"..\..\bin\protoc.exe" --java_out=..\..\src %%i
)
