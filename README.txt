Test Automation Next Generation (former VEM)

Test Automation Next Generation is a test automation tool that tries to make life easier for those who want to automate their testing.
It uses WebDriver and TestNG and builds a layer above them that makes it easy to automate UI tests.
It uses RestDriver to send requests and get responses.
It uses database connections and has it's own set of sql test template.

It also adds verification of structure and/or data in simple or complex formats of html/xml in a new way.
By using static validation, semi dynamic validation or dynamic validation towards databases you can "configure" what to validate and how to validate.

The validations could be done during test run with WebDriver, when running RestDriver or could be called from external application like SoapUI by calling the jar file.


In order to build the project you need to install several jar files which maven can't find on Internet:
mvn install:install-file -Dfile=JAutomate.jar -DgroupId=jautomate -DartifactId=Jautomate -Dversion=1 -Dpackaging=jar
mvn install:install-file -Dfile=sqljdbc4.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=3.0 -Dpackaging=jar

Build
mvn clean install

Test
mvn clean test

Test with reportyng example
mvn clean test -DtestFile=dummyTestng -P tang site org.reportyng:reporty-ng:1.2:reportyng
file:///Users/majidaram/dev/tang-examples/target/test-classes/site/tang-examples/index.html

Examples and Help
https://github.com/majramo/tang


License
Copyright 2014 Majid Aram

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, 
software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
either express or implied. See the License for the specific language governing permissions and limitations under the License.




For more info see folder doc
