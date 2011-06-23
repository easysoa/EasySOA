# EasySOA Model Demo

## Introduction

Turns Nuxeo into a simple service registry. Contains also a "Discovery by browsing" web client, that allows to scrape web pages during navigation and send found WSDLs to Nuxeo through a custom REST API.

*NOTE: This demo code is neither fully functional nor stable.*

## Setup

### Nuxeo registry

The Nuxeo Installation Guide explains how to install and run Nuxeo DM: <http://doc.nuxeo.com/display/DMDOC/Nuxeo+DM+-+Installation+Guide>. After having installed Nuxeo 5.4.1:

 * Copy `lib/` contents to `nuxeo-dm/lib/`
 * Copy `build/` contents to `nuxeo-dm/nxserver/plugins/`

If you're updating from a previous version of the model, please reset all your data by deleting the following folder : `nuxeo-dm/nxserver/data`.

### Discovery by browsing

In order to use this tool, you have to launch a specific HTTP proxy and configure your browser to use it.

* In order to launch the proxy, run the `start-proxy.sh` (or `.bat`) script in the `discovery-by-browsing` folder.
* Launch the web server with the `start-web.sh` (or `.bat`) script (same folder), then browse to http://127.0.0.1:8083/easysoa/core/.

*NOTE: The Nuxeo registry, web proxy, web server and web client are configured to be run on the same machine.*

## Features

### Nuxeo registry

#### General

The registry is based on three document types:

 * The "Service" doctype represents the specifications/documentation of a single service.
 * "Service API" is an aggregation of services. In the case of SOAP services, it can store and parse a WSDL file.
 * "Appli Impl" stands for an application, i.e. the services root. It is called "application implementation" in contrast to "business application" (technical, concrete vs business-oriented information).

The registry arborescence is made to reflect the different aggregations : Applications contain APIs, and APIs contain either Services or other, lower level APIs.

_*(Doesn't work yet for this version)*_ You can navigate through the documents using differents navigations trees (see the tabs on the left). They represent the idea of showing the model from different points of view to different user profiles:

 * *Navigation by Application*: For the business users
 * *Navigation by Server*: For the administrators
 * *Navigation by Service*: For the architects

#### REST services

The registry can be filled thanks to RESTful webservices:

* http://localhost:8080/nuxeo/site/easysoa/notification/appliimpl
* http://localhost:8080/nuxeo/site/easysoa/notification/api
* http://localhost:8080/nuxeo/site/easysoa/notification/service

Visit http://localhost:8080/nuxeo/site/easysoa/ to see how to use them. For now, no authentication is required.

### Discovery by browsing

The idea is to automatically find WSDL files while the user navigates, then make him fill a form to fill some metadata and send the files to the Nuxeo repository.

For now, the parser only looks for links whose URL ends with "wsdl", but we consider supporting several other ways to find services (from trying common URL patterns of services frameworks -"/cxf", etc. -, to custom scripts or application-specific URL patterns).

*NOTE: In order to see uploaded WSDLs in Nuxeo, you may need to refresh the `Descriptors/WSDL/` folder (look for an icon in the top-right corner).*

## About the sources

The code is split into 4 projects:

 * `plugins/easysoa-demo-model-core`: The Nuxeo core contributions.
 * `plugins/easysoa-demo-model-web`: The Nuxeo web contributions, tightly linked to the `core` project.
 * `plugins/easysoa-demo-rest`: The REST API used by the service finder.
 * `discovery-by-browsing`: The service discovery tool.