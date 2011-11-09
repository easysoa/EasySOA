// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #8
 * Description: Detect API changes
 * Context : Integration
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

// Prepare environment

var testEnv = new api.DevelopmentEnvironment("PureAirFlowers", "Sophie"); // on default business architecture
var extImpl = new api.ExternalImpl("uberService");
var extEndpoint = new api.ExternalEndpoint(extImpl, "http://www.othercompany.org/uberService");
testEnv.addExternalServiceEndpoint(extEndpoint);

// Create own service

var ourImpl = new api.JavaImpl("ourService");
var ourEndpoint = testEnv.addServiceImpl(ourImpl);

testEnv.start();

console.log("Done.");