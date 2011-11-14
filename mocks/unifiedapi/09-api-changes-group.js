// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #9
 * Description: Detect API changes on a group of services
 * Context : Integration
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

console.log("-------------------------------------");
console.log("[Scenario #9]");

var env = new api.ProductionEnvironment("Production");

// Create service impls. & appli impl.

var clientImpl = new api.JavaImpl("myClient", options={isProductionReady: true});
var serviceAImpl = new api.ExternalImpl("Service A", options={isProductionReady: true});
var serviceBImpl = new api.ExternalImpl("Service B", options={isProductionReady: true});

var externalAppliImpl = new api.AppliImpl("External App");
externalAppliImpl.addServiceImpl(serviceAImpl);
externalAppliImpl.addServiceImpl(serviceBImpl);

// Add references

clientImpl.addReferences(externalAppliImpl.getServiceImpls());

// Add services to environment

var serviceAEndpoint = new api.ExternalEndpoint(serviceAImpl, "http://www.othercompany.org/serviceA");
var serviceBEndpoint = new api.ExternalEndpoint(serviceBImpl, "http://www.othercompany.org/serviceB");

var clientEndpoint = env.addServiceImpl(clientImpl);
env.addExternalServiceEndpoint(serviceAEndpoint);
env.addExternalServiceEndpoint(serviceBEndpoint);
env.resolveReferences();  // For each managed service, resolve references by:
                          // - Choosing the endpoint 
                          // - Creating a tunneling node
                          // Thus it validates the environment (or not if a reference could not be satisfied)

// Add detection change feature

var changeDetection = new api.ChangeDetectionFeature(
    { "req1" : "resp1",
      "req2" : "resp2",
      "req2" : "resp3" },
    function(changedEndpoint, message) {
       console.log("A service has changed!"); 
    });

console.log(clientEndpoint.getReferences()[1]);

var tunnelingNodes = env.getTunnelingNodesByClientEndpoint(clientEndpoint); // A way among others to retrieve a group of tunneling nodes
tunnelingNodes.each(function (tunnelingNode) {
    tunnelingNode.useProxyFeature(changeDetection);
});

env.start();

console.log("Done.");