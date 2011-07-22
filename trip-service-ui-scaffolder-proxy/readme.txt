# A simple REST/SOAP static proxy.

## Introduction

The goal of this proxy is to establish a communication between a REST Request and a SOAP webservice.
It is build to run with Frascati (1.4 or more).

This proxy is designed to run with the Galaxy travel Demo.
The SCA component name to launch is "RestSoapProxy". 

## Execution

To run this proxy :

- Build the project with the following maven command : "mvn clean install".
- Run the project with the command : "mvn -Prun".

Remark : The Trip travel demo server must be running when the ServiceUIScaffolder proxy is launched otherwise an error is throwed and the proxy stops.
The proxy server listen on the port 7001 and talk with the trip travel demo server on the port 9000.

*********************************

FOR DEVELOPERS

To build this project, the requirements are :

- Frascati version 1.4 (You can get it at "http://forge.ow2.org/svnsnapshots/frascati-svn-latest.tar.gz").
- Maven 3 must be installed on your computer.

Before you can launch the proxy demo, you need to build Frascati in order to fill the maven repository with Frascati libs.
Unpack the Frascati archive in a folder and then launch the "build-all.sh" script.

To reduce to build time, you can modify the "build-all.sh" script by adding "-Dmaven.test.skip=true" parameter to the mvn commands.
eg : "mvn -Dmaven.test.skip=true -f org.eclipse.stp.sca.model/pom.xml clean install"
