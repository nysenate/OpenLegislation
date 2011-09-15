#!/bin/bash

CLASS=gov.nysenate.openleg.ingest.Ingest

CWD=`dirname $0`
cd ..
SOBI_DIR=`pwd`/data
cd $CWD

JSON_DIR=/usr/local/openleg/json/

function default {
	$CWD/base.sh $CLASS -jd $JSON_DIR -sd $SOBI_DIR $@
}

function custom {
	local sd=$1
	shift
	echo $sd
	$CWD/base.sh $CLASS -jd $JSON_DIR -sd $sd $@
}

COMMAND=$1

case $COMMAND in
	-d | --default )	shift
				default $@
				;;
	-c | --custom )		shift
				custom $@
				;;
	* )			default $@
				;;
esac


