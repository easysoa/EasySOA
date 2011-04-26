// TODO : Allow client polling, implement scraper reponse

// /!\ : DOC When specifying the proxy, disable proxy bypass for localhost

var http = require('http');
var fs = require('fs');
var url = require('url');
var base64 = require('./tools/base64');

eval(fs.readFileSync('proxyserver/httpproxy-config.js', 'ASCII'));

var status = new Object();
status.foundLinks = new Object();

// Send found WSDLs in JSONP format (or JSON by default)
function getFoundWSDLs(callback) {
	var json_string = JSON.stringify(status);
	return (callback) ? callback+"("+json_string+");" : json_string;
}

// Scraper response handling
function scraperResponse(response) {
	var data = "";
	response.on('data', function(chunk) {
		data += chunk.toString("ascii");
	});
	response.on('end', function() {
		try {
			var json = JSON.parse(data);
			if (json.foundLinks) {
				for (link in json.foundLinks) {
					status.foundLinks[link] = json.foundLinks[link];			
					console.log("Found: "+link+" = "+json.foundLinks[link]);
				}
			}
		} catch (err) {
			console.log("Note: "+err.message);
		}
	});
}

// HTTP Proxy Server
http.createServer(function(request, response) {

		var request_url = url.parse(request.url, true);
	
		// If direct request to proxy, send found WSDLs
		if (request_url.host == "localhost:"+config.proxy_port) {
			response.writeHead(200, {
				"Content-Type": "text/javascript"
			});
			response.write(getFoundWSDLs(request_url.query.callback));
			response.end();
		}
	
		// Create request to wanted server
		if (!request_url.port)
			request_url.port = 80;
		
		var proxy_options = {
			port : request_url.port,
			method : request.method,
			host : request_url.hostname,
			path : request_url.href,
			headers : request.headers
		};

		var proxy_request = http.request(proxy_options,
				function(proxy_response) {

				// Write response to client
				proxy_response.on('data', function(chunk) {
					response.write(chunk, 'binary');
				});
				proxy_response.on('end', function() {
					response.end();
				});

				// Write response header to client
				response.writeHead(proxy_response.statusCode,
						proxy_response.headers);
			});
		
		// Error handling
		proxy_request.addListener('error', function(error) {
			response.writeHead(404);
			response.write(error.message);
			response.end();
		});

		// Send request to wanted server
		request.addListener('data', function(chunk) {
			proxy_request.write(chunk, 'binary');
		});
		request.addListener('end', function() {
			proxy_request.end();
		});

		// Don't scrape ignored paths
		for (i = 0; i < config.ignore.length; i++) {
			if (request_url.href.indexOf(config.ignore[i]) != -1) {
				return;
			}
		}

		console.log("Scraping: "+request_url.href);
		
		// Notification to scrapers
		for (scraper in config.scrapers) {
			var scraperURL = url.parse(config.scrapers[scraper]);
			headers = "";
			if (!scraperURL.port)
				scraperURL.port = 80;
			
			var scraperOptions = {
				port : scraperURL.port,
				method : 'GET',
				host : scraperURL.hostname,
				path : scraperURL.href.replace('?', request.url)
			};

			// Hard-coded Nuxeo auth
			if (scraperURL.href.indexOf("nuxeo/restAPI") != -1) {
				scraperOptions.headers = {
					'authorization': "Basic " + base64.encode("Administrator:Administrator")
				};
			}

			// Scraper request
			scraper_request = http.request(scraperOptions, function(scraper_response) {
				scraperResponse(scraper_response);
			});
			scraper_request.end();
			
		}

	}).listen(config.proxy_port);
