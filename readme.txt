# A simple Frascati HTTP proxy with a web service detection.

## Introduction

The goal of this proxy is to detect web-services and register them in Nuxeo. It works only with WSDL and REST web-services at this moment.
It is build to run with Frascati (1.4 or more).

This proxy accept HTTP "GET" and HTTP SOAP POST requests.
The SCA component name to launch is "httpProxy". 

## Execution

To run this proxy :

- Build the project with the following maven command : "mvn clean install".
- Copy the generated "esperfrascatipoc-1.0-SNAPSHOT.jar" jar archive in the "sca-apps" folder of your frascati installation.
- Run Frascati with the command "frascati run httpProxy -libpath /YOUR_FRASCATI_HOME/sca-apps/esperfrascatipoc-1.0-SNAPSHOT.jar" (Don't forget to change YOU_FRASCATI_HOME with the path of Frascati in your installation)

The proxy server listen on the port 8082. A Nuxeo DM server with the features developed for the easysoa-model-demo must be running to register services.
The easysoa-model-demo can be found here : https://github.com/mkalam-alami/easysoa-model-demo

Remark : This software is a prototype and will be improved ! No warranty and no support is provided for this software.

----------------------------------

## For developpers

To build and run this project, the requirements are :

- Maven 2.2 or Maven 3 must be installed on your computer.
- Frascati 1.4 must be installed on your computer (Can be found here : http://forge.ow2.org/project/showfiles.php?group_id=329)
- The following additional libraries must be copied into the Fracati lib folder (check your m2 repository folder)  :
 	* org.restlet.ext.simple-2.1-M3.jar
	* org.restlet-2.1-M3.jar

----------------------------------

## Architecture

The proxy works with 2 modes :

- Discovery mode : 
In this mode, the proxy listen for messages, store them in a tree structure and, at the end of the run, analyse them to detect web applications, api's and services and store them in Nuxeo Easy-Soa model.

- Validated mode : 
In this mode, the proxy get the list of registered services from Nuxeo Easy-Soa Model and then listen for messages. When a message is received, it is compared to each entry in the nuxeo service list. If it match, a 'seen service' notification is sent to Nuxeo else the message is stored in a unknown message structure and an alert is send to the user. 
A mapper pattern is used to map data's form Nuxeo model to java objects (see SoaNodesJsonMapper.java) 

How to run ?
- Get the project and open it with your Eclipse IDE. Then execute the JUnit test "ApiDetectorTest". (A Nuxeo DM server with the features developed for the easysoa-model-demo must be running to register services).
