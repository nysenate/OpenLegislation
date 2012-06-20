#!/bin/bash
source $(dirname "$0")/utils.sh

cd $ROOTDIR
bin/setup.sh
mvn package

