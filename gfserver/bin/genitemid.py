#!/usr/bin/env python
# encoding: utf-8

import os.path
import re


fopen = open("../resources/template/item_id.template", 'r')
item_id_file_template = fopen.read()
#print item_id_file_template 
fopen.close()

fopen = open("../resources/config/item.csv", 'r')
all_items = fopen.read()
fopen.close()

#sb.append("    "+item.getTypeStringValue()+"("+item.getId()+")");
#sb.append(i==list.size() - 1 ? ";" : ","+"\n");
#		case 0: return seasonCard;
enumvalue=''
switchvalue=''
allItemArray = all_items.split('\n')
for i in range(2, len(allItemArray), 1):  
	strArray = allItemArray[i].split(';')
	if len(strArray) > 1:
		enumvalue+='    '
		enumvalue+=strArray[1]
		enumvalue+='('
		enumvalue+=strArray[0]
		enumvalue+='),\n'
		switchvalue+='        '
		switchvalue+='case '
		switchvalue+=strArray[0]
		switchvalue+=': return '
		switchvalue+=strArray[1]
		switchvalue+=';\n'

enumvalue=enumvalue[0:len(enumvalue)-2]
enumvalue+=";"

item_id_file_template = item_id_file_template.replace("#ENUM_VAR", enumvalue);
finalContent = item_id_file_template.replace("#SWITCH_VAR", switchvalue);

print(finalContent)

tofilename = '../src/main/java/com/tiny/game/common/domain/item/ItemId.java'
with open(tofilename,'w') as f: # 如果filename不存在会自动创建， 'w'表示写数据，写之前会清空文件中的原有数据！
    f.write(finalContent)
f.close()
        