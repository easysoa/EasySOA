// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #8
 * Description: Detect API changes + Service references
 * Context : Integration
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

console.log("-------------------------------------");
console.log("[Scenario #8]");

// Prepare environment

var testEnv = new api.DevelopmentEnvironment("PureAirFlowers", "Sophie"); // on default business architecture
var extImpl = new api.ExternalImpl("uberService");
var extEndpoint = new api.ExternalEndpoint(extImpl, "http://www.othercompany.org/uberService");
testEnv.addExternalServiceEndpoint(extEndpoint);

// Create own service with reference to an existing one

var ourImpl = new api.JavaImpl("ourService");
var extImplReference = ourImpl.addReference(extImpl);
ourImpl.edit();
var ourEndpoint = testEnv.addServiceImpl(ourImpl);

// Record a few exchanges

var monitoring = new api.MonitoringProxyFeature();
ourEndpoint.useProxyFeature(monitoring);
monitoring.save("coveringExchanges"); // Assuming exchanges have been run since previous line
var coveringExchanges = monitoring.getRecords("coveringExchanges");

// Enable service change detection

testEnv.resolveReferences(); // Creates the tunneling nodes
var tunnelingNode = testEnv.getTunnelingNodeByReference(extImplReference);
tunnelingNode.useProxyFeature(new api.ChangeDetectionFeature(
        coveringExchanges,
        function(changedEndpoint, message) {
           console.log("A service has changed!"); 
        }));

testEnv.start();

console.log("Done.");