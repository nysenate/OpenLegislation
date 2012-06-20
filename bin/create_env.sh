#!/bin/bash
source $(dirname "$0")/utils.sh

# Create the specified base drive, primary folders, and work sub-folders
mkdir -p $1 $1/work $1/json $1/lucene $1/processed $1/data
mkdir $1/work/bills $1/work/calendars $1/work/agendas $1/work/transcripts

# Create a shortcut link to the project repo for convenient access to scripts.
ln -s $ROOTDIR/bin $1

