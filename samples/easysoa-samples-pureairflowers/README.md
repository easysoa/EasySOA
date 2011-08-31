# EasySOA PureAirFlowers Demo

## Introduction

PureAirFlowers is a small imaginary company that sold depolluting plants.

This demo contains a SOAP CXF server that provide a single operation web service to get the number of orders for a client. With the WSDl provided by the web service, a client HTML form can be automatically generated. A REST/SOAP proxy is charged to make the translation between the SOAP protocol used by the CXF server and the REST request send by the client. Additionnaly, the proxy can works with intents. Intents are triggered each time a request is send by the client and can do various tasks as logging, security check, filtering requests ...

This demo works with several technologies :

 * Soap web service implemented by a CXF server.
 * REST/SOAP proxy implemented with Frascati.
 * WSDL2HTML xslt transformation to generate HTML form.

*NOTE: This demo code is neither fully functional nor stable.*

## Prerequisites

In order to launch the REST/SOAP proxy, you will need to get a 1.4 version of FraSCAti, and extract it in the `distrib/frascati` folder.

## Execution

There are three main part to launch separately :

 * `pureAirFlowers`: SOAP CXF server (first)
 * `frascatiProxy`: ServiceUiScaffolder proxy (second)
 * `serviceUiScaffolder`: HTML form (third)

You can build the whole repository by typing this command line from the root:

`mvn clean install`

Launch `distrib/pafServices/start-pafServices.sh` then `distrib/frascati/start-lightProxy.sh`, and you will be able to use the demo from the pre-generated HTML page `serviceUiScaffolder/html/PureAirFlowers.html`. 

The CXF server exposes a SOAP web service available at "http://localhost:9010/PureAirFlowers?wsdl". The proxy talks with the cxf server and expose a rest service at "http://localhost:7001/". The CXF server must be launched before the Frascati proxy.

Check the readmes for each project to get more details on installation and execution instructions.
