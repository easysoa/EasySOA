// EasySOA Web
// Copyright (c) 2011-2012 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

require('longjohn');
var http = require('http');
var express = require('express');

var settings = require('./settings');
var authComponent = require('./auth');
var proxyComponent = require('./proxy');
var dbbComponent = require('./dbb');
var lightComponent = require('./light');
var nuxeoComponent = require('./nuxeo');
var wireframe = require('./wireframe');

/**
 * Application entry point.
 * Serves both an EasySOA Web server & a discovery by browsing proxy
 *
 * Author: Marwane Kalam-Alami
 */

/*
 * Set up web server
 */
var webServer = express.createServer();
webServer.configure(function(){

  // Request formatting
  webServer.use(express.cookieParser());
  webServer.use(express.session({ secret: 'easysoa-web' }));
  webServer.use(express.bodyParser());
  
  // Components routing & middleware configuration
  authComponent.configure(webServer);
  dbbComponent.configure(webServer);
  lightComponent.configure(webServer);
  nuxeoComponent.configure(webServer);
  
  // Router
  webServer.use(webServer.router);
  
  // Static file server
  webServer.use(express.favicon(settings.WWW_PATH + '/favicon.ico'));
  webServer.use(express.static(settings.WWW_PATH));
  webServer.use(express.directory(settings.WWW_PATH));
    
});
wireframe.configure(webServer);
webServer.listen(settings.WEB_PORT);

nuxeoComponent.startConnectionChecker();

/*
 * Set up proxy server
 */
var proxyServer = http.createServer(function(request, response) {
  
  // Service finder
  dbbComponent.handleProxyRequest(request, response);
  
  // Proxy
  proxyComponent.handleProxyRequest(request, response);
  
});
proxyServer.listen(settings.PROXY_PORT);
