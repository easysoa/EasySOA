// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));


//===================== Proxy features =====================

var AbstractProxyFeature = Class.create({
    initialize : function(clazz) {
        this.clazz = clazz;
    },
    process : function(request, response) {
        // To implement
    },
    start : function() {
        
    },
    stop : function() {
        
    },
    getClass : function() {
      return this.clazz;
    }
});

var MonitoringProxyFeature = Class.create(AbstractProxyFeature, {
    initialize : function($super) {
        $super(MonitoringProxyFeature);
        this.activeRun = null;
    },
    process: function(request, response) {
        console.log("Monitoring triggered");  
    },
    save: function(name) {
        console.log("Saving current run as " + name);
        activeRun = null;
    },
    restore: function(name) {
        console.log("Restoring run " + name);
        activeRun = name;
    },
    reset: function() {
        if (activeRun != null) {
            console.log("Resetting run "+activeRun);
        }
    },
    getRecords: function(name) {
        return new Array({
            "request1" : "response1",
            "request2" : "response2"
        });
    }
});


module.exports = {
  AbstractProxyFeature   :  AbstractProxyFeature,
  MonitoringProxyFeature :  MonitoringProxyFeature
};
