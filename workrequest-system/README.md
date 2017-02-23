## Synopsis

Work request system which allows consumers of the service to create and manage work requests in a priority based queue system.

## Project Overview

Spring Boot application which provides a REST api for creating and managing work requests. 

## Build and run

This a Maven project so build with the following command:
mvn clean install

Since this is a Spring Boot application you just need to execute the jar:
java -jar target/workrequest-system-1.0.0.jar


## Documentation

REST api docs are automatically generated using the JSONDoc library and can be accessed while the application is running at the following url: 
http://localhost:8080/workrequestsapi/jsondoc-ui.html

Then enter the following as the JSONDoc URL and hit Get Documentation: 
http://localhost:8080/workrequestsapi/jsondoc

The JSONDoc api page also allows you to fully test the api. The api is broken logically into resources. If you wish to perform specific operations like delete the top work request or get ids rather than work request objects then you supply the operation for the resource via the ‘operation’ query param. Some resources have further query params but this is all available on the api page.

The time format for the api is as follows for requests and responses that contain a time:
yyyy-MM-ddTHH:mm:ss.SSS e.g. 2017-02-19T14:27:16.024

To generate Javadocs run the following command: mvn site
These will be available in the following directory: target/site/apidocs

Unit test coverage is generated with Jacoco as part of the build. After you have followed the Build and run section you will find the report here:
/target/site/jacoco/index.html


## Logging

Logs will be output to the console, see terminal window after following steps in the Build and run section
