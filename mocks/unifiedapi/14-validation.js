// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #14
 * Description: Set up constraints to validate services in various contexts
 * Context : -
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

console.log("-------------------------------------");
console.log("[Scenario #14]");

// Case 1: Validate all services implementing a contract, using a given WSDL 




// Case 2: Make sure all services used in a Light environment are either sandboxed or from a Light environment





// Case 3: On environment startup, make sure to notify the env. manager when an unexpected service is found
// and let him resolve the problem by either:
//   * Approving and adding to the business architecture
//   * Ignoring (consider the service "External")
//   * Blocking env. startup until removed from references or added to archi by someone



console.log("Done.");