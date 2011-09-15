#!/bin/bash

cd /home/ubuntu/openleg

BASE_DIR=`pwd`
WORK_DIR=$BASE_DIR/work
DATA_DIR=$BASE_DIR/data
LOG_DIR=$BASE_DIR/logs
DEST_DIR=$BASE_DIR/processed/
DATE=`date +%Y%m%d%H%M`

DATA_COUNT=`ls $DATA_DIR | wc -l`

if [ $DATA_COUNT -eq 0 ] ; then
  exit 0
fi

if [ $# -gt 0 ] ; then
  DATA_DIR=$1
fi

if [ $# -gt 1 ] ; then
  DEST_DIR=$2
fi

exec 1> $LOG_DIR/process-$DATE.out
exec 2> $LOG_DIR/process-$DATE.err

cd $BASE_DIR
mv $DATA_DIR/* $WORK_DIR/

#pull cal/agenda xml from sobis
./openleg.sh -i -c $WORK_DIR -gx

mv $WORK_DIR/*age* $WORK_DIR/age/
mv $WORK_DIR/*cal* $WORK_DIR/cal/
mv $WORK_DIR/*.TXT $WORK_DIR/sobi/
rm $WORK_DIR/*ann*

#write json from sobis/xml
./openleg.sh -i -c $WORK_DIR/sobi -w
./openleg.sh -i -c $WORK_DIR/age -w
./openleg.sh -i -c $WORK_DIR/cal -w

./openleg.sh --ingest -i -pc

mv $WORK_DIR/sobi/* $DEST_DIR/sobi/
mv $WORK_DIR/age/* $DEST_DIR/age/
mv $WORK_DIR/cal/* $DEST_DIR/cal/
