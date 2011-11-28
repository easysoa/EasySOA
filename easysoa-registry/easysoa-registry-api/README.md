# EasySOA Registry API

## Description

These projects contains common resources used by the service registry, and two implementations of a single EasySOA API.

Note that the API is very basic and in early development, and is subject to evolutions without warnings.

## Building & Using the API as a self-sufficient JAR

In order to access the model remotely, you only need to grab the `easysoa-registry-api-remote` project. To build it as a reusable JAR, type:

`mvn clean package`

Then pick **easysoa-registry-api-remote-SOMEVERSION-jar-with-dependencies.jar** and include it in your project.

## Examples

See the [example classes](https://github.com/easysoa/EasySOA/tree/master/easysoa-registry/easysoa-registry-api/easysoa-registry-api-remote/src/main/java/org/easysoa/examples) embedded in the `easysoa-registry-api-remote` project.
