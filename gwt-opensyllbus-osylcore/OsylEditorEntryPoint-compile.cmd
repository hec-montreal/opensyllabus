del /s /Q www
mkdir bin
del /s /Q bin
del /s /Q ..\osyl\osyl-tool\tool\src\webapp\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint
del /s /Q %CATALINA_HOME%\webapps\sakai-opensyllabus-tool\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint
cd src\org\sakaiquebec\opensyllabus\shared
dir *.java /B/S > _sourcefiles.src
javac @_sourcefiles.src -cp "%GWT_HOME%/gwt-user.jar;\api;\model;\rpc;\events;" -d ..\..\..\..\..\bin
cd ..\..\..\..\..
copy .\src\org\sakaiquebec\opensyllabus\shared.gwt.xml .\bin\org\sakaiquebec\opensyllabus\
cd bin
jar cvf OsylShared.jar *
cd ..
@java -Xmx512m -cp "%~dp0\src;%~dp0\bin;%~dp0\bin\OsylShared.jar;%GWT_HOME%/gwt-user.jar;%GWT_HOME%/gwt-dev-windows.jar;%GWT_HOME%/gwt-incubator_1-4_final.jar" com.google.gwt.dev.GWTCompiler -logLevel ERROR -out "%~dp0\www" %* org.sakaiquebec.opensyllabus.OsylEditorEntryPoint

del /s /Q .\www\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint\*.cache.xml
del /s /Q .\www\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint\*.cache.js
del /s /Q .\www\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint\*-xs.js
del /s /Q .\www\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint\gwt.js

xcopy www\*.* ..\osyl\osyl-tool\tool\src\webapp /E /D /Y /Q

rem This line is only useful when deploying the GWT part (without restarting
rem Tomcat/Sakai). The negative side-effect of this line is a conflict when
rem deploying the whole project as it seems to block maven from time to time.
xcopy www\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint\*.* %CATALINA_HOME%\webapps\sakai-opensyllabus-tool\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint\ /E /D /Y /Q
