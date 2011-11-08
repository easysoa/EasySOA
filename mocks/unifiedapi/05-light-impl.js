// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #5
 * Description: Step by step transition to a Light service testing environment
 * Context : Light
 * Author: Marwane Kalam-Alami
 */

var api = require('./api.js');

// Reproduce Scenario #1: Create Service Scaffolder Client for a given existing service endpoint
//                and #4: Enable monitoring and use its records to build a mock

var imports = require('./04-scaffolder-monitoring.js');
var testEnv = imports.testEnv;
var scaffolderClient = imports.scaffolderClient;
var scaffolderClientEndpoint = imports.scaffolderClientEndpoint;
var serviceEndpointToScaffold = imports.serviceEndpointToScaffold;

console.log("-------------------------------------");
console.log("[Scenario #5]");

// create template UI impl to replace scaffolder (LATER impl rather linked or forked from other env)

var uiServiceImpl = new api.TemplatingUIImpl("pafUI");
var uiServiceEndpoint = testEnv.addServiceImpl(uiServiceImpl);
testEnv.removeServiceImpl(scaffolderClient);

// add WS proxy + js impl between template UI and mock

// record exchanges and let the user tailor a recording session that is a test suite

// setup test suite to be called on each js impl changes

console.log("Done.");