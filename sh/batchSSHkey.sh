#!/bin/bash
if [ ! -n "$1" ]
then
   echo "Please specify the file containing IP list"
   exit
fi

while read LINE
do
echo "============================="
echo "IP:"$LINE
#first: copy
#scp $HOME/.ssh/id_rsa.pub weixiong.zwx@$LINE:.ssh/id_rsa.pub

#secod: append
ssh weixiong.zwx@$LINE "cat .ssh/id_rsa.pub >> .ssh/authorized_keys"
done < $1