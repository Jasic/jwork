#!/bin/bash

cd /

chmod 777 /mnt

sudo usermod -a -G ecm jumper

cd /mnt 

chmod 775 -R .