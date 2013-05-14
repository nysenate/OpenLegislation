#!/bin/bash
source $(dirname "$0")/utils.sh

lib_dir=$ROOTDIR/lib

function mvn_install {
    mvn install:install-file -DgroupId=$1 -DartifactId=$2 -Dversion=$3 -Dfile=$4 -Dpackaging=jar 
}

mvn install:install-file -Dfile=$lib_dir/nysenate-java-client-1.0.1.jar -DpomFile=$lib_dir/nysenate-java-client-1.0.1.pom
mvn install:install-file -Dfile=$lib_dir/nysenate-utils-1.0.0.jar -DpomFile=$lib_dir/nysenate-utils-1.0.0.pom

