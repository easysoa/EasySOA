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

//---------
// Case 1: Validate all services implementing a contract, using a given WSDL 
//---------

// Set up context

var contract = new api.ServiceContract("MyService");
var impl1 = new api.JavaImpl("MyJavaService", options = {contract: contract});
impl1.isProductionReady = true;
var impl2 = new api.JavascriptImpl("MyJavascriptService", options = {contract: contract});

var prodEnv = new api.ProductionEnvironment("Production");
var endp1 = prodEnv.addServiceImpl(impl1);
prodEnv.start();

// Validation

var validationRule = new api.ValidationRule(impl1, new api.WSDLTest(endp1.url+"?wsdl"), new api.BlockAction());
impl1.addValidationRule(validationRule, "change"); // Run validation on each service impl. change

//---------
// Case 2: Make sure all services used in a Light environment are either sandboxed or from a Light environment
//---------

// More context

var devEnv = new api.DevelopmentEnvironment("MyEnv", "Sophie");
devEnv.addServiceImpl(impl2);
devEnv.start();

// Validation: code run internally when creating the DevelopmentEnvironment?

var serviceCheckTest = new api.ServiceCheckTest(function testService(env, endpoint) {
    var testSuccess = true;
    endpoint.getResolvedReferences().each(function(reference) {
        if (/*the reference doesn't lead to a sandboxed or Light service*/ false) {
            testSuccess = false;
        }
    });
    return testSuccess;
});
var validationRule = new api.ValidationRule(impl1, serviceCheckTest, new api.BlockAction());
devEnv.addValidationRule(validationRule, "add"); // Run validation on each service addition

//---------
// Case 3: On environment startup, make sure to notify the env. manager when an unexpected service is found
// and let him resolve the problem by either:
//   * Approving and adding to the business architecture
//   * Ignoring (consider the service "External")
//   * Blocking env. startup until removed from references or added to archi by someone
//---------

var notificationTest = new api.NotificationTest(function testService(env, notification) {
    var endpoints = env.getAllEndpoints();
    var serviceFound = false;
    // Test if the notification matches one of our services
    endpoints.each(function(endpoint) {
        if (notification.url == serviceFound.url) { 
            serviceFound = true;
        }
    });
    return serviceFound;
});
var validationRule = new api.ValidationRule(impl1, notificationTest, new api.SendMailAction("montgomery.burns@snpp.com"));
devEnv.addValidationRule(validationRule, "notification"); // Run validation on each notification

// Resolve the problem by adding the service to the business architecture

var notification = api.getUnexpectedNotification();
var newImpl = new api.ExternalImpl("NewService", options={contract: contract});
var newEndpoint = new api.ExternalEndpoint(newImpl, notification.url);
devEnv.addExternalServiceEndpoint(newEndpoint);

// Ignore the service

var newImpl = new api.ExternalImpl("NewService", options={hide: true});
var newEndpoint = new api.ExternalEndpoint(newImpl, notification.url);
devEnv.addExternalServiceEndpoint(newEndpoint);

// Block the environment startup

/* Nothing to do on the model, user has to contact those in charge */

console.log("Done.");