#!/bin/bash
source $(dirname "$0")/utils.sh

if [ "$1" = "" ]; then
    env=".";
else
    env="$1";
fi

if [ `find $env/processed -type f -name SOBI* | wc -l` -ne 0 ]; then
    find $env/processed/ -type f -name SOBI* | xargs mv -t $env/data/
fi

if [ -e $env/processed/CMS.TEXT ]; then
    mv $env/processed/CMS.TEXT $env/data
fi

if [ -e $env/work/CMS.TEXT ]; then
    mv $env/work/CMS.TEXT $env/data
fi

if [ `find $env/work -type f -name SOBI* | wc -l` -ne 0 ]; then
    find $env/work/ -type f -name SOBI* | xargs mv -t $env/data/
fi

if [ `find $env/processed/transcripts/ -type f | wc -l` -ne 0 ]; then
    find $env/processed/transcripts/ -type f | xargs mv -t $env/data/transcripts/
fi

if [ `find $env/processed/hearings/ -type f | wc -l` -ne 0 ]; then
    find $env/processed/hearings/ -type f | xargs mv -t $env/data/hearings/
fi


rm -fr $env/work/*
rm -fr $env/processed/*
rm -fr $env/json/*
rm -fr $env/lucene/*
rm -f $env/data/*.xml
rm -f $env/data/*.log
find $env/data/ -type f -name *.sobi | xargs rm

