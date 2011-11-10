// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #10
 * Description: New design & development project
 * Context : Integration
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

console.log("-------------------------------------");
console.log("[Scenario #10]");

// New service creation project

var project = new api.Project("Service creation");
var service = new api.ServiceDefinition("My new service");
project.addService(service);
service.edit(); // Work on service contract
project.setReadyForImplementation(true);

// Prototype service implementation

var prototypeImpl = new api.JavascriptImpl("My new service proto", options={serviceDef: service});
prototypeImpl.edit();

var testEnv = new api.DevelopmentEnvironment("Proto test", "George");
testEnv.addServiceImpl(prototypeImpl);
testEnv.start();

// Real service implementation

var serviceImpl = new api.JavaImpl("My new service", options={serviceDef: service});
serviceImpl.edit();

var stagingEnv = new api.StagingEnvironment("Staging");
stagingEnv.addServiceImpl(serviceImpl);
stagingEnv.start();

// Send the service to production

serviceImpl.isProductionReady = true;
var productionEnv = stagingEnv.cloneAs(api.ProductionEnvironment, "Production");
productionEnv.start();

console.log("Done.");