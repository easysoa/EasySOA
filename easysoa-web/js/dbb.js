// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var httpProxy = require('http-proxy');
var url = require('url');
var socketio = require('socket.io');

var settings = require('./settings');
var proxy = require('./proxy');
var nuxeo = require('./nuxeo');

/**
 * Discovery by Browsing component, responsible for communicating with the DBB client.
 *
 * Author: Marwane Kalam-Alami
 */

EASYSOA_PARSED_ROOT_URL = url.parse(EASYSOA_ROOT_URL);

// =============== Model ================

var clientWellConfigured = false; // XXX: Is common to everybody (should be part of a session)


// ================ I/O =================

exports.configure = function(webServer) {
	// Router configuration
	webServer.get('/dbb/servicefinder', forwardToNuxeo);
	webServer.get('/dbb/discovery/environments', forwardToNuxeo);
	webServer.get('/dbb/clear', clearWSDLs);
	webServer.get('/dbb/send', sendWSDL);

	// SocketIO init
	io = socketio.listen(webServer);
	io.set('log level', 2);
	io.set('transports', ['flashsocket', 'htmlfile', 'xhr-polling', 'jsonp-polling']); // 'websocket' doesn't work through the proxy
	io.sockets.on('connection', initSocketIOConnection);
};

exports.handleProxyRequest = function(request, response) {
	confirmClientConfiguration();
};

forwardToNuxeo = function(request, response, next) {
	var parsedUrl = url.parse(request.url);
	proxy.forwardTo(request, response,
			EASYSOA_PARSED_ROOT_URL.hostname,
			EASYSOA_PARSED_ROOT_URL.port,
			EASYSOA_PARSED_ROOT_URL.path + parsedUrl.path.replace('/dbb', ''));
};


// ============= Controller =============

initSocketIOConnection = function(socket) {
    // Notify that Nuxeo is ready
    if (nuxeo.isReady()) {
      socket.emit('ready');
    }
    if (clientWellConfigured) {
      socket.emit('proxyack');
    }
	// TODO
   /*// Send stored WSDLs
    for (key in wsdlList) {
      socket.emit('wsdl', wsdlList[key]);
    }*/
};

confirmClientConfiguration = function() {
	clientWellConfigured = true;
};

sendWSDL = function(request, response, next) {
	// TODO
};

clearWSDLs = function(request, response, next) {
	// TODO
};
