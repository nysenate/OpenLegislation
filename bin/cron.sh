#!/bin/sh
#
# cron.sh - Main cron wrapper for OpenLegislation data processing.
#
# Project: OpenLegislation
# Author: Ken Zalewski
# Organization: New York State Senate
# Date: 2014-12-05
# Revised: 2015-06-05 - added options to control the two phases
# Revised: 2015-11-18 - added --no-get as a pass-thru option to xferdata.sh
#

prog=`basename $0`

usage() {
  echo "Usage: $prog [--skip-xfer] [--skip-proc]" >&2
}

skip_xfer=0
skip_proc=0
xfer_opts=

while [ $# -gt 0 ]; do
  case "$1" in
    --skip-xfer) skip_xfer=1 ;;
    --skip-proc) skip_proc=1 ;;
    --no-get) xfer_opts="$xfer_opts $1" ;;
    --help) usage; exit 0 ;;
    *) echo "$prog: $1: Invalid option" >&2; usage; exit 1 ;;
  esac
  shift
done

echo "Starting OpenLegislation data processing at `date`"

if [ $skip_xfer -ne 1 ]; then
  echo "About to start the data transfer phase..."
  /data/openleg/lbdc_xfer/xferdata.sh $xfer_opts
  echo "Finished with the data transfer phase"
fi

if [ $skip_proc -ne 1 ]; then
  echo "About to start the SOBI processing phase..."
  [ "$JAVA_HOME" ] || . /etc/profile.d/java.sh
  cd /data/openleg/runtime_prod
  /opt/openleg_prod/bin/run.sh ProcessData -e ./app.properties -t stage,collate,ingest,push,archive -f change.log -p lucene,reporter
  echo "Finished with the SOBI processing phase"
fi

echo "Completed OpenLegislation data processing at `date`"
exit 0
