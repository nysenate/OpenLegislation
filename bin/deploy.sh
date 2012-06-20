#!/bin/bash
source $(dirname "$0")/utils.sh

# Rough deployment plan
#   * Rebuild Project
#   * Stop Tomcat Server
#   * Remove existing installation
#   * Copy over fresh-built .war file
#   * Start Tomcat Server
#   * Empty the varnish cache
#
# TODO: Utilize a second tomcat server so that we can
#   1. Deploy new code
#   2. Warm up the lucene indexes
#   3. Swap
#
$BINDIR/build.sh
sudo service tomcat stop
rm -r $1/webapps/legislation*
cp target/legislation-$VERSION.war $1/webapps/legislation.war
sudo service tomcat start
sudo service varnish force-reload

