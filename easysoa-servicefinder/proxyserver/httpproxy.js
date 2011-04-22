// TODO : Allow client polling, implement scraper reponse

// /!\ : DOC When specifying the proxy, disable proxy bypass for localhost

var http = require('http');
var fs = require('fs');
var url = require('url');
var base64 = require('./tools/base64');

eval(fs.readFileSync('proxyserver/httpproxy-config.js', 'ASCII'));
var foundWSDLs = new Array();

// Scraper response handling
function scraperResponse(response) {
	var data;
	response.on('data', function(chunk) {
		data += chunk.toString("ascii");
	});
	response.on('end', function() {
		try {
			var json = JSON.parse(data);
			for (link in json.foundLinks) {
				var entry;
				entry.name = link;
				entry.url = json.foundLinks[link];
				foundWSDLs.push(entry);
			}
			console.log("array : " + foundWSDLs);
		} catch (err) {
			console.log(err);
		}
	});
}

// HTTP Proxy Server
http.createServer(function(request, response) {

		// Create request to wanted server
		var proxy_options = {
			port : 80,
			method : request.method,
			host : request.headers['host'],
			path : request.url,
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
				path : scraperURL.href.replace('?', request.url),
				headers : {
					'authorization' : "Basic " + base64.encode("Administrator:Administrator")
				}
			};

			console.log(scraperOptions.path);
			scraper_request = http.request(scraperOptions, function(scraper_response) {
				scraperResponse(scraper_response);
			});
			scraper_request.end();
			
		}

	}).listen(config.proxy_port);
