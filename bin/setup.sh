#!/bin/bash
source $(dirname "$0")/utils.sh

lib_dir=$ROOTDIR/lib

function mvn_install {
    mvn install:install-file -DgroupId=$1 -DartifactId=$2 -Dversion=$3 -Dfile=$4 -Dpackaging=jar -DgeneratePom=true
}

mvn_install gov.nysenate.services nysenate-java-client 1.0 $lib_dir/nysenate-java-client-1.0.jar

