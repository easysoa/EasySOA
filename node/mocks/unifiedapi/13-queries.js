// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #13
 * Description: Various approaches to find a service we want to use to replace another one, thanks to NXQL queries
 * Context : -
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

console.log("-------------------------------------");
console.log("[Scenario #13]");

// Set up context

var serviceToMatchContract = new api.ServiceContract("MyService");
var serviceToMatch = new api.JavaImpl("MyService", options = { contract: serviceToMatchContract.name });

var devEnv = new api.DevelopmentEnvironment("Dev", "Sophie");
devEnv.addServiceImpl(serviceToMatch);
devEnv.start();

var query = new api.Query();
var newService, newService2;

// Replace with a finished service that fits the same contract

newService = query.run("SELECT * FROM ServiceImpl WHERE serv:contractId = " + serviceToMatch.contract + " AND serv:isProductionReady = 1");
devEnv.replaceServiceImpl(serviceToMatch, newService);

// Replace with a service that matches another, but in a certain environment

newService2 = query.run("SELECT * FROM ServiceImpl WHERE serv:contractId = " + serviceToMatch.contract + " AND serv:environment = '" + devEnv.name + "'");
devEnv.replaceServiceImpl(newService, newService2);

// Replace with a service that holds the same references to other services


// Replace with a service that holds some or all of the same references to other services


// Replace with a service that only holds references to services from a certain environment


// Replace with a service only holds references to the selected services




console.log("Done.");