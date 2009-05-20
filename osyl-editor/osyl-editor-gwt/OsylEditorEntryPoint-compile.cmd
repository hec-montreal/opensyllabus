del /s /Q www
del /s /Q ..\osyl\osyl-tool\tool\src\webapp\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint
del /s /Q %CATALINA_HOME%\webapps\sakai-opensyllabus-tool\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint

@java -Xmx512m -cp "%~dp0\src;%M2_REPO%\org\sakaiquebec\opensyllabus\sakai-opensyllabus-common-shared\M2\sakai-opensyllabus-common-shared-M2.jar;%GWT_HOME%\gwt-user.jar;%GWT_HOME%\gwt-dev-windows.jar;%GWT_HOME%\gwt-incubator_1-5_Dec_28.jar;%GWT_HOME%\gwt-fx 3.0.0.jar;%GWT_HOME%\gwt-dnd-2.5.6.jar;%GWT_HOME%\gwt-beans-binding-0.2.3.jar;%GWT_HOME%\gwtx-1.5.2.jar;%GWT_HOME%\gwt-mosaic-0.1.9.jar" com.google.gwt.dev.GWTCompiler -logLevel ERROR -out "%~dp0\www" %* org.sakaiquebec.opensyllabus.OsylEditorEntryPoint

xcopy www\*.* ..\osyl\osyl-tool\tool\src\webapp /E /D /Y /Q

rem This line is only useful when deploying the GWT part (without restarting
rem Tomcat/Sakai). The negative side-effect of this line is a conflict when
rem deploying the whole project as it seems to block maven from time to time.
xcopy www\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint\*.* %CATALINA_HOME%\webapps\sakai-opensyllabus-tool\org.sakaiquebec.opensyllabus.OsylEditorEntryPoint\ /E /D /Y /Q
