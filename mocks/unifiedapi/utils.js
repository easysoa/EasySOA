// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

// ===================== Functions =====================

/**
 * Turns any text into an URL-compatible string
 */
exports.toUrlPath = function(name) {
    return name.replace(/ /g, "_");
};

// ============ Object prototype additions ==============

/**
 * Clones an object
 * Customized from: http://stackoverflow.com/questions/728360/copying-an-object-in-javascript
 */
Object.defineProperty(Object.prototype, "clone", {
    enumerable : false,
    value : function() {
        var copy = this.constructor();
        for (var attr in this) {
            if (this.hasOwnProperty(attr))
                copy[attr] = this[attr];
        }
        return copy;
    }
});

/**
 * Allows to create an object by extending an existing one.
 * Customized from: http://onemoredigit.com/post/1527191998/extending-objects-in-node-js
 */
Object.defineProperty(Object.prototype, "extend", {
    enumerable : false,
    value : function(from) {
        var props = Object.getOwnPropertyNames(from);
        var dest = this.clone();
        props.forEach(function(name) {
            var destination = Object.getOwnPropertyDescriptor(from, name);
            Object.defineProperty(dest, name, destination);
        });
        return dest;
    }
});