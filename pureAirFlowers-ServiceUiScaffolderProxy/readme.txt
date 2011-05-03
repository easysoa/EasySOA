# A simple REST/SOAP static proxy.

## Introduction

The goal of this proxy is to establish a communication between a REST Request and a SOAP webservice.
It is build to run with Frascati (1.4 or more).

This proxy is designed to run with the PureAirFlowers ServiceUIScaffolder and the CXF server examples.
The SCA component name to launch is "RestSoapProxy". 
An other component "RestSoapProxy_withIntents" works with the Log intent and the autorearm fuse intent. To activate this component, change the value of the property "composite.file" in the "pom.xml" file with the value "RestSoapProxy_withIntents". 

If you want to use the intents, you need to build them first !

## Execution

To run this proxy :

- Build the project with the following maven command : "mvn clean install".
- Run the project with the command : "mvn -Prun".

Remark : The PureAirFlower CXF server must be running when the ServiceUIScaffolder proxy is launched otherwise an error is throwed and the proxy stops.
The proxy server listen on the port 7001 and talk with the CXF server on the port 9001.

*********************************

FOR DEVELOPERS

To build this project, the requirements are :

- Frascati version 1.4 (You can get it at "http://forge.ow2.org/svnsnapshots/frascati-svn-latest.tar.gz").
- Maven 2 must be installed on your computer.

Before you can launch the proxy demo, you need to build Frascati in order to fill the maven repository with Frascati libs.
Unpack the Frascati archive in a folder and then launch the "build-all.sh" script.