@java -Xmx512m -cp "%~dp0\src;%~dp0\bin;%GWT_HOME%/gwt-user.jar;%GWT_HOME%/gwt-dev-windows.jar;" com.google.gwt.dev.GWTCompiler -out "%~dp0\www" %* org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint
del /s /Q www mkdir bin
del /s /Q bin
del /s /Q ..\osyl-admin\osyl-admin-tool\tool\src\webapp\org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint
del /s /Q %CATALINA_HOME%\webapps\sakai-opensyllabus-admin-tool\org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint

del /s /Q .\www\org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint\*.cache.xml
del /s /Q .\www\org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint\*.cache.js
del /s /Q .\www\org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint\*-xs.js
del /s /Q .\www\org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint\gwt.js

java -cp "%~dp0\src;%~dp0\bin;%GWT_HOME%/gwt-user.jar;%GWT_HOME%/gwt-dev-windows.jar;" com.google.gwt.dev.GWTCompiler -out "%~dp0\www" %* org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint

xcopy www\*.* ..\osyl-admin\osyl-admin-tool\tool\src\webapp /E /D /Y /Q
xcopy www\org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint\*.* %CATALINA_HOME%\webapps\sakai-opensyllabus-admin-tool\org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint\ /E /D /Y /Q