#!/bin/bash
source $(dirname "$0")/utils.sh

cd $ROOTDIR
mvn package -DskipTests

