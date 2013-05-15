#!/bin/bash
source $(dirname "$0")/utils.sh

lib_dir=$ROOTDIR/lib

mvn install:install-file -Dfile=$lib_dir/nysenate-java-client-1.0.1.jar -DpomFile=$lib_dir/nysenate-java-client-1.0.1.pom
mvn install:install-file -Dfile=$lib_dir/nysenate-java-utils-1.0.0.jar -DpomFile=$lib_dir/nysenate-java-utils-1.0.0.pom

