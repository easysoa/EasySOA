# EasySOA Model Demo

## Introduction

Turns Nuxeo into a simple service registry. Contains also a "Discovery by browsing" web client, that allows to scrape web pages during navigation and send found WSDLs to Nuxeo through a custom REST API.

*NOTE: This demo code is neither fully functional nor stable.*

## Repository structure

The code is mainly split into 5 projects:

 * `easysoa-demo-model-core`: The Nuxeo core contributions.
 * `easysoa-demo-model-web`: The Nuxeo web contributions, tightly linked to the `core` project.
 * `easysoa-demo-rest`: The REST API used by the service finder.
 * `easysoa-demo-dashboard`: A basic dashboard "gadget".
 
 You can build these bundles by typing `mvn clean install` from this folder.

## Use and development

Please see [this repository's wiki](https://github.com/easysoa/easysoa-model-demo/wiki) for further information.
