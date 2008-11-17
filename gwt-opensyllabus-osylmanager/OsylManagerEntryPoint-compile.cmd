@java -Xmx512m -cp "%~dp0\src;%~dp0\bin;%GWT_HOME%/gwt-user.jar;%GWT_HOME%/gwt-dev-windows.jar;" com.google.gwt.dev.GWTCompiler -out "%~dp0\www" %* org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint
del /s /Q www mkdir bin
del /s /Q bin
del /s /Q ..\osyl-manager\osyl-manager-tool\tool\src\webapp\org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint
del /s /Q %CATALINA_HOME%\webapps\sakai-opensyllabus-manager-tool\org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint

del /s /Q .\www\org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint\*.cache.xml
del /s /Q .\www\org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint\*.cache.js
del /s /Q .\www\org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint\*-xs.js
del /s /Q .\www\org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint\gwt.js

java -cp "%~dp0\src;%~dp0\bin;%GWT_HOME%/gwt-user.jar;%GWT_HOME%/gwt-dev-windows.jar;" com.google.gwt.dev.GWTCompiler -out "%~dp0\www" %* org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint

xcopy www\*.* ..\osyl-manager\osyl-manager-tool\tool\src\webapp /E /D /Y /Q
xcopy www\org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint\*.* %CATALINA_HOME%\webapps\sakai-opensyllabus-manager-tool\org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint\ /E /D /Y /Q