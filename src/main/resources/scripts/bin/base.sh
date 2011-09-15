#!/bin/sh

apppath='/usr/local/tomcat/webapps/legislation/WEB-INF'

java -Xmx756m -Xms16m -cp $apppath/classes/:$apppath/lib/* $@ 
