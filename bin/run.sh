#!/bin/bash
source $(dirname "$0")/utils.sh

TOMCAT_DIR="/usr/share/tomcat7"
BASE="$ROOTDIR/target/legislation##$VERSION/WEB-INF"
PROFILE=$1; shift
SCRIPT=$1; shift

if [ ! $SCRIPT ]; then
    echo "Script name is a required argument.";
fi

# TODO: This memory size should be an adjustable parameter
java -Xmx4G -Xms16m -cp $BASE/classes/:$BASE/lib/*:$TOMCAT_DIR/lib/* -Dspring.profiles.active=$PROFILE gov.nysenate.openleg.script.$SCRIPT $@

