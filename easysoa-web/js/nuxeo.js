// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var http = require('http');
var url = require('url');

var base64 = require('./lib/base64').base64;
var settings = require('./settings');
var proxy = require('./proxy');
var utils = require('./utils');

/**
 * Basic HTTP proxy component, using 'node-http-proxy'.
 *
 * Author: Marwane Kalam-Alami
 */


//=============== Model ================

NUXEO_REST_PARSED_URL = url.parse(settings.NUXEO_REST_URL);
EASYSOA_ROOT_URL_PARSED = url.parse(settings.EASYSOA_ROOT_URL);

var ready = false;

//================ I/O =================

exports.configure = function(webServer) {
	webServer.get('/nuxeo/discovery*', forwardToNuxeo);
	webServer.get('/nuxeo/servicefinder*', forwardToNuxeo);
};

forwardToNuxeo = function(request, response, next) {
	var parsedUrl = url.parse(request.url);
	proxy.forwardTo(request, response,
			EASYSOA_ROOT_URL_PARSED.hostname,
			EASYSOA_ROOT_URL_PARSED.port,
			EASYSOA_ROOT_URL_PARSED.path + parsedUrl.path.replace('/nuxeo', ''));
};

exports.runRestRequest = runRestRequest = function(session, path, method, headers, body, callback) {

  // Normalize optional params
  method = method || 'GET';
  headers = headers || {};
  body = body || '';
  callback = callback || (function() {});
   
  if (session && session.username) {
	  headers['Authorization'] = utils.encodeAuthorization(session.username, session.password);
  }

  var requestOptions = {
	  'port' : NUXEO_REST_PARSED_URL.port,
	  'method' : method,
	  'host' : NUXEO_REST_PARSED_URL.hostname,
	  'path' : NUXEO_REST_PARSED_URL.path + '/' + path,
	  'headers' : headers
  };
  
  var nxRequest = http.request(requestOptions, function(response) {
        var responseData = '';
        response.on('data', function(data) {
              responseData += data;
          });
		response.on('end', function() {
            callback(responseData);
		});
  });
  
  nxRequest.on('error', function(data) {
    callback(false, data);
  });
  nxRequest.write(JSON.stringify(body));
  nxRequest.end();

};

exports.runAutomationRequest = runAutomationRequest = function(session, operation, params, headers, callback, method) {
	
  // Normalize optional params (callback is also opt.)
  operation = operation || '';
  params = params || '';
  headers = headers || {};
  method = method || 'POST';
      
  headers['Content-Type'] = 'application/json+nxrequest';
  headers['Accept'] = 'application/json+nxentity, */*';
  body = {
	context: {},
	params: params
  };

  runRestRequest(session, 'automation/' + operation,
		  method || 'POST', headers, body, callback);
};

exports.runAutomationDocumentQuery = function(session, query, schemasToInclude, callback) {
	if (session.username != null) {
		nuxeo.runAutomationQuery(
			session,
			'Document.Query',
			{'query': query},
			(schemasToInclude) ? {'X-NXDocumentProperties': 'dublincore, servicedef'} : null,
			callback
		);
	}
	else {
		callback(false);
	}
};


//============= Controller =============

exports.isReady = function() {
	return ready;
};

exports.areCredentialsValid = areCredentialsValid = function(username, password, callback) {
	runAutomationRequest({username: username, password: password},
		null, null, null,
		function(data) {
			callback(data[0] == "{"); // If login succeeded, we get the automation doc
		},
		method='GET'
	);
};

exports.startConnectionChecker = function() {
	console.log('Checking if Nuxeo is ready...');
	checkConnection();
};

checkConnection = function() {
	areCredentialsValid('Administrator', 'Administrator', function(valid) {
		if (valid) {
			console.log('Nuxeo is ready.');
			ready = true;
		}
		else {
			console.log("...");
			setTimeout(checkConnection, 2000);
		}
	});
};