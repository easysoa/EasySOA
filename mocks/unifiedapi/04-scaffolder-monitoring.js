// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #4
 * Description: Enable monitoring and use its records to build a mock
 * Context : Light
 * Author: Marwane Kalam-Alami
 */

var api = require('./api.js');

// Reproduce Scenario #1: Create Service Scaffolder Client for a given existing service endpoint

var imports = require('./01-scaffolder.js');
var testEnv = imports.testEnv;
var scaffolderClient = imports.scaffolderClient;
var scaffolderClientEndpoint = imports.scaffolderClientEndpoint;
var serviceEndpointToScaffold = imports.serviceEndpointToScaffold;

console.log("-------------------------------------");
console.log("[Scenario #4]");


// Add monitoring to scaffolder

scaffolderClientEndpoint.useProxyFeature(new api.MonitoringProxyFeature("mymonit"));

// Retrieve and control scaffolder monitoring

var monitoring = scaffolderClientEndpoint.getProxyFeature("mymonit");
monitoring.save("myrun");
monitoring.save("myrun2");
monitoring.restore("myrun");
monitoring.reset();

// Use monitoring session to build mock

var serviceMock = new api.JavascriptImpl("MyMock", isMock=true, serviceImplToMock=serviceEndpointToScaffold.getImpl());
serviceMock.feedMockWithRecords(monitoring.getRecords("myrun"));

console.log("Done.");


//Exports for further scenarios

exports.testEnv = testEnv;
exports.scaffolderClient = scaffolderClient;
exports.serviceEndpointToScaffold = serviceEndpointToScaffold;
exports.scaffolderClientEndpoint = scaffolderClientEndpoint;