// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var http = require('http');
var httpProxy = require('http-proxy');
var url = require('url');

var base64 = require('./lib/base64').base64;
var settings = require('./settings');

/**
 * Basic HTTP proxy component, using 'node-http-proxy'.
 *
 * Author: Marwane Kalam-Alami
 */


//=============== Model ================

NUXEO_AUTOMATION_PARSED_URL = url.parse(settings.NUXEO_AUTOMATION_URL);

var ready = false;

//================ I/O =================

computeAuthorization = function(username, password) {
    if (username != null && password != null) {
        return "Basic " + base64.encode(username + ':' + password);
    }
    else {
        return null;
    }
};

exports.runAutomationRequest = runAutomationRequest = function(session, operation, params, headers, callback, method) {

  // Normalize optional params
  if (operation == null)
	  operation = '';
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
	  'port' : NUXEO_AUTOMATION_PARSED_URL.port,
	  'method' : method || 'POST',
	  'host' : NUXEO_AUTOMATION_PARSED_URL.hostname,
	  'path' : NUXEO_AUTOMATION_PARSED_URL.href+'/'+operation,
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
	return true;//return ready;
};

exports.areCredentialsValid = function(username, password, callback) {
	runAutomationRequest({username: username, password: password},
		null, null, null,
		function(data) {
			callback(data[0] == "{"); // If login succeeded, we get the automation doc
		},
		method='GET'
	);
};
