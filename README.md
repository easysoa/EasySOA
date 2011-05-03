# EasySOA PureAirFlowers Demo

## Introduction

PureAirFlowers is a small imaginary company that sold depolluting plants.

This demo contains a SOAP CXF server that provide a single operation web service to get the number of orders for a client. With the WSDl provided by the web service, a client HTML form can be automatically generated. A REST/SOAP proxy is charged to make the translation between the SOAP protocol used by the CXF server and the REST request send by the client. Additionnaly, the proxy can works with intents. Intents are triggered each time a request is send by the client and can do various tasks as logging, security check, filtering requests ...

This demo works with several technologies :

 * Soap web service implemented by a CXF server.
 * REST/SOAP proxy implemented with Frascati.
 * WSDL2HTML xslt transformation to generate HTML form.

*NOTE: This demo code is neither fully functional nor stable.*

## Execution

There are three main part to launch separately :

 * SOAP CXF server (first)
 * ServiceUiScaffolder proxy (second)
 * HTML form (third)

Check the readme's for each project to get installation and execution instructions.

## About the sources

The code is split into 3 main projects and 2 optional projects :

 * `pureAirFlowers-easysoa-demo-cxf-server`: The SOAP CXF server.
 * `pureAirFlowers-ServiceUiScaffolderProxy`: The ServiceUiScaffolder proxy project.
 * `pureAirFlowers-ServiceUiScaffolder`: The WSDL2HTML xslt transformation project.
 * `pureAirFlowers-autoRearmFuseIntent`: The autorearm intent project.
 * `pureAirFlowers-logIntent`: The log intent project.
