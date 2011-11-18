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
    initialize : function(fromImpl, toImpl) {
        this.fromImpl = fromImpl;
        this.toImpl = toImpl;
        
        this.url = this.buildUrl(fromImpl, toImpl);
        this.proxyFeatures = new $H();
    },
    useProxyFeature : function(proxyFeature) {
        this.proxyFeatures.set(proxyFeature.getClass(), proxyFeature);
    },
    getProxyFeature: function(clazz) {
        return this.proxyFeatures.get(clazz);
    },
    buildUrl: function(fromImpl, toImpl) {
        return consts.ServerURL.TUNNELING + fromImpl.name.sub(' ', '_')
                + "/to/" + toImpl.name.sub(' ', '_');
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

var FuseFeature = Class.create(AbstractProxyFeature, {
    initialize : function($super, limitPerMinute) {
        $super(ChangeDetectionFeature);
        this.limitPerMinute = limitPerMinute;
    }
});


module.exports = {
  TunnelingNode          : TunnelingNode,
  MonitoringProxyFeature : MonitoringProxyFeature,
  ChangeDetectionFeature : ChangeDetectionFeature,
  FuseFeature            : FuseFeature
};
