#!/bin/bash
if [ ! -n "$1" ]
then
   echo "Please specify the file containing tradeIds and storeCodes"
   exit
fi
echo "Input your password:"
read passwd
passwd=`echo -n $passwd|md5sum`
#echo $passwd
if [ "$passwd" != "d1fa0d764cbe49db73353cb128abb765  -" ]
then
  echo "Wrong password,exit the program"
  exit
fi

echo "start executing..."
while read LINE
do

tradeId=`echo $LINE|cut -d '#' -f 1|tr -d ' '`
storeCode=`echo $LINE|cut -d '#' -f 2|tr -d ' '`
echo "============================="
echo "[tradeId:"$tradeId",storeCode:"$storeCode"]"
curl --data "flag=1&store_code="$storeCode"&trade_id="$tradeId"&passwd=yysmd" http://127.0.0.1/changeRouteWarehouse.do
echo ""
done < $1