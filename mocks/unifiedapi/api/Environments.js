// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));
var consts = require('./Consts');
var endpoints = require('./Endpoints');
var proxies = require('./Proxies');

var EnvironmentType = {
  DEVELOPMENT   : "development",
  STAGING       : "staging",
  PRODUCTION    : "production"
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
        this.isStarted = false;
    },
    install : function(serviceImpl, env) {
        // To implement
    },
    contains : function(serviceImpl) {
        var result = false;
        this.serviceEndpoints.each(function (endpoint) {
            if (endpoint.impl == serviceImpl) {
                result = true;
            }
        });
        return result;
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
        if (!this.isStarted) {
            var allIsStarted = true;
            this.serviceEndpoints.each(function(endpoint) {
                if (!endpoint.start()) {
                    allIsStarted = false;
                }
            });
            if (allIsStarted) {
                this.isStarted = true;
            }
            else {
                this.stop();
            }
            return allIsStarted;
        }
        else {
            return true;
        }
    },
    stop : function() {
        this.serviceEndpoints.each(function(endpoint) {
            endpoint.stop();
            this.isStarted = false;
        });
    }
});

var NodeServer = Class.create(AbstractServer, {
    initialize : function($super) {
        $super(consts.ServerURL.LIGHT, [
                consts.ServiceImplType.TEMPLATING_UI,
                consts.ServiceImplType.JAVASCRIPT
            ]);
    },
    install : function(serviceImpl, env) {
        var newEndpoint = new endpoints.JavaServiceEndpoint(serviceImpl, this);
        this.serviceEndpoints.push(newEndpoint);
        
        if (newEndpoint != null) {
            this.serviceEndpoints.push(newEndpoint);
        }
        return newEndpoint;
    }
});

var FraSCAtiServer = Class.create(AbstractServer, {
    initialize : function($super) {
        $super(consts.ServerURL.JAVA, [ 
                 consts.ServiceImplType.SCAFFOLDER_CLIENT,
                 consts.ServiceImplType.JAVA
             ]);
    },
    install : function(serviceImpl, env) {
        var newEndpoint = null;
        switch (serviceImpl.type) {
        case consts.ServiceImplType.SCAFFOLDER_CLIENT:
            newEndpoint = new endpoints.ScaffolderClientEndpoint(serviceImpl, this); break;
        case consts.ServiceImplType.JAVA:
            newEndpoint = new endpoints.JavaServiceEndpoint(serviceImpl, this); break;
        }
        if (newEndpoint != null) {
            this.serviceEndpoints.push(newEndpoint);
        }
        return newEndpoint;
    }
});

// ===================== Environments =====================

var AbstractEnvironment = Class.create({
    initialize : function(envType, name, serverArray) {
        this.name = name;
        this.externalServiceEndpoints = new Array();
        this.endpoints = new Array();
        this.servers = new $H();
        this.tunnelingNodes = new Array(); // TODO put in a tunneling server?
        for (var i = 0; i < serverArray.length; i++) {
            var server = serverArray[i];
            for (var j = 0; j < server.supportedImplTypes.length; j++) {
                this.servers.set(server.supportedImplTypes[j], server);
            }
        }
    },
    addExternalServiceEndpoint : function(serviceEndpoint) {
        this.externalServiceEndpoints.push(serviceEndpoint);
    },
    addServiceImpl : function(serviceImpl) {
        var newEndpoint;
        if (serviceImpl.type != consts.ServiceImplType.TALEND) {
            var server = this.servers.get(serviceImpl.type);
            newEndpoint = server.install(serviceImpl, this);
        }
        else {
            // Talend: no auto deployment possible
            newEndpoint = new endpoints.ExternalEndpoint(serviceImpl, serviceImpl.endpointUrl);
        }
        this.endpoints.push(newEndpoint);
        return newEndpoint;
    },
    replaceServiceImpl : function(serviceImpl, newServiceImpl) {
        var replaced = false;
        this.servers.each(function (entry) {
            if (entry[1].contains(serviceImpl)) {
                entry[1].remove(serviceImpl);
                entry[1].install(newServiceImpl);
                replaced = true;
                console.log("Replacing " + serviceImpl.name + " with " + newServiceImpl.name);
            }
        });
        if (!replaced) {
            console.error("Couldn't find impl. "+serviceImpl.name);
        }
    },
    removeServiceImpl : function(serviceImpl) {
        this.servers.each(function (entry) {
            entry[1].remove(serviceImpl); 
        });
    },
    resolveReferences : function() {
        var tunnelingNodes = new Array();
        var allEndpoints = this.endpoints.concat(this.externalServiceEndpoints);
        var allReferencesResolved = true;
        allEndpoints.each(function (endpoint) {
            var references = endpoint.getReferences();
            // Resolve each reference
            references.each(function (reference) {
                var matchingEndpoint = null;
                allEndpoints.each(function (potentialEndpoint) {
                    if (reference.toImpl == potentialEndpoint.impl) {
                        matchingEndpoint = potentialEndpoint;
                    }
                });
                // Create tunneling node for each reference
                if (matchingEndpoint != null) {
                    tunnelingNodes.push(new proxies.TunnelingNode(reference.fromImpl, matchingEndpoint.impl));
                }
                else {
                    console.info("Warning: could not resolve reference from " + 
                            reference.fromImpl.name + " to " + reference.toImpl.name);
                    allReferencesResolved = false;
                }
            });
        });
        this.tunnelingNodes = tunnelingNodes;
        return allReferencesResolved;
    },
    getTunnelingNodeByReference : function(reference) {
        var result = null;
        this.tunnelingNodes.each(function (tunnelingNode) {
            result = tunnelingNode;
         });
        return result;
    },
    getTunnelingNodesByClientEndpoint : function(endpoint) {
        var result = new Array();
        this.tunnelingNodes.each(function (tunnelingNode) {
           if (tunnelingNode.fromImpl == endpoint.impl) {
               result.push(tunnelingNode);
           }
        });
        return result;
    },
    start : function() {
        console.log("Starting environment " + this.name + "...");
        if (this.resolveReferences()) {
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
        }
        else {
            return false;
        }
    },
    stop : function() {
        this.servers.each(function(entry) {
            entry[1].stop();
        });
    },
    cloneAs : function(clazz, name /*=undefined*/, options /*=undefined*/) {
      if (name == undefined) {
          name = this.name;
      }
      if (options == undefined || options.user == undefined) {
          user = this.user;
      }
      
      var msg = "Cloning environment " + this.name + " to " + name;
      if (options != undefined && options.noProxies != undefined) {
          msg += " (without the proxies & tunnels)";
      }
      console.log(msg);
      
      var newEnv = new clazz(name, user); // TODO Clone environment contents
      return newEnv;
    }
});

var StagingEnvironment = Class.create(AbstractEnvironment, {
    initialize : function($super, name) {
        $super(EnvironmentType.STAGING, name, [ new FraSCAtiServer() ]);
    }
});

var ProductionEnvironment = Class.create(AbstractEnvironment, {
    initialize : function($super, name) {
        $super(EnvironmentType.PRODUCTION, name, [ new FraSCAtiServer() ]);
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
                [ new NodeServer() , new FraSCAtiServer() ]);
        this.user = user;
    }
});


module.exports = {
  StagingEnvironment      :  StagingEnvironment,
  ProductionEnvironment   :  ProductionEnvironment,
  DevelopmentEnvironment  :  DevelopmentEnvironment
};
