#!/bin/bash

BINDIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
USAGE="USAGE: `basename $0` --source-dir SOURCE --work-dir WORK --dest-dir DEST --storage STORAGE";

while [ $# -gt 0 ]; do
  case "$1" in
    --source-dir) shift; source="$1" ;;
    --work-dir) shift; work="$1" ;;
    --dest-dir) shift; dest="$1" ;;
    --storage) shift; storage="$1" ;;
    *) echo $USAGE; exit 1 ;;
  esac
  shift
done

required_args=( "$source" "$work" "$dest" "$storage" )
for required_arg in "${required_args[@]}"; do
    if [ ! "$required_arg" ]; then
        echo $USAGE; exit 1 ;
    elif [ ! -r "$required_arg" ]; then
        echo "$required_arg must exist on the filesystem.";
        echo $USAGE; exit 1 ;
    fi
done

# Check to see if there is any work to be done
if [ `find $source -maxdepth 1 -type f | wc -l` -eq 0 ]; then
    exit 0;
fi

# Stop immediately if any command below fails
set -e

# Organize the change files and ingest to storage
$BINDIR/run.sh collate --source $source --dest $work;
$BINDIR/run.sh ingest --source $work --storage $storage --change-file $work/change.log

# Migrate all processed files to $dest for archival purposes
year="`date +%Y`"
file_types=( "bills" "agendas" "calendars" "transcripts" "annotations" )
for file_type in "${file_types[@]}"; do
    if [ `find $work/$file_type -maxdepth 1 -type f | wc -l` -ne 0 ]; then
        if [ ! -r $dest/$year/$file_type/ ]; then
            mkdir -p $dest/$year/$file_type
        fi
        mv $work/$file_type/* $dest/$year/$file_type/
    fi
done

# Push the changes out from storage
$BINDIR/run.sh push --lucene --storage $storage --change-file $work/change.log

# Move the change.log to $dest for archiving as well
if [ ! -r $dest/changelogs/ ]; then
    mkdir $dest/changelogs/
fi
mv $work/change.log $dest/changelogs/`date +CHANGELOG.D%Y%m%d.T%H%M%S.TXT`
