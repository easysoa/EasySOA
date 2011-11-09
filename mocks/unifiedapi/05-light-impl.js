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

var api = require('./api');

// Reproduce Scenario #1: Create Service Scaffolder Client for a given existing service endpoint
//                and #4: Enable monitoring and use its records to build a mock

var imports = require('./04-scaffolder-monitoring.js');
var testEnv = imports.testEnv;
var scaffolderClient = imports.scaffolderClient;
var scaffolderClientEndpoint = imports.scaffolderClientEndpoint;
var serviceEndpointToScaffold = imports.serviceEndpointToScaffold;

console.log("-------------------------------------");
console.log("[Scenario #5]");


// Replace scaffolder UI with a templating UI
// [ Templating UI ] =========> [ WS ]

var uiServiceImpl = new api.TemplatingUIImpl("MyServiceUI", scaffolderClient);
var uiServiceEndpoint = testEnv.addServiceImpl(uiServiceImpl);
testEnv.removeServiceImpl(scaffolderClient);

// Add proxy to templating UI
// [ Templating UI ] ==proxy==> [ WS ]

var uiMonitoring = new api.MonitoringProxyFeature("uimonit");
uiServiceEndpoint.useProxyFeature(uiMonitoring);

// Implement JS service & template UI
// [ Templating UI ] ==proxy==> [ JS ] =========> [ WS ]

var jsServiceImpl = new api.JavascriptImpl("myjsservice");
jsServiceImpl.edit(); // Implement JS service
var jsServiceEndpoint = testEnv.addServiceImpl(jsServiceImpl, autoUpdate=true);
uiServiceImpl.edit(); // Make template use new JS service

// Record some exchanges and build test suite to be executed on each service impl. update

uiMonitoring.reset();
uiMonitoring.save("exchangesForTests");
var records = uiMonitoring.getRecords("exchangesForTests");
var testSuite = new api.TestSuite(records);
jsServiceEndpoint.registerOnUpdateListener(testSuite);

console.log("Done.");