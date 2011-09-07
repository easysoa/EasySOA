/* 
 * Proxy which
 * WORK IN PROGRESS
 *
 * Author: Marwane Kalam-Alami
 */
 
var http = require('http');
var fs = require('fs');
var url = require('url');

var easysoaNuxeo = require('./web-proxy-nuxeo.js');
var easysoaDbb = require('./web-proxy-dbb.js');
var easysoaScraping = require('./web-proxy-scraping.js');
var easysoaProxy = require('./web-proxy.js');

var connect = require('connect');

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

/* Web server */

var webServer = connect.createServer(
    function(request, response, next) {
        request.url = request.url.replace('/http://localhost:'+settings.webPort, ''); // URL fix (pb caused by Connect?)
        if (request.url.indexOf('socket.io') != -1) {
            easysoaDbb.forceSocketIOHandling(request, response);
        }
        else {
            next();
        }
    }
  , connect.favicon()
  , connect.static(__dirname + '/' + settings.webRoot)
  , connect.directory(__dirname + '/' + settings.webRoot)
);

webServer.listen(settings.webPort);

easysoaDbb.startDiscoveryByBrowsingHandler(webServer);

      
/* Proxy server */

var proxyServer = connect.createServer(
    connect.cookieParser()
  , connect.session({ secret: "easysoa-web" })
  , function(request, response, next){
       
        // Proxying
        if (!isRequestToProxy(request)) {
            easysoaProxy.proxyRequest(request, response);
          
            // Scraping
            request_url = url.parse(request.url);
            if (!isInIgnoreList(request_url)) {
	            console.log("[INFO] Scraping: "+request.url);
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

easysoaNuxeo.checkNuxeo(function(result) {
    easysoaDbb.setNuxeoReady();
});

proxyServer.listen(settings.proxyPort);
