#!/bin/sh

GWT_HOME='/opt/sakai/gwt-windows-1.5.0'

echo "Removing files from previous compilation/deployment..."
rm -rf www/*
mkdir bin
rm -rf bin/*
rm -rf ../osyl/osyl-tool/tool/src/webapp/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint
rm -rf ${CATALINA_HOME}/webapps/sakai-opensyllabus-tool/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint

echo ""
echo "Compiling shared classes..."
$JAVA_HOME/bin/javac `find src/org/sakaiquebec/opensyllabus/shared -name *.java` -cp "${GWT_HOME}/gwt-user.jar:." -d bin
cp ./src/org/sakaiquebec/opensyllabus/shared.gwt.xml ./bin/org/sakaiquebec/opensyllabus/
cd bin
jar cvf OsylShared.jar *
cd ..


echo ""
echo "Running GWT compilation..."
$JAVA_HOME/bin/java -Xmx512m -cp "src:bin:$GWT_HOME/gwt-user.jar:$GWT_HOME/gwt-dev-windows.jar:bin/OsylShared.jar:$GWT_HOME/gwt-incubator_1-4_final.jar" com.google.gwt.dev.GWTCompiler -logLevel ERROR -out "www" $* org.sakaiquebec.opensyllabus.OsylEditorEntryPoint

echo ""
echo "Removing unused files..."
rm -f www/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/*.cache.xml
rm -f www/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/*.cache.js
rm -f www/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/*-xs.js
rm -f www/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/gwt.js

echo ""
echo "Deploying files to ../osyl/osyl-tool/tool/src/webapp..."
\cp -pr www/* ../osyl/osyl-tool/tool/src/webapp/
echo ""
echo "Deploying files to $CATALINA_HOME/webapps..."
mkdir -p $CATALINA_HOME/webapps/sakai-opensyllabus-tool/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint
\cp -pr www/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/* $CATALINA_HOME/webapps/sakai-opensyllabus-tool/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/

