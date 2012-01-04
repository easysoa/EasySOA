// EasySOA Web
// Copyright (c) 2011 -2012Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/**
 * Authentication components, service both the authentication service & filter.
 *
 * Author: Marwane Kalam-Alami
 */

authFilter = function(request, response, next) {
  next();
};

exports.configure = function(webServer) {
	// Authentication filter
	webServer.use(authFilter);
	  
	// Router configuration
	webServer.post('/login', login);
};
	
login = function(request, response, next) {
	// TODO
	next();
};