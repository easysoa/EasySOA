# EasySOA Model Demo

## Introduction

Turns Nuxeo 5.5 into a simple service registry.

## Repository structure

The code is mainly split into 6 projects:

 * `easysoa-registry-core`: The Nuxeo core contributions.
 * `easysoa-registry-web`: The Nuxeo web contributions, tightly linked to the `core` project.
 * `easysoa-registry-rest`: The REST API used by other EasySOA components to fill the model.
 * `easysoa-registry-api`: A library to help external projects work with the model and interact with the registry.
 * `easysoa-registry-dashboard`: A basic dashboard "gadget".
 * `easysoa-registry-dependencies`: A package of all libraries needed for the service registry.

## Building

You can build these bundles by typing `mvn clean install` from this folder. Then deploy the files by either copying the `target` folder contents to your Nuxeo setup, or [by using Buildr](https://github.com/easysoa/EasySOA/wiki/Releasing-EasySOA).

See [this repository's wiki](https://github.com/easysoa/EasySOA/wiki) for further information.
