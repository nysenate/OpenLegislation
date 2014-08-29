#!/bin/bash
source $(dirname "$0")/utils.sh

cd $ROOTDIR
mvn clean
mvn compile war:exploded

