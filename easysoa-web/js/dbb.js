// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var httpProxy = require('http-proxy');
var http = require('http');
var socketio = require('socket.io');
var url = require('url');

var settings = require('./settings');
var proxy = require('./proxy');
var nuxeo = require('./nuxeo');
var utils = require('./utils');

/**
 * Discovery by Browsing component, responsible for communicating with the DBB client.
 *
 * Author: Marwane Kalam-Alami
 */

//============= Initialize ==============

SERVICE_FINDER_IGNORE_REGEXPS = utils.strToRegexp(settings.SERVICE_FINDER_IGNORE);

// =============== Model ================

var clientWellConfigured = false; // XXX: Is common to everybody (should be part of a session)
var io = null;
var foundWSDLs = [];

saveWSDLs = function(data) {
	if (data.foundLinks) {
		try {
			for (name in data.foundLinks) {
				var newWSDL = {
					applicationName : data.applicationName,
					serviceName : name,
					url : data.foundLinks[name]
				};
				console.log('Found:', newWSDL.serviceName, '(' + newWSDL.url + ')');
				foundWSDLs[newWSDL.url] = JSON.stringify(newWSDL);
			    if (io != null) {
			        io.sockets.emit('wsdl', JSON.stringify(newWSDL));
			    }
			}
		}
		catch (error) {
			console.log('ERROR: While saving a WSDL: ', error);
		}
	}
};

// ================ I/O =================

exports.configure = function(webServer) {
	// Router configuration
	webServer.get('/dbb/clear', clearWSDLs);
	webServer.get('/dbb/send', sendWSDL);

	// SocketIO init
	io = socketio.listen(webServer);
	io.set('log level', 2);
	io.sockets.on('connection', initSocketIOConnection);
};

exports.handleProxyRequest = function(request, response) {
	
	// Confirms that the proxy is indeed seeing requests coming
	confirmClientConfiguration();
	
	// Run service finder
	if (!isIgnoredUrl(request.url)) {
		findWSDLs(request.url);
	}
	
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
    // Send stored WSDLs
    for (key in foundWSDLs) {
      socket.emit('wsdl', foundWSDLs[key]);
    }
};

confirmClientConfiguration = function() {
	clientWellConfigured = true;
};

isIgnoredUrl = function(url) {
	for (key in SERVICE_FINDER_IGNORE_REGEXPS) {
		var regexp = SERVICE_FINDER_IGNORE_REGEXPS[key];
		if (regexp.test(url)) {
			return true;
		}
	}
	return false;
};

findWSDLs = function(url) {
	nuxeo.runRestRequest(
		{username: 'Administrator', password: 'Administrator'},
		settings.EASYSOA_SERVICE_FINDER_PATH + '/' + url,
		'GET',
		null,
		null,
		function(data, error) {
			try {
				saveWSDLs(JSON.parse(data));
			}
			catch (error) {
				console.log("ERROR: While finding WSDLs:", error);
			}
		}
	);
};

sendWSDL = function(request, response, next) {
	body = 'url='+request.query.url+
	    '&environment='+request.query.environment+
	    '&title='+request.query.servicename+
	    '&discoveryTypeBrowsing=Discovered by '+request.session.username;
	nuxeo.runRestRequest(
		request.session,
		settings.EASYSOA_DISCOVERY_PATH,
		'POST',
		{'Content-Type':'application/x-www-form-urlencoded'},
		body,
		function(data, error) {
			if (data) {
				response.end('ok');
			}
			else {
				response.end(error);
			}
		}
	);
};

clearWSDLs = function(request, response, next) {
	foundWSDLs = [];
	response.end('ok');
};
