#!/bin/sh

/bin/rm -rf www/*
/bin/rm -rf ../osyl-admin/osyl-admin-tool/tool/src/webapp/org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint
/bin/rm -rf $CATALINA_HOME/webapps/sakai-opensyllabus-admin-tool/org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint

$JAVA_HOME/bin/java -Xmx512m -cp "src:bin:$GWT_HOME/gwt-user.jar:$GWT_HOME/gwt-dev-linux.jar" com.google.gwt.dev.GWTCompiler -out "www" $* org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint

/bin/cp -pr www/* ../osyl-admin/osyl-admin-tool/tool/src/webapp/
mkdir -p $CATALINA_HOME/webapps/sakai-opensyllabus-admin-tool
/bin/cp -pr www/org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint $CATALINA_HOME/webapps/sakai-opensyllabus-admin-tool/

