#!/bin/sh

basedir=`dirname $0`;

echo "Starting GWT Hosted-Mode..."

$JAVA_HOME/bin/java -Xmx512m -Xdebug -Xrunjdwp:transport=dt_socket,address=9000,server=y,suspend=n -cp "${basedir}/src:${M2_REPO}/org/sakaiquebec/opensyllabus/sakai-opensyllabus-common-shared/M2/sakai-opensyllabus-common-shared-M2.jar:${GWT_HOME}/gwt-user.jar:${GWT_HOME}/gwt-dev-linux.jar:${GWT_HOME}/gwt-incubator.jar:${GWT_HOME}/gwt-fx.jar:${GWT_HOME}/gwt-mosaic.jar" com.google.gwt.dev.GWTShell -out "${basedir}/www" $* org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/OsylEditorEntryPoint.html
