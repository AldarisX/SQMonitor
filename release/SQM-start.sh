#!/bin/sh

cd ~/SQMonitor/
nohup setsid java -server -Xmx48M -Xms16M -jar SQMonitor.jar > /dev/null > /dev/null 2>&1 &