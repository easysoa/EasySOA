// EasySOA Web
// Copyright (c) 2011 -2012Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var settings = require('./settings');
var nuxeo = require('./nuxeo');

/**
 * Authentication components, service both the authentication service & filter.
 *
 * Author: Marwane Kalam-Alami
 */

//========== Initialization ============

JSONP_HEADERS = {'Content-Type': 'application/javascript'};
LOGIN_FORM_PATH = '/easysoa/login.html';

stringToRegexp = function(strings) {
	var result = [];
	for (var key in strings) {
		result.push(new RegExp(strings[key]));
	}
	return result;
};
var noAuthNeeded = settings.NO_AUTH_NEEDED;
noAuthNeeded.push(LOGIN_FORM_PATH);
noAuthNeeded.push('/login');
NO_AUTH_NEEDED_REGEXP = stringToRegexp(noAuthNeeded);

//================ I/O =================

exports.configure = function(webServer) {
	// Authentication filter
	webServer.use(authFilter);
	  
	// Router configuration
	webServer.post('/login', login);
	webServer.get('/logout', logout);
	webServer.get('/userdata', getUserdata);
};

authFilter = function(request, response, next) {
  if (isRequestAuthorized(request)) {
	  next();
  }
  else {
	  redirectToLoginForm(request, response);
  }
};

//============= Controller =============

login = function(request, response, next) {
	var params = request.body || request.query;
    if (params && params.username && params.password) {
    	if (nuxeo.isReady()) {
	    	try {
	    		nuxeo.areCredentialsValid(params.username, params.password, function(isValid) {
		            if (isValid) {
		            	// Create session
		                request.session.username = params.username;
		                request.session.password = params.password; // XXX: Could store the Base64 hash instead
		                console.log("[INFO] Session created for: "+params.username);
		            	if (params.callback) {
		            		response.writeHead(200, JSONP_HEADERS);
		            		response.end(params.callback + '({result: "ok"})');
		            	}
		            	else {
		            		response.redirect(params.prev || '/easysoa');
		            	}
		            }
		            else {
		            	redirectToLoginForm(request, response);
		            }
		        });
	    	}
	    	catch (error) {
	    		console.log("ERROR:", error);
	        	redirectToLoginForm(request, response);
	    	}
    	}
    	else {
        	redirectToLoginForm(request, response, true);
    	}
    }
    else {
    	// No credentials provided
		var message = "No credentials provided";
    	if (params.callback) {
    		response.writeHead(400, JSONP_HEADERS);
    		message = params.callback + '(' + message + ')';
    	}
		response.end(message);
    }
};

logout = function(request, response, next) {
	var username = request.session.username;
    request.session.destroy();
    console.log("[INFO] Session destroyed for: "+username);
	if (request.query && request.query.callback) {
		response.writeHead(200, JSONP_HEADERS);
		message = params.callback + '({result: "ok"})';
	}
	else {
		response.redirect('/easysoa');
	}
};

getUserdata = function(request, response, next) {
    if (request.session && request.session.username) {
        var responseData = new Object();
        responseData.username = request.session.username;
        if (request.query && request.query.callback) { // JSONP support
            response.write(request.query.callback + '(' + JSON.stringify(responseData) + ')');
        }
        else {
            response.write(JSON.stringify(responseData));
        }
    }
    else {
        if (request.query && request.query.callback) { // JSONP support
            response.write(request.query.callback + '()');
        }
        else {
        	response.writeHead(403);
        }
    }
    response.end();
};

isRequestAuthorized = function(request) {
	if (isAuthenticated(request)) {
		return true;
	}
	else {
		for (var key in NO_AUTH_NEEDED_REGEXP) {
			var regex = NO_AUTH_NEEDED_REGEXP[key];
			if (regex.test(request.url)) {
				return true;
			}
		}
	}
	return false;
};

isAuthenticated = function(request) {
	return request.session.username != undefined;
};

redirectToLoginForm = function(request, response, nuxeoNotReady) {
	if (request.query && request.query.callback) {
		if (nuxeoNotReady) {
			response.writeHead(500, JSONP_HEADERS);
			response.end(credentials.callback + '({result: "error", error: "Internal error: Nuxeo not started"})');
		}
		else {
			response.writeHead(403, JSONP_HEADERS);
			response.end(credentials.callback + '({result: "error", error: "Forbidden"})');
		}
	}
	else {
		var destinationUrl = LOGIN_FORM_PATH + '?'
			 + ((request.body && request.body.prev) ? 'prev=' + request.body.prev + '&' : '')
			 + ((nuxeoNotReady) ? 'nuxeoNotReady=true' : '');
		response.redirect(destinationUrl);
	}
};
