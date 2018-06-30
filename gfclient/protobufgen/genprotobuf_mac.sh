protoc -I  -oPackets.bin 
mono ./clientgen/protogen.exe -i:Packets.bin -o:../unityproject/Assets/GEngine/Generated/NetProtocolMessage.cs -ns:GEngine.Net.Proto -p:detectMissing