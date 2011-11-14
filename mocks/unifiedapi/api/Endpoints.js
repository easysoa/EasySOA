// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));
var consts = require('./Consts');
var proxies = require('./Proxies');


var ServiceEndpoint = Class.create({
    initialize : function(impl, url, server, autoUpdate /*=false*/) {
        this.impl = impl;
        this.url = url;
        this.server = server;
        this.autoUpdate = (autoUpdate == undefined) ? false : autoUpdate;
        this.started = false;
        this.proxyFeatures = new $H();
        this.onUpdateListeners = new Array();
    },
    getName : function() {
        return this.impl.name;
    },
    getImpl : function() {
        return this.impl;
    },
    checkStarted : function() {
        console.log(" * Checking: " + this.url);
        return this.started;
    },
    start : function() {
        console.log(" * Starting: " + this.impl.name);
        this.started = true;
        return this.started;
    },
    stop : function() {
        console.log(" * Stopping: " + this.impl.name);
        this.started = false;
    },
    useProxyFeature : function(proxyFeature) {
        this.proxyFeatures.set(proxyFeature.getClass(), proxyFeature);
    },
    getProxyFeature: function(clazz) {
        return this.proxyFeatures.get(clazz);
    },
    registerOnUpdateListener: function(runnable) {
        this.onUpdateListeners.push(runnable);
    },
    getReferences: function(availableEndpoints) {
        return this.impl.references;
    },
    addReference : function(toServiceImpl) {
        var newReference = new ServiceReference(this, toServiceImpl, "endpoint"); // TODO uncouple with impl refs
        this.references.push(newReference);
        return newReference;
    }
});

var ScaffolderClientEndpoint = Class.create(ServiceEndpoint, {
    initialize : function($super, impl, server) {
        $super(impl, null, server);
        this.setTargetEndpoint(impl.targetEndpoint);
    },
    setTargetEndpoint : function(targetEndpoint) {
        this.impl.targetEndpointUrl = targetEndpoint.url;
        this.url = this.server.url
                + toUrlPath(targetEndpoint.getName())
                + "_Scaffolder_Client?endpoint=" + targetEndpoint.url;
    },
    display : function() {
        console.log("Displaying UI: "+this.url);
    }
});


var JavaServiceEndpoint = Class.create(ServiceEndpoint, {
    initialize : function($super, impl, server) {
        $super(impl, server.url + impl.name, server);
    }
});


var ExternalEndpoint = Class.create(ServiceEndpoint, {
    initialize : function($super, impl, url, server /*=undefined*/) {
        $super(impl, url, server);
    }
});


module.exports = {
  ServiceEndpoint           :  ServiceEndpoint,
  ScaffolderClientEndpoint  :  ScaffolderClientEndpoint,
  JavaServiceEndpoint       :  JavaServiceEndpoint,
  ExternalEndpoint          :  ExternalEndpoint
};
