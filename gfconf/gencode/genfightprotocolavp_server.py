#!/usr/bin/env python
# encoding: utf-8

import os.path
import re


fopen = open("template/fight_protocol_avp.template_java", 'r')
item_id_file_template = fopen.read()
#print item_id_file_template 
fopen.close()

fopen = open("../../gfserver/resources/config/fight_protocol_avp.csv", 'r')
all_items = fopen.read()
fopen.close()

#avpTypes.put(Para.UserActionTypeName.getValue(), AVPType.T_Int);
#sb.append("    "+item.getTypeStringValue()+"("+item.getId()+")");
#sb.append(i==list.size() - 1 ? ";" : ","+"\n");
#		case 0: return seasonCard;
enumvalue=''
switchvalue=''
avptypeinitvalue=''
allItemArray = all_items.split('\n')
for i in range(2, len(allItemArray), 1):  
	strArray = allItemArray[i].split(';')
	if len(strArray) > 1:
		enumvalue+='        '
		enumvalue+=strArray[0]
		enumvalue+='('
		enumvalue+=strArray[1]
		enumvalue+='),\n'
		switchvalue+='            '
		switchvalue+='case '
		switchvalue+=strArray[1]
		switchvalue+=': return '
		switchvalue+=strArray[0]
		switchvalue+=';\n'
		avptypeinitvalue+='            '
		avptypeinitvalue+='avpTypes.put(Para.'
		avptypeinitvalue+=strArray[0]
		avptypeinitvalue+='.getValue(), AVPType.T_'
		avptypeinitvalue+=strArray[2]
		avptypeinitvalue+=');\n'

enumvalue=enumvalue[0:len(enumvalue)-2]
enumvalue+=";"

item_id_file_template = item_id_file_template.replace("#ENUM_VAR", enumvalue);
item_id_file_template = item_id_file_template.replace("#AVPTYPE_INIT", avptypeinitvalue);
finalContent = item_id_file_template.replace("#SWITCH_VAR", switchvalue);

#print(finalContent)

tofilename = '../../gfserver/src/main/java/com/tiny/game/common/server/fight/bizlogic/IFight.java'
with open(tofilename,'w') as f: # 如果filename不存在会自动创建， 'w'表示写数据，写之前会清空文件中的原有数据！
    f.write(finalContent)
f.close()
        