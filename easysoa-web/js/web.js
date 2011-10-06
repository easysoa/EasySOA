/* 
 * Proxy which
 * WORK IN PROGRESS
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

eval(fs.readFileSync('settings.js', 'ASCII'));

/* Handle errors without crashing */

process.on('uncaughtException', function (error) {
  console.error(error.stack);
});

/* Functions */

function isRequestToProxy(request) {
    return request.headers['host'] == "localhost:" + settings.proxyPort
            || request.headers['host'] == "127.0.0.1:" + settings.proxyPort;
}

function isInIgnoreList(request_url) {
    for (i in settings.ignore) {
	    if (request_url.pathname.indexOf(settings.ignore[i]) != -1
	        || request_url.pathname.indexOf("socket.io") != -1) {
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

var proxy = new httpProxy.HttpProxy();

var webServer = express.createServer();

webServer.configure(function(){
  //  webServer.use(express.logger({ format: ':method :url' }));
    webServer.use(express.cookieParser());
    webServer.use(express.session({ secret: "easysoa-web" }));
    webServer.use(express.bodyParser());
    webServer.use(urlFixer);
    webServer.use(easysoaAuth.authFilter);
    webServer.use(webServer.router);
    webServer.use(express.favicon());
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

webServer.get('/scaffoldingProxy', function(request, response, next) {
	request.url = settings.scaffoldingServer + request.url;
    request_url = url.parse(request.url);
    console.log(request.url);
    proxy.proxyRequest(request, response, {
        host: request_url.hostname,
        port: request_url.port
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

proxy.on('proxyError', function(error, request, result) {
    result.write("<h1>Error "+error.errno+"</h1>");
    result.write("<p>"+error.message+"</p>");
    result.end();
});

var proxyServer = http.createServer(function(request, response) {

        // Proxying
        if (!isRequestToProxy(request)) {

            request.url = fixUrl(request.url);
            request_url = url.parse(request.url);
            proxy.proxyRequest(request, response, {
                host: request_url.hostname,
                port: request_url.port
            });
          
            // Scraping
            if (!isInIgnoreList(request_url)) {
                console.log("[INFO] Scraping: " + request.url);
                easysoaScraping.sendUrlToScrapers(request_url, function(scraperResults) {
                  for (var linkName in scraperResults) {
                     easysoaDbb.provideWsdl(linkName, scraperResults[linkName]);
                  }
                });
            }
        }
        else { 
            request.url = request.url.replace('http://localhost:'+settings.proxyPort, '');
        }
    }
);

easysoaNuxeo.checkNuxeo(null, null, function(result) {
    console.log("[INFO] Checking Nuxeo status");
    if (result != false) {
        console.log('[INFO] Nuxeo is ready for scraping/upload');
        easysoaDbb.setNuxeoReady();
    }
});

proxyServer.listen(settings.proxyPort);
