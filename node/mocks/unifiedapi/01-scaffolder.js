// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #1 
 * Description: Create Service Scaffolder Client for a given existing service endpoint
 * Context : Light
 * Author: Marc Dutoo, Marwane Kalam-Alami
 */

var api = require('./api');

// Make the user choose a service

console.log("-------------------------------------");
console.log("[Scenario #1]");

var envFilter = [ "sandbox", "dev" ]; // "sandbox" is a sandboxed version of "staging" i.e. actual, existing services
var serviceEndpointToScaffold = api.selectServiceEndpointInUI(envFilter); // user also navigates or filters

// Create environment

var testEnv = new api.DevelopmentEnvironment("PureAirFlowers", "Sophie"); // on default business architecture

testEnv.addExternalServiceEndpoint(serviceEndpointToScaffold);

var scaffolderClient = new api.ScaffolderClientImpl("MyClient", serviceEndpointToScaffold);
var scaffolderClientEndpoint = testEnv.addServiceImpl(scaffolderClient);

// Launch scaffolder

console.log("Setting up environment "+testEnv.name);
if (testEnv.start()) { // starts scaffolder
    scaffolderClientEndpoint.display();
    console.log("Done.");
} else {
    console.error("Fail.");
}

// Exports for further scenarios

exports.serviceEndpointToScaffold = serviceEndpointToScaffold;
exports.testEnv = testEnv;
exports.scaffolderClient = scaffolderClient;
exports.scaffolderClientEndpoint = scaffolderClientEndpoint;
