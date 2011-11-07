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
 * Author: Marwane Kalam-Alami
 */

var api = require('./api.js');

// Make the user choose a service

var user = "Sophie";
var envFilter = [ "sandbox", "dev" ];
var serviceEndpointToScaffold = api.selectServiceEndpointInUI(envFilter);

// Create environment

var testEnv = api.createEnvironment("Light", user, "PureAirFlowers");

var serviceMock = api.createMockServiceImpl(serviceEndpointToScaffold); // TODO createImpl("js", mock=true, params=serviceEndpointToScaffold ?
var serviceMockEndpoint = api.addServiceImpl(testEnv, serviceMock);

var scaffolderClient = api.createScaffolderClient(testEnv, serviceMockEndpoint);
var scaffolderClientEndpoint = api.addServiceImpl(testEnv, scaffolderClient);

// Launch scaffolder

console.log("Setting up environment "+testEnv.name);
if (api.start(testEnv)) {
    api.display(scaffolderClientEndpoint);
    console.log("Done.");
} else {
    console.error("Fail.");
}