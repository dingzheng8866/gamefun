#!/usr/bin/env python
# encoding: utf-8

import os.path
import re


fopen = open("../resources/template/item_attr.template", 'r')
item_id_file_template = fopen.read()
#print item_id_file_template 
fopen.close()

fopen = open("../resources/config/item_attr.csv", 'r')
all_items = fopen.read()
fopen.close()

#public static final String hitTarget="hitTarget";
enumvalue=''
switchvalue=''
allItemArray = all_items.split('\n')
strArray = allItemArray[1].split(';')
for i in range(4, len(strArray), 1):  
	enumvalue+='    public static final String '
	enumvalue+=strArray[i]
	enumvalue+=' = \"'
	enumvalue+=strArray[i]
	enumvalue+='\";\n'

finalContent = item_id_file_template.replace("#ATTR_VAR", enumvalue);

print(finalContent)

tofilename = '../src/main/java/com/tiny/game/common/domain/item/ItemAttr.java'
with open(tofilename,'w') as f: # 如果filename不存在会自动创建， 'w'表示写数据，写之前会清空文件中的原有数据！
    f.write(finalContent)
f.close()
        