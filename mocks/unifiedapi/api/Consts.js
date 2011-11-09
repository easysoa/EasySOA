// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var ServiceImplType = {
    SCAFFOLDER_CLIENT : "scaffolderclient",
    TEMPLATING_UI : "ui",
    JAVASCRIPT : "javascript",
    JAVA : "java",
    EXTERNAL : "external"
};

EASYSOA_HOST = "http://localhost";

var ServerURL = {
    LIGHT : EASYSOA_HOST + ":9011/",
    JAVA : EASYSOA_HOST + ":9012/",
    SCAFFOLDER : EASYSOA_HOST + ":8090/",
    TUNNELING : EASYSOA_HOST + ":8091/tunnelingDev/"
};


module.exports = {
  ServiceImplType   : ServiceImplType,
  ServerURL         : ServerURL
};