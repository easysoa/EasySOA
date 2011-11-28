// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));
var consts = require('./Consts');
var proxies = require('./Proxies');

/**
 * Available service impl. options:
 * options = {
 *      isMock            : true/false (default = false),
 *      isProductionReady : true/false (default = false),
 *      serviceDef        : ServiceDefinition (default = undefined)
 * }
 */

//===================== Service Impl. =====================

var AbstractServiceImpl = Class.create({
    initialize : function(name, type, options /*=undefined*/) {
        this.name = name;
        this.type = type;

        this.isMock = false;
        this.isProductionReady = false;
        this.references = new Array();
        this.serviceDef = undefined;
        this.eventListeners = new $H();
        if (options != undefined) {
            this.isMock = (options.isMock == undefined) ? this.isMock : options.isMock;
            this.isProductionReady = (options.isProductionReady == undefined) ? 
                    this.isMock : options.isProductionReady;
            this.contract = (options.contract == undefined) ? 
                    this.contract : options.contract;
            
        }
    },
    getName : function() {
        return this.name;
    },
    edit : function() {
        if (this.type != consts.ServiceImplType.EXTERNAL) {
            var showReferences = this.references.size() > 0;
            console.log("Making user edit service impl. " + this.name
                    + ((showReferences) ? " with available dependencies:" : ""));
            if (showReferences) {
                this.references.each(function(reference) {
                   console.log(" * " + reference.tunnelingUrl + " = " + reference.toImpl.name); 
                });
            }
        }
    },
    addReference : function(toServiceImpl) {
        var newReference = new ServiceReference(this, toServiceImpl, "implementation");
        this.references.push(newReference);
        return newReference;
    },
    addReferences : function(toServiceImplHashTable) {
        var impl = this;
        var newReferences = new Array();
        toServiceImplHashTable.each(function (entry) {
            newReferences.push(impl.addReference(entry[1]));
        });
        return newReferences;
    },
    addValidationRule : function(validationRule, eventType) {
        this.eventListeners.set(eventType, validationRule); // (Not used anywhere in the mock atm)
    }
});

var ScaffolderClientImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, targetEndpoint, options /*=undefined*/) {
        $super(name, consts.ServiceImplType.SCAFFOLDER_CLIENT, options);
        this.targetEndpoint = targetEndpoint;
    }
});

var JavascriptImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, options /*=undefined*/, serviceImplToMock /*=undefined*/) {
        $super(name, consts.ServiceImplType.JAVASCRIPT, options);
        if (serviceImplToMock != undefined) {
            console.log("Building mock using "+serviceImplToMock.name);
        }
    },
    feedMockWithRecords : function(records) {
        console.log("Making mock use some request/response records");
    }
});

var JavaImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, options /*=undefined*/) {
        $super(name, consts.ServiceImplType.JAVA, options);
    }
});

var TemplatingUIImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, options /*=undefined*/, fromScaffolderClient /*=undefined*/) {
        $super(name, consts.ServiceImplType.TEMPLATING_UI, options);
        if (fromScaffolderClient != undefined) {
            console.log("Building template UI using "+fromScaffolderClient.name);
        }
    }
});

var TalendImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, endpointUrl, options /*=undefined*/) {
        $super(name, consts.ServiceImplType.TALEND, options);
        this.endpointUrl = endpointUrl;
    }
});

var ExternalImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, options /*=undefined*/) {
        $super(name, consts.ServiceImplType.EXTERNAL, options);
    }
});

//===================== Appli Impl. =====================

var AppliImpl = Class.create({
   initialize: function(name) {
       this.name = name;
       this.serviceImpls = new $H();
   },
   addServiceImpl: function(serviceImpl) {
       this.serviceImpls.set(serviceImpl.name, serviceImpl);
   },
   getServiceImpls: function() {
       return this.serviceImpls;
   },
   getServiceImpl: function(name) {
       return this.serviceImpls.get(name);
   }
});



//===================== Service Reference =====================

var ServiceReference = Class.create({
   initialize: function(fromImpl, toImpl, creationTime) {
       this.fromImpl = fromImpl;
       this.toImpl = toImpl;
       this.creationTime = creationTime;
       this.tunnelingUrl = proxies.TunnelingNode.prototype.buildUrl(fromImpl, toImpl);
   }
});


module.exports = {
  ScaffolderClientImpl  :   ScaffolderClientImpl,
  JavascriptImpl        :   JavascriptImpl,
  JavaImpl              :   JavaImpl,
  TemplatingUIImpl      :   TemplatingUIImpl,
  TalendImpl            :   TalendImpl,
  ExternalImpl          :   ExternalImpl,
  AppliImpl             :   AppliImpl
};
