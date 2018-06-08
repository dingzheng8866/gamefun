#!/bin/sh

WHITE_IPS=`cat <<EOF
116.231.182.202
60.251.139.151
60.251.139.120
EOF`

PORTS=80,8080,2100,2200,2300,2400

service iptables restart

iptables -P OUTPUT ACCEPT
iptables -P FORWARD ACCEPT
#iptables -P INPUT DROP
iptables -I INPUT -p tcp -m multiport --dport $PORTS -j DROP

# 从外面连进来，本机开放的端口
#iptables -I INPUT -p tcp -m multiport --dport 22,80,443,2701,29017,29021,29200,29300 -j ACCEPT
#iptables -I INPUT -p icmp --icmp-type 0 -j ACCEPT

# 白名单的IP可访问游戏端口
for ip in $WHITE_IPS ;
do
iptables -I INPUT -p tcp -s $ip -m multiport --dport $PORTS -j ACCEPT
done


#WHITE_LIGHT_IPS=`cat <<EOF
#EOF`
#LIGHT_PORTS=29007
#iptables -I INPUT -p tcp -m multiport --dport $LIGHT_PORTS -j DROP

#for ip in $WHITE_LIGHT_IPS ;
#do
#iptables -I INPUT -p tcp -s $ip -m multiport --dport $LIGHT_PORTS -j ACCEPT
#done
#
#WHITE_LIGHT_MACS=`cat <<EOF
#EOF`
#for ip in $WHITE_LIGHT_MACS ;
#do
#echo "iptables -A INPUT -p tcp --dport $LIGHT_PORTS -m mac --mac-source $ip -j ACCEPT"
#iptables -I INPUT -p tcp -m multiport --dport $LIGHT_PORTS -m mac --mac-source $ip -j ACCEPT
#done
#
#iptables -I INPUT -p tcp --dport 29007 -j LOG --log-level 5 --log-prefix "IPTABLES:"

# 从外面的哪些端口流入的数据允许进入本机
#iptables -I INPUT -p tcp -m multiport --sport 80,443,8080,2100,2200,2300,2400 -j ACCEPT

# ping 和域名解析的设置
#iptables -I INPUT -p icmp --icmp-type 0 -j ACCEPT
#iptables -I INPUT -p icmp --icmp-type 8 -j ACCEPT
#iptables -I INPUT -p udp --sport 53 -j ACCEPT

iptables -L -nv
