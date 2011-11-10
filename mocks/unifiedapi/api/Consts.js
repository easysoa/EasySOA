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
    NODE : EASYSOA_HOST + ":9011/",
    FRASCATI : EASYSOA_HOST + ":9012/",
    TUNNELING : EASYSOA_HOST + ":9013/tunnelingDev/"
};


module.exports = {
  ServiceImplType   : ServiceImplType,
  ServerURL         : ServerURL
};