// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scnario #2 
 * Description: Create Service Scaffolder Client from mock of a given existing service implementation
 * Context : Light
 * Author: Marwane Kalam-Alami
 */

var api = require('./api.js');

// Make the user choose a service

var user = "Sophie";
var envFilter = [ "sandbox", "dev" ]; // "sandbox" is a sandboxed version of "staging" i.e. actual, existing services
var serviceEndpointToScaffold = api.selectServiceEndpointInUI(envFilter); // user also navigates or filters

// Create mock

var serviceMock = api.createMockServiceImpl(serviceEndpointToScaffold);

// Build environment

var testEnv = api.createEnvironment("Light", user); // on default business architecture
var serviceMockEndpoint = api.addServiceImpl(testEnv, serviceMock);
var scaffolderClient = api.createScaffolderClient(testEnv, serviceMockEndpoint);
api.addServiceImpl(testEnv, scaffolderClient);

// Launch scaffolder

if (api.start(testEnv)) { // starts scaffolder
    api.display(scaffolderClient.clientUi);
    console.log("Done.");
} else {
    console.error("Fail.");
}