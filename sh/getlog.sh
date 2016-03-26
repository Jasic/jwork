#!/bin/bash

if [ ! -n "$1" ]
then
   echo "Must Specity Log File Name~~~~"
   exit
fi

IPSFILE=./ip.ini

if [ $IPSFILE ]
then
  if [ -d "$IPSFILE" ]
  then
    echo "Can't find ips setting file ip.ini"
  fi
fi

# Get a log by a ip ~~~~~~~~~
function getlog(){
  echo "get logs[$2] from server [$1]~"
  echo "~~~~~~~~~~~~~~~get logs[$2] from server [$1]"~~~~~~~~~~~ >> ~/log/$2
  ssh weixiong.zwx@$1 " cat /home/admin/vsp/logs/$2" >> ~/log/$2

}

# Get a log by vsphost~~~~~~~
for IP in  `cat $IPSFILE`
  do
  getlog $IP $1
done