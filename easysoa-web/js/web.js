// EasySOA Web
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/* 
 * EasySOA Web server + Discovery by browsing proxy
 *
 * Author: Marwane Kalam-Alami
 */
 
var http = require('http');
var fs = require('fs');
var url = require('url');
var express = require('express');
var httpProxy = require('http-proxy');

var easysoaAuth = require('./web-auth.js');
var easysoaNuxeo = require('./web-nuxeo.js');
var easysoaDbb = require('./web-dbb.js');
var easysoaScraping = require('./web-scraping.js');
var easysoaLight = require('./web-light.js');

/* Settings */

eval(fs.readFileSync('settings.js', 'ASCII'));
settings.ignore.push('/login');
settings.ignore.push('/send');
settings.ignore.push('/scaffoldingProxy');
settings.ignore.push('/light/serviceList');

/* Handle errors without crashing */

process.on('uncaughtException', function (error) {
  console.error(error.stack);
});

/* Functions */

function isRequestToProxy(request) {
    return request.headers['host'] == "localhost:" + settings.proxyPort
            || request.headers['host'] == "127.0.0.1:" + settings.proxyPort;
}

function isRequestToSocketIO(request) {
    return request.url.indexOf("socket.io") != -1;
}

function isInIgnoreList(urlString) {
	if (urlString.indexOf("socket.io") != -1) {
		return true;
	}
    for (i in settings.ignore) {
	    if (urlString.indexOf(settings.ignore[i]) != -1) {
		    return true;
	    }
    }
    return false;
}

function fixUrl(url) {
   url = url.replace('localhost', '127.0.0.1');
   url = url.replace('/http://127.0.0.1:'+settings.webPort, ''); // URL fix (pb caused by Connect/Express?)
   return url;
}

function urlFixer(request, response, next) {
   request.url = fixUrl(request.url);
   next();
}

/* Web server */

var tunnelProxy = new httpProxy.HttpProxy();

var webServer = express.createServer();

webServer.configure(function(){
  //  webServer.use(express.logger({ format: ':method :url' }));
    webServer.use(express.cookieParser());
    webServer.use(express.session({ secret: "easysoa-web" }));
    webServer.use(express.bodyParser());
    webServer.use(urlFixer);
    webServer.use(easysoaAuth.authFilter);
    webServer.use(webServer.router);
    webServer.use(express.favicon(__dirname + '/favicon.ico', { maxAge: 0 }));
    webServer.use(express.static(__dirname + '/' + settings.webRoot));
    webServer.use(express.directory(__dirname + '/' + settings.webRoot));
});

webServer.get('/login', function(request, response, next) {
    response.redirect('/easysoa/login.html');
});
      
webServer.get('/send', function(request, response, next) {
      // Send notifications to Nuxeo on client request
      /**
       * Message from the client should have the following JSON format:
       * {
       *  'url': 'The URL',
       *  'applicationname': 'The applicationname',
       *  'servicename': 'The service name'
       * }
       */
       if (request.session.username != undefined) {
		   request.query.session = request.session;
		   easysoaNuxeo.registerWsdl(request.query, function(json) {
		       if (json.result) {
		           try {
		              response.write(json.result);
		           }
		           catch (error) {
		              response.write('Request failed');
		           }
		       }
		       else {
		          response.write('Unexpected response');
		       }
		       response.end();
		   });
       }
});

webServer.get('/clear', function(request, response, next) {
	easysoaDbb.clearWsdls();
	response.end();
});

// Tunnel Proxy rules :
// TODO LATER make it generic with a loop and a table in settings
scaffolding_server_url = url.parse(settings.scaffoldingServer);
webServer.get('/scaffoldingProxy', function(request, response, next) {
    console.log("[INFO] Tunnelling scaffolder: http://" + scaffolding_server_url.hostname + ":" + scaffolding_server_url.port + request.url);
    tunnelProxy.proxyRequest(request, response, {
        host: scaffolding_server_url.hostname,
        port: scaffolding_server_url.port
    });
});

webServer.get('/light/serviceList', function(request, response, next) {
	easysoaLight.fetchServiceList(request.session, function(data) {
		var responseData = new Object();
		responseData.success = (data !== false);
		responseData.data = data;
		response.write(JSON.stringify(responseData));
		response.end();
	});
});

// Dashboard requests routing to Nuxeo
webServer.all('/dashboard/*', function(request, response, next) {
    easysoaNuxeo.forwardToNuxeo(request, response, settings);
});

webServer.get('*', function(request, response, next) {
    // Socket.io compatibility
    if (request.url.indexOf('socket.io') != -1) {
        easysoaDbb.forceSocketIOHandling(request, response);
    }
    else {
        next();
    }
});

webServer.listen(settings.webPort);

easysoaDbb.startDiscoveryByBrowsingHandler(webServer);

      
/* Proxy server */
/* (TODO: Merge with web server?) */

var proxy = new httpProxy.HttpProxy();

proxy.on('proxyError', function(error, request, result) {
    result.write("<h1>Error "+error.errno+"</h1>");
    result.write("<p>"+error.message+"</p>");
    result.end();
});

var proxyServer = http.createServer(function(request, response) {

    request.url = fixUrl(request.url);
    request_url = url.parse(request.url);
        
	// Allow the client to know that it's configured correctly
	if (!isRequestToSocketIO(request)) {
		easysoaDbb.setClientWellConfigured(request);
	}
	
    // Proxying
    if (!isRequestToProxy(request)) {

        proxy.proxyRequest(request, response, {
            host: request_url.hostname,
            port: request_url.port
        });
      
        // Scraping
        if (!isInIgnoreList(request.url)) {
            console.log("[INFO] Scraping: " + request.url);
            easysoaScraping.sendUrlToScrapers(request_url, function(scraperResults) {
              for (var linkName in scraperResults) {
                 easysoaDbb.provideWsdl(linkName, scraperResults[linkName]);
              }
            });
        }
    }
    
});

easysoaNuxeo.checkNuxeo(null, null, function(result) {
    console.log("[INFO] Checking Nuxeo status");
    if (result != false) {
        console.log('[INFO] Nuxeo is ready for scraping/upload');
        easysoaDbb.setNuxeoReady();
    }
});

proxyServer.listen(settings.proxyPort);
