#!/bin/sh

echo "Removing files from previous compilation/deployment..."
rm -rf www/*
rm -rf ../osyl/osyl-tool/tool/src/webapp/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint
rm -rf ${CATALINA_HOME}/webapps/sakai-opensyllabus-tool/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint

echo ""
echo "Running GWT compilation..."
$JAVA_HOME/bin/java -Xmx512m -cp "src:$GWT_HOME/gwt-user.jar:$GWT_HOME/gwt-dev-linux.jar:$M2_REPO/org/sakaiquebec/opensyllabus/sakai-opensyllabus-common-shared/M2/sakai-opensyllabus-common-shared-M2.jar:$GWT_HOME/gwt-incubator.jar:$GWT_HOME/gwt-fx.jar:${GWT_HOME}/gwt-mosaic.jar:${GWT_HOME}/gwtx.jar:${GWT_HOME}/gwt-beans-binding.jar:${GWT_HOME}/gwt-dnd.jar" com.google.gwt.dev.GWTCompiler -logLevel ERROR -out "www" $* org.sakaiquebec.opensyllabus.OsylEditorEntryPoint

echo ""
echo "Deploying files to ../osyl/osyl-tool/tool/src/webapp..."
\cp -pr www/* ../osyl/osyl-tool/tool/src/webapp/
echo ""
echo "Deploying files to $CATALINA_HOME/webapps..."
mkdir -p $CATALINA_HOME/webapps/sakai-opensyllabus-tool/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint
\cp -pr www/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/* $CATALINA_HOME/webapps/sakai-opensyllabus-tool/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/
