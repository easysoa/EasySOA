// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));

var Query = Class.create({
    initialize : function() {
        
    },
    run : function(query) {
        console.log(query);
    }
});


module.exports = {
  Query   : Query
};
