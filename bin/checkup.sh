#!/bin/bash

if [ ! "$1" ]; then
    echo "Tomcat directory required." >&2
    echo "USAGE: bin/checkup.sh TOMCAT_DIRECTORY" >&2
    exit 1;
elif [ ! -d "$1" ]; then
    echo "$1 is not a valid directory." >&2
    echo "USAGE: bin/checkup.sh TOMCAT_DIRECTORY" >&2
    exit 1;
fi

DATE=`date`
TOMCAT_DIR="$1"

echo "$DATE - Running checkup"

# This attempts to find the tomcat pid by grepping on tomcat and catalina.base.
# TODO: Look into the CATALINA_PID option in catalina.sh
PID=`ps -ef | grep tomcat | grep "catalina.base" | tr -s " " "|" | cut -d "|" -f 2`
if [ ! -n "$PID" ]; then
	echo "$DATE - No tomcat process found. Running bin/startup.sh" >&2
	$TOMCAT_DIR/bin/startup.sh
	exit 1
else
    echo "$DATE - PID $PID, tomcat still running";
fi

# Here we consider 20 seconds to be unresponsive.
# It is important to ping :8080 directly instead of going through the proxy
# because the proxy (aka varnish) might be having problems or be in the middle
# of reloading.
STATUS=`curl --connect-timeout 20 --write-out %{http_code} --silent http://open.nysenate.gov:8080/legislation/bill/S1234-2011 | tail -1`
if [ $STATUS -ne "200" ]; then
    echo "$DATE - HTTP $STATUS, restarting tomcat" >&2
    # Sometimes bin/shutdown.sh won't work. We need to be sure.
    kill -9 $PID
    $TOMCAT_DIR/bin/startup.sh
    # TODO: Why are we sleeping here?
    sleep 10
    exit 1
else
    echo "$DATE - HTTP $STATUS, checkup detected no issues." >&2
fi

