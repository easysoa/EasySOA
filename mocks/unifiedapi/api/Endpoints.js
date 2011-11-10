// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));
var consts = require('./Consts');


var ServiceEndpoint = Class.create({
    initialize : function(impl, url, env, autoUpdate /*=false*/) {
        this.impl = impl;
        this.url = url;
        this.env = env;
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
    setEnvironment: function(env) {
        this.env = env;
    },
    getOutputTunnelingNodes: function() {
        return new Array(); // TODO 
    }
});

var ScaffolderClientEndpoint = Class.create(ServiceEndpoint, {
    initialize : function($super, impl, env) {
        $super(impl, null, env);
        this.setTargetEndpoint(impl.targetEndpoint);
    },
    setTargetEndpoint : function(targetEndpoint) {
        this.impl.targetEndpointUrl = targetEndpoint.url;
        this.url = this.env.implServerUrl // FIXME
                + toUrlPath(targetEndpoint.getName())
                + "_Scaffolder_Client?endpoint=" + targetEndpoint.url;
    },
    display : function() {
        console.log("Displaying UI: "+this.url);
    }
});


var JavaServiceEndpoint = Class.create(ServiceEndpoint, {
    initialize : function($super, impl, env) {
        $super(impl, consts.ServerURL.JAVA + impl.name, env);
    }
});


var ExternalEndpoint = Class.create(ServiceEndpoint, {
    initialize : function($super, impl, url, env /*=undefined*/) {
        $super(impl, url, env);
    }
});


module.exports = {
  ServiceEndpoint           :  ServiceEndpoint,
  ScaffolderClientEndpoint  :  ScaffolderClientEndpoint,
  JavaServiceEndpoint       :  JavaServiceEndpoint,
  ExternalEndpoint          :  ExternalEndpoint
};
