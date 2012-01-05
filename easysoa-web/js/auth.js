// EasySOA Web
// Copyright (c) 2011 -2012Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var settings = require('./settings');

/**
 * Authentication components, service both the authentication service & filter.
 *
 * Author: Marwane Kalam-Alami
 */

//========== Initialization ============

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
NO_AUTH_NEEDED_REGEXP = stringToRegexp(noAuthNeeded);

//================ I/O =================

exports.configure = function(webServer) {
	// Authentication filter
	webServer.use(authFilter);
	  
	// Router configuration
	webServer.post('/login', login);
	webServer.get('/logout', logout);
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
	// TODO
	next();
};

logout = function(request, response, next) {
	// TODO
	next();
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
	var destinationUrl = LOGIN_FORM_PATH + '?'
		 + ((request.body && request.body.prev) ? 'prev=' + request.body.prev + '&' : '')
		 + ((nuxeoNotReady) ? 'nuxeoNotReady=true' : '');
	response.redirect(destinationUrl);
};
