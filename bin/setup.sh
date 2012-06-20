#!/bin/bash


root_dir=`dirname $0`/../
lib_dir=$root_dir/src/main/resources/lib

function mvn_install {
    mvn install:install-file -DgroupId=$1 -DartifactId=$2 -Dversion=$3 -Dfile=$4 -Dpackaging=jar -DgeneratePom=true
}

mvn_install gov.nysenate    SenateServices         0.0.1-SNAPSHOT     $lib_dir/SenateServices-0.0.1-SNAPSHOT.jar

