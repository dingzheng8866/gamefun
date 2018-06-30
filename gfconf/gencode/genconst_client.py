#!/usr/bin/env python
# encoding: utf-8

import os.path
import re


fopen = open("template/const.template_cs", 'r')
file_template_content = fopen.read()
#print file_template_content 
fopen.close()

fopen = open("../../gfserver/resources/config/const.csv", 'r')
file_contents = fopen.read()
fopen.close()

enumvalue=''
allItemArray = file_contents.split('\n')
for i in range(2, len(allItemArray), 1):  
	strArray = allItemArray[i].split(';')
	if len(strArray) > 1:
		if len(strArray[1]) > 0:
			enumvalue+='        public const float '
			enumvalue+=strArray[0]
			enumvalue+=' = '
			enumvalue+=strArray[1]
			enumvalue+='f;\n'
		elif len(strArray[2]) > 0:
			enumvalue+='        public const int '
			enumvalue+=strArray[0]
			enumvalue+=' = '
			enumvalue+=strArray[2]
			enumvalue+=';\n'		
		elif len(strArray[3]) > 0:
			enumvalue+='        public const string '
			enumvalue+=strArray[0]
			enumvalue+=' = \"'
			enumvalue+=strArray[3]
			enumvalue+='\";\n'
		
finalContent = file_template_content.replace("#CONST_VAR", enumvalue);

print(finalContent)

tofilename = '../../gfclient/unityproject/Assets/GEngine/Generated/GameConst.cs'
with open(tofilename,'w') as f: # 如果filename不存在会自动创建， 'w'表示写数据，写之前会清空文件中的原有数据！
    f.write(finalContent)
f.close()
        