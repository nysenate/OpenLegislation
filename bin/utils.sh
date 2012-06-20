#!/bin/bash

BINDIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )";
ROOTDIR="$(dirname $BINDIR)";
VERSION=`sed -ne 's/\s*<version>\(.*\)<\/version>/\1/p' $ROOTDIR/pom.xml | head -1`;

