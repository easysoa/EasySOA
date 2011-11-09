// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #7
 * Description: Transition from developer environment to staging one
 * Context : Integration
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

// Set up environments

var lightEnv = new api.DevelopmentEnvironment("light", "Sophie");
var productionEnv = new api.ProductionEnvironment("production", "http://www.myservices.com");

// Implement java service

var javaImpl = new api.JavaImpl("myHeavyService");
javaImpl.edit();
lightEnv.addServiceImpl(javaImpl);
lightEnv.start();

// Send to production environment

productionEnv.addServiceImpl(javaImpl); // Should fail: the service is not production-ready

javaImpl.isProductionReady = true;
productionEnv.addServiceImpl(javaImpl);

productionEnv.start();

console.log("Done.");