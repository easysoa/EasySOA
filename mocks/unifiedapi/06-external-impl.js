// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #6
 * Description: Step by step transition to an externally managed service, with a testing environment
 * Context : Integration
 * Author: Marwane Kalam-Alami
 */

var api = require('./api.js');

// Reproduce Scenario #1: Create Service Scaffolder Client for a given existing service endpoint
//                and #4: Enable monitoring and use its records to build a mock
// [ Scaffolder UI ] ==proxy==> [ WS ]

var imports = require('./04-scaffolder-monitoring.js');
var testEnv = imports.testEnv;
var monitoring = imports.monitoring;
var scaffolderClient = imports.scaffolderClient;
var scaffolderClientEndpoint = imports.scaffolderClientEndpoint;
var serviceEndpointToScaffold = imports.serviceEndpointToScaffold;

console.log("-------------------------------------");
console.log("[Scenario #6]");

// Implement external service

var externalImpl = new api.ExternalImpl("myBrainfckService");
var externalEndpoint = new api.ExternalEndpoint(externalImpl, "http://myservices.com/myBrainfckService");
testEnv.addExternalServiceEndpoint(externalEndpoint);

// Record some exchanges

monitoring.reset();
monitoring.save("exchangesForTests");
var records = monitoring.getRecords("exchangesForTests");

// Build and start (manually) a test suite

var testSuite = new api.TestSuite(externalImpl, records);
testSuite.run();

console.log("Done.");