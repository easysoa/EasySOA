// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));
var consts = require('./Consts');


//===================== ServiceDefinition =====================

var ServiceContract = Class.create({
    initialize : function(name) {
        this.name = name;
    },
    edit : function() {
        console.log("Making user edit service " + this.name + "'s contract");
    }
});


//========================== Project ==========================

var Project = Class.create({
    initialize : function(name) {
        this.name = name;
        this.serviceDefs = new Array();
        this.readyForImplementation = false;
    },
    addService : function(serviceDefinition) {
        this.serviceDefs.push(serviceDefinition);
    },
    setReadyForImplementation : function(ready) {
        this.readyForImplementation = ready;
    }
});


module.exports = {
        ServiceContract : ServiceContract,
    Project : Project
};