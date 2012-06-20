#!/bin/bash
source $(dirname "$0")/utils.sh

USAGE="USAGE: `basename $0` --source SOURCE --work WORK --dest DEST --storage STORAGE --lucene LUCENE";

while [ $# -gt 0 ]; do
  case "$1" in
    --source) shift; source="$1" ;;
    --work) shift; work="$1" ;;
    --dest) shift; dest="$1" ;;
    --storage) shift; storage="$1" ;;
    --lucene) shift; lucene="$1" ;;
    *) echo $USAGE; exit 1 ;;
  esac
  shift
done

required_args=( "$source" "$work" "$dest" "$storage" "$lucene")
for required_arg in "${required_args[@]}"; do
    if [ ! "$required_arg" ]; then
        echo "All options are required: source, work, dest, storage, lucene";
        echo $USAGE; exit 1 ;
    elif [ ! -r "$required_arg" ]; then
        echo "$required_arg must exist on the filesystem.";
        echo $USAGE; exit 1 ;
    fi
done

# Check to see if there is any work to be done
if [ `find $source -type f | wc -l` -eq 0 ]; then
    exit 0;
fi

# Stop immediately if any command below fails
set -e

errorlog=$work/process.log
changelog=$work/change.log

# Organize the change files and ingest to storage
$BINDIR/run.sh collate $source $work &>$errorlog;
$BINDIR/run.sh ingest $work $storage --change-file $changelog &>>$errorlog

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
$BINDIR/run.sh push $storage --lucene $lucene --change-file $changelog &>>$errorlog

# Move the logs to $dest for archiving as well
if [ ! -r $dest/logs/ ]; then
    mkdir $dest/logs/
fi
mv $changelog $dest/logs/`date +D%Y%m%d.T%H%M%S.change.log`
mv $errorlog $dest/logs/`date +D%Y%m%d.T%H%M%S.error.log`

