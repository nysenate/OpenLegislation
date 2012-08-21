#!/bin/bash
source $(dirname "$0")/utils.sh

if [ "$1" = "" ]; then
	env=".";
else
	env="$1";
fi

if [ `find $env/processed -type f -name SOBI* | wc -l` -ne 0 ]; then
	mv `find $env/processed -type f -name SOBI*` data/;
fi

rm -fr processed/*
rm -fr json/*
rm -fr lucene/*
