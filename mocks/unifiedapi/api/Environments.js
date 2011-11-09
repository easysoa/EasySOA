// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));
var consts = require('./Consts');
var endpoints = require('./Endpoints');

EASYSOA_HOST = "http://localhost";
EASYSOA_LIGHT_SERVER_URL = EASYSOA_HOST + ":9011/";
EASYSOA_JAVA_SERVER_URL = EASYSOA_HOST + ":9011/";
EASYSOA_SCAFFOLDER_UI_URL = EASYSOA_HOST + ":8090/";

var EnvironmentType = {
  DEVELOPMENT : "development",
  STAGING : "staging",
  PRODUCTION : "production"
};

toUrlPath = function(name) {
    return name.sub(' ', '_');
};


// ===================== Servers =====================

var AbstractServer = Class.create({
    initialize : function(url, supportedImplTypes) {
        this.url = url;
        this.supportedImplTypes = supportedImplTypes;
        this.serviceEndpoints = new Array();
    },
    supports : function(serviceImpl) {
        return (this.supportedImplTypes.indexOf(serviceImpl.type) != -1);
    },
    install : function(serviceImpl, env) {
        // To implement
    },
    remove : function(serviceImpl) {
        var newServiceEndpoints = Array();
        this.serviceEndpoints.each(function (endpoint) {
            if (endpoint.impl != serviceImpl) {
                newServiceEndpoints.push(endpoint);
            }
        });
        this.serviceEndpoints = newServiceEndpoints;
    },
    start : function() {
        var allIsStarted = true;
        this.serviceEndpoints.each(function(endpoint) {
            if (!endpoint.start()) {
                allIsStarted = false;
            }
        });
        return allIsStarted;
    }
});

var LightServer = Class.create(AbstractServer, {
    initialize : function($super) {
        $super(EASYSOA_LIGHT_SERVER_URL, [
                consts.ServiceImplType.SCAFFOLDER_CLIENT,
                consts.ServiceImplType.TEMPLATING_UI,
                consts.ServiceImplType.JAVASCRIPT
            ]);
    },
    install : function(serviceImpl, env) {
        var newEndpoint = null;
        
        switch (serviceImpl.type) {
        case consts.ServiceImplType.SCAFFOLDER_CLIENT:
            newEndpoint = new endpoints.ScaffolderClientEndpoint(serviceImpl, env); break;
        case consts.ServiceImplType.JAVA:
            newEndpoint = new endpoints.JavaServiceEndpoint(serviceImpl, env); break;
        default:
            newEndpoint = new endpoints.ServiceEndpoint(serviceImpl,
                    this.implServerUrl + toUrlPath(serviceImpl.name), env);
        }
        
        if (newEndpoint != null) {
            this.serviceEndpoints.push(newEndpoint);
        }
        return newEndpoint;
    }
});

var JavaServer = Class.create(AbstractServer, {
    initialize : function($super) {
        $super(EASYSOA_JAVA_SERVER_URL, [ consts.ServiceImplType.JAVA ]);
    },
    install : function(serviceImpl, env) {
        var newEndpoint = new endpoints.JavaServiceEndpoint(serviceImpl, env);
        this.serviceEndpoints.push(newEndpoint);
        return newEndpoint;
    }
});

// ===================== Environments =====================

var AbstractEnvironment = Class.create({
    initialize : function(envType, name, serverArray) {
        this.name = name;
        this.externalServiceEndpoints = new Array();
        this.servers = new $H();
        for (var i = 0; i < serverArray.length; i++) {
            var server = serverArray[i];
            for (var j = 0; j < server.supportedImplTypes.length; j++) {
                this.servers.set(server.supportedImplTypes[j], server);
            }
        }
    },
    addExternalServiceEndpoint : function(serviceEndpoint) {
        serviceEndpoint.setEnvironment(this);
        this.externalServiceEndpoints.push(serviceEndpoint);
    },
    addServiceImpl : function(serviceImpl) {
        var newServiceEndpoint = null;
        this.servers.each(function(entry) {
            var server = entry[1];
            if (server.supports(serviceImpl)) {
                newServiceEndpoint = server.install(serviceImpl, this);
            }
        });
        return newServiceEndpoint;
    },
    removeServiceImpl : function(serviceImpl) {
        this.servers.each(function (entry) {
            entry[1].remove(serviceImpl); 
        });
    },
    start : function() {
        console.log("Starting environment " + this.name + "...");
        var allIsStarted = true;
        this.externalServiceEndpoints.each(function(endpoint) {
            if (!endpoint.checkStarted()) {
                allIsStarted = false;
            }
        });
        this.servers.each(function(entry) {
            if (!entry[1].start()) {
                allIsStarted = false;
            }
        });
        return allIsStarted;
    },
    stop : function() {
        this.serviceEndpoints.each(function(endpoint) {
            endpoint.stop();
        });
    }
});

var StagingEnvironment = Class.create(AbstractEnvironment, {
    initialize : function($super, name) {
        $super(EnvironmentType.STAGING, name, [ new JavaServer() ]);
    }
});

var ProductionEnvironment = Class.create(AbstractEnvironment, {
    initialize : function($super, name, url) {
        $super(EnvironmentType.PRODUCTION, name, [ new JavaServer() ]);
    },
    addExternalServiceEndpoint : function($super, serviceEndpoint) {
        if (serviceEndpoint.getImpl().isProductionReady) {
            $super(serviceEndpoint);
        }
         else {
            console.log("Service endpoint " + serviceEndpoint.getImpl().name + " is not production ready!");
        }
    },
    addServiceImpl : function($super, serviceImpl) {
        if (serviceImpl.isProductionReady) {
            return $super(serviceImpl);
        }
        else {
            console.log("Service endpoint "+serviceImpl.name+" is not production ready!");
            return null;
        }
    }
});

var DevelopmentEnvironment = Class.create(AbstractEnvironment, {
    initialize : function($super, name, user) {
        $super(EnvironmentType.DEVELOPMENT, user + "_" + name,
                [ new LightServer() , new JavaServer() ]);
    }
});


module.exports = {
  StagingEnvironment      :  StagingEnvironment,
  ProductionEnvironment   :  ProductionEnvironment,
  DevelopmentEnvironment  :  DevelopmentEnvironment
};
