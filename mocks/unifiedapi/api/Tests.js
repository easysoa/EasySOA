// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));


var Runnable = Class.create({
    run: function() {
        // To implement
    }
});

var TestSuite = Class.create(Runnable, {
    initialize: function(serviceImpl, requestsRecords) {
        this.serviceImpl = serviceImpl;
        this.requestsRecords = requestsRecords;
    },
    run: function() {
       console.log("Running test suite on service " + this.serviceImpl.name);
    } 
});

module.exports = {
  TestSuite : TestSuite
};
