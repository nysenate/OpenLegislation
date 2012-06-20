#!/bin/bash
if [ ! "$1" ]; then
    echo "Tomcat directory required.";
    echo "USAGE: bin/checkup.sh TOMCAT_DIRECTORY";
    exit 1;
fi

TOMCAT=$1
DATE=`date`
PID=`ps -ef | grep tomcat | grep -v $0 | grep -v grep | tr -s " " "|" | cut -d "|" -f 2`

cd $TOMCAT

if [ ! -n "$PID" ]; then
        echo "$DATE - tomcat was down" >&2
        bin/startup.sh
        exit 1
fi

echo "pid: $PID"

STATUS=`curl --connect-timeout 15 --write-out %{http_code} -silent http://localhost:8080/legislation/bill/S1234-2011 | tail -1`

echo "http status: $STATUS"

if [ $STATUS -ne "200" ]; then
        echo "$DATE - tomcat was unresponsive.. restarting" >&2
        kill -9 $PID
        bin/startup.sh
        sleep 10
        echo `curl --write-out %{http_code} http://localhost:8080/legislation/bill/S1234-2011` >&2
        exit 1
fi

echo "no problems in tomcat land"

exit 0

