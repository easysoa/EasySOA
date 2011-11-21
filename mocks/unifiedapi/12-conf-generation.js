// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Unified API Scenario #12
 * Description: Generation of environment-specific configuration by various means
 * Context : Integration
 * Author: Marwane Kalam-Alami
 */

var api = require('./api');

console.log("-------------------------------------");
console.log("[Scenario #12]");

// Set up environment

var serviceImpl = new api.JavaImpl("MyService");
serviceImpl.edit();
serviceImpl.isProductionReady = true;

var devEnv = new api.DevelopmentEnvironment("MyEnv", "Sophie");
devEnv.addServiceImpl(serviceImpl);
devEnv.start();

var prodEnv = new api.ProductionEnvironment("Production");
prodEnv.addServiceImpl(serviceImpl);
prodEnv.start();

// Property File

var endpointConfGen = new api.EndpointListConfGenerator();
endpointConfGen.generate(devEnv, "properties"); // Generate a property file to be used in any runtime
endpointConfGen.generate(prodEnv, "properties");

// Template File

var templateConfGen = new api.TemplateConfGenerator(new api.TemplatingUIImpl("SpringConfTpl"));
templateConfGen.edit();
templateConfGen.generate(prodEnv);

// SCA configuration File

var scaConfGen = new api.SCACompositeGenerator();
scaConfGen.generate(devEnv);

//SCA configuration File

var soapUiGen = new api.SoapUIProjectGenerator();
soapUiGen.generate(devEnv);

console.log("Done.");