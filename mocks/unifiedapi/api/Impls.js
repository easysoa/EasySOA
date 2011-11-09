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
 * Available options:
 * options = {
 *      isMock            : true/false (default = false),
 *      isProductionReady : true/false (default = false)
 * }
 */

var AbstractServiceImpl = Class.create({
    initialize : function(name, type, options /*=undefined*/) {
        this.name = name;
        this.type = type;

        this.tunnelingNodes = new $H();
        this.isMock = false;
        this.isProductionReady = false;
        if (options != undefined) {
            this.isMock = (options.isMock == undefined) ? this.isMock : options.isMock;
            this.isProductionReady = (options.isProductionReady == undefined) ? 
                    this.isMock : options.isProductionReady;
        }
    },
    getName : function() {
        return this.name;
    },
    edit : function() {
        if (this.type != consts.ServiceImplType.EXTERNAL) {
            var showReferences = this.tunnelingNodes.size() > 0;
            console.log("Making user edit service impl. " + this.name
                    + ((showReferences) ? " with available dependencies:" : ""));
            if (showReferences) {
                this.tunnelingNodes.each(function(entry) {
                   console.log(" * "+entry[0]+": "+entry[1].url); 
                });
            }
        }
    },
    addReference : function(toServiceImpl) {
        var tunnelingNode = new proxies.TunnelingNode(this, toServiceImpl);
        this.tunnelingNodes.set(toServiceImpl.name, tunnelingNode);
        return tunnelingNode;
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
    initialize : function($super, name, options, fromScaffolderClient /*=undefined*/) {
        $super(name, consts.ServiceImplType.TEMPLATING_UI, options);
        if (fromScaffolderClient != undefined) {
            console.log("Building template UI using "+fromScaffolderClient.name);
        }
    }
});

var ExternalImpl = Class.create(AbstractServiceImpl, {
    initialize : function($super, name, options /*=undefined*/) {
        $super(name, consts.ServiceImplType.EXTERNAL, options);
    }
});


module.exports = {
  ScaffolderClientImpl  :   ScaffolderClientImpl,
  JavascriptImpl        :   JavascriptImpl,
  JavaImpl              :   JavaImpl,
  TemplatingUIImpl      :   TemplatingUIImpl,
  ExternalImpl          :   ExternalImpl
};
