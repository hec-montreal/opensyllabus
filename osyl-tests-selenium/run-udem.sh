#!/bin/sh

# if ./run debug then
#java -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y -classpath classes:lib/testng-5.9-jdk15.jar:lib/selenium-java-client-driver.jar:lib/selenium-grid-tools-1.0.3.jar:lib/junit-4.6.jar:lib/commons-logging-1.0.4.jar org.testng.TestNG OSYLTestSuiteUdeM.xml
# else
java -classpath classes:lib/testng-5.9-jdk15.jar:lib/selenium-java-client-driver.jar:lib/selenium-grid-tools-1.0.3.jar:lib/junit-4.6.jar:lib/commons-logging-1.0.4.jar org.testng.TestNG OSYLTestSuiteUdeM.xml
#endif