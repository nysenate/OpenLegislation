#!/bin/bash
source $(dirname "$0")/utils.sh

BASE="$ROOTDIR/target/legislation-$VERSION/WEB-INF"
SCRIPT=$1; shift

if [ ! $SCRIPT ]; then
    echo "Script name is a required argument.";
fi

# TODO: This memory size should be an adjustable parameter
java -Xmx1024m -Xms16m -cp $BASE/classes/:$BASE/lib/* gov.nysenate.openleg.scripts.$SCRIPT $@

