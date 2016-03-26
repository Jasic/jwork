#!/bin/bash

LOGPATH=~/log
echo $LOGPATH
ALLCOUNT=0
DISTINCTALL=0
SUCCESSCOUNT=0

for x in `ls $LOGPATH|grep msgSend`
do
echo "---------------- Proccess [$x] begin -----------------"
cat $x | grep '开始-->调用PAC向' |cut -f 2 -d "始" -s| uniq -c| sort -rn > allTemp
cat $x | grep ']处理成功' |cut -f 2 -d "单" -s| uniq -c| sort -rn > allSTemp
wc -l < allTemp > allCountTemp
wc -l < allSTemp > allSCountTemp

if [ -n "$1" ]
then

        echo "####################### All TredeIds BEGIN  ###########################"
        cat allTemp
        echo "####################### All TredeIds END ###########################"


        echo "####################### Success TredeIds BEGIN ###########################"
        cat allSTemp
        echo "####################### Success TredeIds EDN ###########################"
fi
echo "Distinct-count:"
cat allCountTemp

echo "Success-count:"
cat allSCountTemp
echo "---------------- Proccess [$x] over -----------------"
done
yes|rm *Temp