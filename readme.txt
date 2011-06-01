# A simple Frascati HTTP proxy with a web service detection.

## Introduction

The goal of this proxy is to detect web-services and register them in Nuxeo. It works only with WSDL and REST web-services at this moment.
It is build to run with Frascati (1.4 or more).

This proxy accept only HTTP "GET" requests.
The SCA component name to launch is "httpProxy". 

## Execution

To run this proxy :

- Build the project with the following maven command : "mvn clean install".

The proxy server listen on the port 8082. A Nuxeo DM server with the features developed for the easysoa-model-demo must be running to register services.
The easysoa-model-demo can be found here : https://github.com/mkalam-alami/easysoa-model-demo

Remark : This software is an alpha prototype and will be improved ! No warranty and no support is provided for this software.

*********************************

FOR DEVELOPERS

To build this project, the requirements are :

- Frascati version 1.4 (You can get it at "http://forge.ow2.org/svnsnapshots/frascati-svn-latest.tar.gz").
- Maven 2 must be installed on your computer.

Before you can launch the proxy demo, you need to build Frascati in order to fill the maven repository with Frascati libs.
Unpack the Frascati archive in a folder and then launch the "build-all.sh" script.

To reduce to build time, you can modify the "build-all.sh" script by adding "-Dmaven.test.skip=true" parameter to the mvn commands.
eg : "mvn -Dmaven.test.skip=true -f org.eclipse.stp.sca.model/pom.xml clean install"


*********************************

@TODO

-- Add a "group by" Esper statement to count how much time a service is called. Then record the results every n minutes in nuxeo.
-- Add a "true" log system (log4j...) instead of using the System.out.println() log system.
-- Set the parameters outside the code (proxy listening port, Nuxeo address, Esper statements ...)
--