Test Automation Next Generation (former VEM)

Test Automation Next Generation is a test automation tool that tries to make life easier for those who want to automate their testing.
It uses WebDriver and TestNG and builds a layer above them that makes it easy to automate UI tests.
It uses database connections and has it's own set of sql test template.
It uses pkdiff to compare images.

It also adds verification of structure and/or data in simple or complex formats of html/xml in a new way.
By using static verification, semi dynamic verification or dynamic verification towards databases you can "configure" what to verification and how to verify.

The verification could be done during test run with WebDriver or could be called from external application like SoapUI by calling the jar file.

Todo: Remove dependency to SqlJdbc
In order to build the project you need to install a jar files which maven can't find on Internet:
Find sqljdbc4.jar in internet and download localy and run:
mvn install:install-file -Dfile=sqljdbc4.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=3.0 -Dpackaging=jar

Build
mvn clean install

Todo: Test this
Test
mvn clean test

Examples and Help
https://github.com/majramo/tang


License
Copyright 2015 Majid Aram

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, 
software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
either express or implied. See the License for the specific language governing permissions and limitations under the License.




For more info see folder doc
