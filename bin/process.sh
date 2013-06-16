#!/bin/bash
source $(dirname "$0")/utils.sh

USAGE="USAGE: `basename $0` --source SOURCE --work WORK --dest DEST --storage STORAGE --lucene LUCENE --basedir BASEDIR --dryrun";

dryrun=0;
basedir="";
source="data";
work="work";
dest="processed";
storage="json";
lucene="lucene";

while [ $# -gt 0 ]; do
  case "$1" in
    --source) shift; source="$1" ;;
    --work) shift; work="$1" ;;
    --dest) shift; dest="$1" ;;
    --storage) shift; storage="$1" ;;
    --lucene) shift; lucene="$1" ;;
    --basedir) shift; basedir="$1" ;;
    --dryrun) shift; dryrun=1 ;;
    *) echo $USAGE; exit 1 ;;
  esac
  shift
done

if [ "$basedir" != "" ]; then
    source="$basedir/$source";
    work="$basedir/$work";
    dest="$basedir/$dest";
    storage="$basedir/$storage";
    lucene="$basedir/$lucene";
fi

echo "Processing using: ";
echo "  Source:  $source";
echo "  Work:    $work";
echo "  Dest:    $dest";
echo "  Storage: $storage";
echo "  Lucene:  $lucene";
echo "tail -f $work/process.log to watch progress.";

if [ $dryrun -eq 1 ]; then
    exit;
fi

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
document_count=`find -L $source -type f | wc -l`
if [ $document_count -eq 0 ]; then
    echo "No files in $source to process."
    exit 0;
fi

# Stop immediately if any command below fails
set -e

errorlog=$work/process.log
changelog=$work/change.log

# Organize the change files and ingest to storage
echo "Collating $document_count files into $work";
$BINDIR/run.sh Collate $source $work &>$errorlog;
echo "Ingesting $document_count files into $storage";
$BINDIR/run.sh Ingest $work $storage --change-file $changelog &>>$errorlog

# Migrate all processed files to $dest for archival purposes
# TODO: Get Year from the filename somehow!
echo "Archiving processed files to $dest.";
year="`date +%Y`"
file_types=( "bills" "agendas" "calendars" "transcripts" "annotations" )
for file_type in "${file_types[@]}"; do
    if [ `find $work/$file_type -maxdepth 1 -type f | wc -l` -ne 0 ]; then
        if [ ! -r $dest/$year/$file_type/ ]; then
            mkdir -p $dest/$year/$file_type
        fi
        find $work/$file_type/ -type f | xargs --max-args=1000 mv -t  $dest/$year/$file_type/
    fi
done

# Push the changes out from storage
echo "Pushing `wc -l $changelog` document changes to $lucene";
$BINDIR/run.sh Push $storage --lucene $lucene --varnish --updateReporter --change-file $changelog &>>$errorlog

# Move the logs to $dest for archiving as well
echo "Archiving the change and error logs to $dest/logs/";
if [ ! -r $dest/logs/ ]; then
    mkdir $dest/logs/
fi

new_change_log=$dest/logs/`date +D%Y%m%d.T%H%M%S.change.log`;
new_error_log=$dest/logs/`date +D%Y%m%d.T%H%M%S.error.log`;
mv $changelog $new_change_log
mv $errorlog $new_error_log

echo "Finished processing changes to `cat $new_change_log | wc -l` objects.";
