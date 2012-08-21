#!/bin/bash
source $(dirname "$0")/utils.sh

if [ "$1" = "" ]; then
	env=".";
else
	env="$1";
fi

# Create the specified base drive, primary folders, and work sub-folders
mkdir -p $env $env/work $env/json $env/lucene $env/processed $env/data
mkdir $env/work/bills $env/work/calendars $env/work/agendas $env/work/transcripts

# Create a shortcut link to the project repo for convenient access to scripts.
ln -s $ROOTDIR/bin $env

