# EasySOA Model Demo

## Introduction

Turns Nuxeo into a simple service registry. Contains also a "Discovery by browsing" web client, that allows to scrape web pages during navigation and send found WSDLs to Nuxeo through a custom REST API.

Please see [this repository's wiki](https://github.com/easysoa/easysoa-model-demo/) for further information.

*NOTE: This demo code is neither fully functional nor stable.*

## Repository structure

The code is split into 4 projects:

 * `plugins/easysoa-demo-model-core`: The Nuxeo core contributions.
 * `plugins/easysoa-demo-model-web`: The Nuxeo web contributions, tightly linked to the `core` project.
 * `plugins/easysoa-demo-rest`: The REST API used by the service finder.
 * `discovery-by-browsing`: The service discovery tool.