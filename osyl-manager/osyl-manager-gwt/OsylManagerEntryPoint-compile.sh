#!/bin/sh

/bin/rm -rf www/*
/bin/rm -rf ../osyl-admin/osyl-admin-tool/tool/src/webapp/org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint
/bin/rm -rf $CATALINA_HOME/webapps/sakai-opensyllabus-manager-tool/org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint

$JAVA_HOME/bin/java -Xmx512m -cp "src:$GWT_HOME/gwt-user.jar:$GWT_HOME/gwt-dev-linux.jar" com.google.gwt.dev.GWTCompiler -out "www" $* org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint

/bin/cp -pr www/* ../osyl-manager/osyl-manager-tool/tool/src/webapp/
mkdir -p $CATALINA_HOME/webapps/sakai-opensyllabus-manager-tool
/bin/cp -pr www/org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint $CATALINA_HOME/webapps/sakai-opensyllabus-manager-tool/

