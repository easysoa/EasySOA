// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var httpProxy = require('http-proxy');
var url = require('url');

/**
 * Basic HTTP proxy component, using 'node-http-proxy'.
 *
 * Author: Marwane Kalam-Alami
 */

// Proxy Initialization

var proxy = new httpProxy.HttpProxy();

proxy.on('proxyError', function(error, request, result) {
    result.end("<h1>Error "+error.errno+"</h1>" +
    		   "<p>"+error.message+"</p>");
});

// Proxy handler

exports.forwardTo = forwardTo = function(request, response, host, port, path) {
	var parsedUrl = null;
	if (host == null || port == null) {
		parsedUrl = url.parse(request.url);
	}
	if (path != null) {
		request.url = path;
	}
	proxy.proxyRequest(request, response, {
	    'host': host || parsedUrl.hostname,
	    'port': port || parsedUrl.port
	});
};

exports.handleProxyRequest = function(request, response) {
	var parsedUrl = url.parse(request.url);
	forwardTo(request, response, parsedUrl.hostname, parsedUrl.port, parsedUrl.path);
};
