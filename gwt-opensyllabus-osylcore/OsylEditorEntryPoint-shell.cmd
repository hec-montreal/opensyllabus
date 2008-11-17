@java -Xmx512m -Xdebug -Xrunjdwp:transport=dt_socket,address=9000,server=y,suspend=n -cp "%~dp0\src;%~dp0\bin;%~dp0\bin\GwtModel.jar;%GWT_HOME%/gwt-user.jar;%GWT_HOME%/gwt-dev-windows.jar;%GWT_HOME%/gwt-incubator_1-4_final.jar" com.google.gwt.dev.GWTShell -out "%~dp0\www" %* org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/OsylEditorEntryPoint.html

