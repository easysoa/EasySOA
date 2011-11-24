// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #13
 * Description: Various approaches to find a service we want to use, using NXQL queries
 * Context : -
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

console.log("-------------------------------------");
console.log("[Scenario #13]");

var query = new api.Query();

// Find a service that fits exactly the same contract

var serviceToMatchContract = new api.ServiceContract("MyService");
var serviceToMatch = new api.JavaImpl("MyService", options = { contract: serviceToMatchContract.name });
query.run("SELECT * FROM ServiceImpl WHERE serv:contractId = " + serviceToMatch.contract + " AND serv:isProductionReady = true");

// Find a service that matches another, but in a certain environment

var environment = "Dev_Sophie";
query.run("SELECT * FROM ServiceImpl WHERE serv:contractId = " + serviceToMatch.contract + " AND serv:environment = '" + environment + "'");

console.log("Done.");