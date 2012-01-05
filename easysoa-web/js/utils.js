// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var base64 = require('./lib/base64').base64;

/**
 * Misc functions
 *
 * Author: Marwane Kalam-Alami
 */

exports.encodeAuthorization = function(username, password) {
    if (username != null && password != null) {
        return "Basic " + base64.encode(username + ':' + password);
    }
    else {
        return null;
    }
};

exports.strToRegexp = function(strings) {
	if (!(strings instanceof Array)) {
		console.log(typeof strings),
		strings = [ strings ];
	}
	var result = [];
	for (var key in strings) {
		result.push(new RegExp(strings[key]));
	}
	return result;
};