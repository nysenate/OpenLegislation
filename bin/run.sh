#!/bin/bash
source $(dirname "$0")/utils.sh

BASE="$ROOTDIR/target/legislation-$VERSION/WEB-INF"

case $1 in
    ingest) SCRIPT=gov.nysenate.openleg.scripts.Ingest; shift;;
    collate) SCRIPT=gov.nysenate.openleg.scripts.Collate; shift;;
    push) SCRIPT=gov.nysenate.openleg.scripts.Push; shift;;
    --help | -h | help) echo "USAGE: `basename $0` ingest|help <args>"; exit;;
esac

# TODO: This memory size should be an adjustable parameter
java -Xmx1024m -Xms16m -cp $BASE/classes/:$BASE/lib/* $SCRIPT $@

