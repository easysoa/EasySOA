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

var ready = false;

//============= Controller =============

exports.isReady = function() {
	return ready;
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

exports.runAutomationRequest = function(session, operation, params, headers, callback) {

	  // Normalize optional params
	  if (params == null)
		  params = '';
	  if (headers == null)
	      headers = {};
	  if (callback == null)
	      callback = function() {};
	    
	  headers['Content-Type'] = 'application/json+nxrequest';
	  headers['Accept'] = 'application/json+nxentity, */*';
	  headers['Authorization'] = computeAuthorization(session.username, session.password);

	  var requestOptions = {
		  'port' : nuxeoAutomation.port,
		  'method' : 'POST',
		  'host' : nuxeoAutomation.hostname,
		  'path' : nuxeoAutomation.href+'/'+operation,
		  'headers' : headers
	  };
	  
	  var body = new Object();
	  body.params = params;
	  body.context = {};
	  
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
	    callback(false);
	  });
	  nxRequest.write(JSON.stringify(body));
	  nxRequest.end();

	};
