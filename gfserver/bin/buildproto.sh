cd ../proto/protobuf
for i in *.proto;
do
	echo $i;
	protoc --java_out=../../src/main/java $i;
done
