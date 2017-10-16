#!/bin/sh

cd ~/SQMonitor/
nohup setsid java -server -Xmx32M -Xms16M -jar ~/SQMonitor/SQMonitor.jar -passwd aldaris -port 8000 > /dev/null > /dev/null 2>&1 &