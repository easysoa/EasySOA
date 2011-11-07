// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #3 
 * Description: Create mock and make it replace the scaffolder client's targeted service
 * Context : Light
 * Author: Marwane Kalam-Alami
 */

var api = require('./api.js');

// Reproduce Scenario #01: Create Service Scaffolder Client for a given existing service endpoint

console.log("------------------------------------");
console.log("[Scenario #1]");
var imports = require('./01-scaffolder.js');
testEnv = imports.testEnv;
scaffolderClientEndpoint = imports.scaffolderClientEndpoint;
serviceEndpointToScaffold = imports.serviceEndpointToScaffold;
console.log("------------------------------------");

// Create mock of existing endpoint

var serviceMock = api.createMockServiceImpl(serviceEndpointToScaffold); // TODO createImpl("js", mock=true, params=serviceEndpointToScaffold ?
var serviceMockEndpoint = api.addServiceImpl(testEnv, serviceMock);

// Update Scaffolder

api.setTargetEndpoint(scaffolderClientEndpoint, serviceMockEndpoint, testEnv);
api.display(scaffolderClientEndpoint);
console.log("Done.");