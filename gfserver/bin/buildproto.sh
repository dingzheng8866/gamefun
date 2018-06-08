cd ../proto/protobuf
for i in *.proto;
do
	echo $i;
	protoc --java_out=../../src $i;
done
