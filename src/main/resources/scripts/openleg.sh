#!/bin/bash

function usage
{
	echo "usage: $0 (-(r|i|h)|--(report|ingest|help)) (--help|<params>)"
}

CWD=`pwd`

BASE=`pwd`
BIN=$BASE/bin

COMMAND=$1
shift

case $COMMAND in
	-r | --report )		$BIN/report.sh $@	
				;;
	-i | --ingest )		$BIN/ingest.sh $@	
				;;
	-s | --search )		$BIN/search.sh $@
				;;
	-h | --help )		usage	
				;;
	* )			usage
				exit 1
esac
