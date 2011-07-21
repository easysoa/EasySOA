# A simple Frascati HTTP proxy with a web service detection.

Sources availables at https://github.com/easysoa/esper-frascati-poc (Name will be changed soon).

## HTTP Proxy

### Introduction

The goal of this proxy is to detect web-services and register them in Nuxeo. It works only with WSDL and REST web-services at this moment.
It is build to run with Frascati (1.4 or more).

This proxy accept HTTP "GET" and HTTP SOAP POST requests. The original messages are not modified.

The SCA component name to launch is "httpProxy". This component describe the http proxy and the http proxy driver interface. 

### Execution (OUTDATED)

By default, the proxy works on the port 8082.

To run this proxy :

Solution A (preferred)
- run the project with the Maven command : "mvn -Prun"

Solution B
- Build the project with the following maven command : "mvn clean install".
- Copy the generated "esperfrascatipoc-1.0-SNAPSHOT.jar" jar archive in the "sca-apps" folder of your frascati installation.
- Run Frascati with the command "frascati run httpProxy -libpath /YOUR_FRASCATI_HOME/sca-apps/esperfrascatipoc-1.0-SNAPSHOT.jar" (Don't forget to change YOU_FRASCATI_HOME with the path of Frascati in your installation)

The proxy server listen on the port 8082. A Nuxeo DM server with the features developed for the easysoa-model-demo must be running in port 8080 to register services.
The easysoa-model-demo can be found here : https://github.com/easysoa/easysoa-model-demo

Remark : This software is a prototype and will be improved ! No warranty and no support is provided for this software.

----------------------------------

### For developpers (OUTDATED)

To build and run this project, the requirements are :

- Maven 2.2 or Maven 3 must be installed on your computer.
- Frascati 1.4 must be installed on your computer (Can be found here : http://forge.ow2.org/project/showfiles.php?group_id=329)
- The following additional libraries must be copied into the Fracati lib folder (check your m2 repository folder)  :
	* httpclient-4.1.1.jar
	* log4j-1.2.16.jar
	* jersey-client-1.7-ea06.jar
 	* jersey-core-1.7-ea06.jar
	* esper-4.2.0.jar

----------------------------------

### Architecture

The proxy works with 2 modes :

- Discovery mode : 
In this mode, the proxy listen for messages, store them in a tree structure and, at the end of the run, analyze them to detect web applications, api's and services and store them in Nuxeo Easy-Soa model. All detected services are registered in Nuxeo.
Heuristics are used in combination with a tree to detect api's. 

- Validated mode : 
In this mode, the proxy get the list of registered services from Nuxeo Easy-Soa Model and then listen for messages. When a message is received, it is compared to each entry in the nuxeo service list. If it match, a 'seen service' notification is sent to Nuxeo else the message is stored in a unknown message structure and an alert is send to the user. 
A mapper pattern is used to map data's form Nuxeo model to java objects (see SoaNodesJsonMapper.java) 

How to run the JUnit test ?
- Get the project and open it with your Eclipse IDE. Then execute the JUnit test "ApiDetectorTest". (A Nuxeo DM server with the features developed for the easysoa-model-demo must be running to register services).

Architectural choices :

The proxy works on FraSCAti because there is a lot of extension capabilities (intents, injection ...). In addition, it is possible to deploy it independently or embedded in EasySOA, thanks to SCA and OSGI. 
The main class of the proxy implements the Servlet interface. HttpClient form Apache httpComponents library is used to redirect the request to the original recipient.

Esper is used for event stream processing to support heavy loads. His role is to aggregate the received messages and to regroup them by services before to send them to Nuxeo for registration. This is to avoid to flood Nuxeo with registration requests when a huge amount of messages is received by the proxy.

Handlers are used to detect the kind of the received messages. Indeed, the way to register services from a SOAP message is somewhat different from that to register form REST Messages. It is easy to extends the capabilities of the proxy by adding new handlers. In a future version, handlers will be loaded automatically.

## Http proxy driver interface for HTTP Proxy

A client interface is available for the proxy. This interface can be used to start/stop proxy run. A proxy run is a collection of recorded messages that can be 'replayed'.
To record messages, a new proxy run must be started. When the run is stopped, the recorded messages are analyzed and the corresponding web services are recorded in Nuxeo EasySOA model.

The interface is started automatically with the proxy and is only available in 'console mode'.
The way to send command is to send an http request on the port 8083 (by default) :

    * To start a new proxy run : /startNewRun/{runName}
    * To stop the current proxy run : /stopCurrentRun
    
In future versions, new features will be added :'replay' function, proxy mode change, informations display like message number in the current run .., and a true graphical interface.
