// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #11
 * Description: Creation of a service with Talend ESB
 *              (inspired by 3rd video in: http://www.easysoa.org/2011/10/easysoa-demo-0-3-in-videos/)
 * Context : Integration
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

console.log("-------------------------------------");
console.log("[Scenario #11]");

//// Step 1: Create and discover Talend service

// Set up scenario context

var devEnv = new api.DevelopmentEnvironment("MyDevEnv", "George");
devEnv.addServiceImpl(new api.JavaImpl("ExistingService"));
devEnv.start();

// New service creation project

var project = new api.Project("Service creation");
var service = new api.ServiceContract("My new service");
project.addService(service);
service.edit(); // Work on service contract
project.setReadyForImplementation(true);

// Service creation from Talend Studio

var currentEnv = devEnv; // Tell in which env we are
var talendImpl = new api.TalendImpl("MyTalendService", "http://localhost:8200/esb/MyTalendService"); // Create service
currentEnv.addServiceImpl(talendImpl);
var existingServiceImpl = api.selectServiceImplFromEnvInUI(currentEnv); // Find and use another service thanks to EasySOA
talendImpl.addReference(existingServiceImpl);

//// Step 2: Import in Light environment to be used as a reference

// Create new Light service to work on

var lightEnvSophie = new api.DevelopmentEnvironment("LightEnv", "Sophie");
var scriptedServiceImpl = new api.JavascriptImpl("ScriptedService");
lightEnvSophie.addServiceImpl(scriptedServiceImpl);

// From Light UI, tell to use the Talend service 

var reference = scriptedServiceImpl.addReference(talendImpl);
lightEnvSophie.addServiceImpl(talendImpl);
lightEnvSophie.resolveReferences();
var tunnel = lightEnvSophie.getTunnelingNodeByReference(reference);
tunnel.useProxyFeature(new api.FuseFeature(10)); // Default conf. when adding reference from external service in Light
tunnel.useProxyFeature(new api.MonitoringProxyFeature()); // Idem

/* TODO Rather:
var reference = scriptedServiceImpl.addReference(talendImpl);
var tunnel = reference.createTunnelingNode();
tunnel.useProxyFeature(new api.FuseFeature(10)); 
tunnel.useProxyFeature(new api.MonitoringProxyFeature());
lightEnvSophie.addServiceImpl(talendImpl);
*/

// Edit then start service

scriptedServiceImpl.edit();
lightEnvSophie.start(); // Should fail: missing Talend dep!

// Add missing service and restart*

lightEnvSophie.addServiceImpl(existingServiceImpl); // TODO: lightEnvSophie.fillDependencies(); ?
if (lightEnvSophie.start()) {
    console.log("Done.");
} else {
    console.error("Fail.");
}