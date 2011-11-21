// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));

var AbstractConfGenerator = new Class.create({
    initialize : function() {
        // Do nothing
    },
    generate : function(env) {
        // To implement
    }
});


var EndpointListConfGenerator = new Class.create(AbstractConfGenerator, {
    initialize : function() {
        // Do nothing
    },
    generate : function(env, format) {
        console.log("Generating endpoint list as " + format + " from environment " + env.name);
    }
});


var TemplateConfGenerator = new Class.create(AbstractConfGenerator, {
    initialize : function() {
        // Do nothing
    },
    edit : function() {
        console.log("Editing configuration generator template");
    },
    generate : function(env) {
        console.log("Generating configuration from template, from environment " + env.name);
    }
});



module.exports = {
    TemplateConfGenerator       : TemplateConfGenerator,
    EndpointListConfGenerator   : EndpointListConfGenerator
};