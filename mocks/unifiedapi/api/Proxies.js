// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));
var consts = require('./Consts');


//===================== Tunneling node =====================

var TunnelingNode = Class.create({
    initialize : function(fromServiceImpl, toServiceImpl) {
        this.fromServiceImpl = fromServiceImpl;
        this.toServiceImpl = toServiceImpl;
        
        this.url = consts.ServerURL.TUNNELING + fromServiceImpl.name + "/to/" + toServiceImpl.name;
        this.proxyFeatures = new $H();
    },
    useProxyFeature : function(proxyFeature) {
        this.proxyFeatures.set(proxyFeature.getClass(), proxyFeature);
    },
    getProxyFeature: function(clazz) {
        return this.proxyFeatures.get(clazz);
    }
});


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

var ChangeDetectionFeature = Class.create(AbstractProxyFeature, {
    initialize : function($super, trustedRecords, onChangeCallback) {
        $super(ChangeDetectionFeature);
        this.trustedRecords = trustedRecords;
        this.onChangeCallback = onChangeCallback;
    }
});

module.exports = {
  TunnelingNode          : TunnelingNode,
  MonitoringProxyFeature :  MonitoringProxyFeature,
  ChangeDetectionFeature :  ChangeDetectionFeature
};
