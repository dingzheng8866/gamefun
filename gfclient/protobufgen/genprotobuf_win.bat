@echo off
setlocal EnableDelayedExpansion

set pdir=../../gfserver/proto/protobuf/
rem %~d0
rem cd %~sdp0
rem cd ..\proto\protobuf
for %%i in (%pdir%*.proto) do (
rem echo %%i
set filelist=%pdir%%%i !filelist!
)
set pfiles=!filelist!
rem echo %pfiles%

@protoc -I%pdir% %pfiles% -oPackets.bin
clientgen\protogen.exe -i:Packets.bin -o:../unityproject/Packet.cs -ns:CC.Runtime.PB -p:detectMissing
