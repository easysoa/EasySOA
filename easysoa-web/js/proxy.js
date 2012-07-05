// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var httpProxy = require('http-proxy');
var url = require('url');

var base64 = require('./lib/base64').base64;
var utils = require('./utils');

/**
 * Basic HTTP proxy component, using 'node-http-proxy'.
 *
 * Author: Marwane Kalam-Alami
 */

// Proxy Initialization

var proxy = new (httpProxy.RoutingProxy)();

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
	if (request.session && request.session.username) {
		request.headers['Authorization'] = utils.encodeAuthorization(request.session.username, request.session.password);
	}
	var proxyOptions = {
	      host: host || parsedUrl.hostname,
	      port: port || parsedUrl.port
	  };
	if (proxyOptions.host && proxyOptions.port) {
	  try {
	    proxy.proxyRequest(request, response, proxyOptions);
	  }
	  catch (error) {
	    log.error('Proxy failed to forward request (' + proxyOptions + ') : ' + error);
	    response.end();
	  }
	}
	else {
	  response.end();
	}
};

exports.handleProxyRequest = function(request, response) {
	var parsedUrl = url.parse(request.url);
	forwardTo(request, response, parsedUrl.hostname, parsedUrl.port, parsedUrl.path);
};
