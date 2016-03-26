#!/bin/bash

yes|rm *Temp

LOGPATH=~/log
echo $LOGPATH

ALLCOUNT=0
DISTINCTALL=0
SUCCESSCOUNT=0

for x in `ls $LOGPATH|grep msgSend`
do
  echo "---------------- Proccess [$x] begin -----------------"
  #cat "$LOGPATH/$x" | grep 'BizMsgSendCallBackService.java:92' | cut -f 3 -d ']' -s|cut -f 2 -d '[' -s | uniq -c| sort -rn > allTemp
  cat "$LOGPATH/$x" | grep 'BizMsgSendCallBackService.java:92' | cut -f 3 -d ']' -s| cut -f 2 -d '[' -s |uniq -c| sort -rn > allTemp
  cat "$LOGPATH/$x" | grep 'BizMsgSendCallBackService.java:105'| cut -f 3 -d ']' -s| cut -f 2 -d '[' -s |uniq -c| sort -rn > allSTemp
  cat allTemp | uniq | wc -l > allCountTemp
  cat allSTemp| uniq | wc -l > allSCountTemp
 
if [ -n "$1" ]
then

        echo "####################### All TredeIds BEGIN  ###########################"
        cat allTemp
        echo "####################### All TredeIds END ###########################"


        echo "####################### Success TredeIds BEGIN ###########################"
        cat allSTemp
        echo "####################### Success TredeIds EDN ###########################"
  
        echo "####################### Fail TredeIds BEGIN ###########################"
        cat allTemp allSTemp | sort -n | uniq -u
        echo "####################### Fail TredeIds EDN ###########################"
fi

echo "Distinct-count:"
cat allCountTemp

echo "Success-count:"
cat allSCountTemp
echo "---------------- Proccess [$x] over -----------------"
echo ""
done
yes|rm *Temp