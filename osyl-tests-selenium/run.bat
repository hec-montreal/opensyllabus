java -cp classes;lib\testng-5.9-jdk15.jar;lib\selenium-server-standalone-2.19.0.jar;lib\selenium-grid-tools-1.0.8.jar;lib\commons-logging-1.0.4.jar org.testng.TestNG OSYLTestSuite-dev.xml


REM rename file according to date
set fileOut=TestReport_281_%date%_%time:~0,2%%time:~3,2%.html
copy test-output\emailable-report.html test-output\%fileOut% 


