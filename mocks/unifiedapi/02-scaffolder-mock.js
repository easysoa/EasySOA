// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #2 
 * Description: Create Service Scaffolder Client from mock of a given existing service implementation
 * Context : Light
 * Author: Marc Dutoo, Marwane Kalam-Alami
 */

var api = require('./api');

// Make the user choose a service

console.log("-------------------------------------");
console.log("[Scenario #2]");

var user = "Sophie";
var envFilter = [ "sandbox", "dev" ];
var serviceEndpointToScaffold = api.selectServiceEndpointInUI(envFilter);

// Create environment

var testEnv = new api.DevelopmentEnvironment("PureAirFlowers", "Sophie");

var serviceMock = new api.JavascriptImpl("MyMock", options={isMock: true}, serviceImplToMock=serviceEndpointToScaffold.getImpl());
var serviceMockEndpoint = testEnv.addServiceImpl(serviceMock);

var scaffolderClient = new api.ScaffolderClientImpl("MyClient", serviceMockEndpoint);
var scaffolderClientEndpoint = testEnv.addServiceImpl(scaffolderClient);

// Launch scaffolder

console.log("Setting up environment "+testEnv.name);
if (testEnv.start()) {
    scaffolderClientEndpoint.display();
    console.log("Done.");
} else {
    console.error("Fail.");
}