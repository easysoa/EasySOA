# EasySOA Model Demo

## Introduction

Turns Nuxeo into a simple service registry. Contains also a "Service Finder" web client, that allows to scrape web pages during navigation and send found WSDLs to Nuxeo through a custom REST API.

*NOTE: This demo code is neither fully functional nor stable.*

## Installation

### Nuxeo registry

The Nuxeo Installation Guide explains how to install and run Nuxeo DM: <http://doc.nuxeo.com/display/DMDOC/Nuxeo+DM+-+Installation+Guide>. After having installed Nuxeo 5.4:

 * Copy `lib/` contents to `nuxeo-dm/lib/`
 * Copy `build/` contents to `nuxeo-dm/nxserver/plugins/`

### Service finder

This tool doesn't need any installation, since it's only made of client-side Javascript. Simply run the `index.html` file in `easysoa-servicefinder`.

*NOTE: The Nuxeo registry must be launched in order to function properly. If Nuxeo is hosted on a different computer than the client, please change `js/NavbarView.js:26` accordingly.*

## Features

### Nuxeo registry

TODO

### Service finder

The idea is to automatically find WSDL files while the user navigates, then make him fill a form to fill some metadata and send the files to the Nuxeo repository.

For now, the parser only looks for links whose URL ends with "wsdl", but we consider supporting several other ways to find services (from trying common URL patterns of services frameworks -"/cxf", etc. -, to custom scripts or URL patterns given by users).

## About the sources

The code is split into 4 projects:

 * `plugins/easysoa-demo-model-core`: The Nuxeo core contributions.
 * `plugins/easysoa-demo-model-web`: The Nuxeo web contributions, tightly linked to the `core` project.
 * `plugins/easysoa-demo-rest`: The REST API used by the service finder.
 * `easysoa-servicefinder`: The service finding tool.
