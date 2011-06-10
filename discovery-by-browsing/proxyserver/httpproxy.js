var http = require('http');
var fs = require('fs');
var url = require('url');
var base64 = require('./tools/base64.js');

eval(fs.readFileSync('proxyserver/httpproxy-config.js', 'ASCII'));

var status = new Object();
status.error = null;
status.foundLinks = new Object();

// Send found WSDLs in JSONP format (or JSON by default)
function getFoundWSDLs(callback) {
	var json_string = JSON.stringify(status);
	if (status.error != null) {
		status.error = null;
	}
	return (callback) ? callback+"("+json_string+");" : json_string;
}

// Scraper response handling
function scraperResponse(response) {
	var data = "";
	response.on('data', function(chunk) {
		console.log("received sthg");
		data += chunk.toString("ascii");
	});
	response.on('end', function() {
		var json = null;
		try {
			console.log("result:" +data);
			json = JSON.parse(data);
			if (json.foundLinks) {
				for (link in json.foundLinks) {
					status.foundLinks[link] = {
						'url': json.foundLinks[link],
						'serviceName': link,
						'applicationName': json.applicationName
					};			
					console.log("Found: "+link+" = "+json.foundLinks[link]);
				}
			}
		} catch (err) {
			console.log("[INFO] Note: "+err.message+" ("+data+")");
		}
	});
}

function responseError(request, response, msg) {
	console.log("[ERROR] " + msg + " (on request for "+request.url+")");
	response.writeHead(404);
	response.write(msg);
	response.end();
}

// HTTP Proxy Server
var server = http.createServer(function(request, response) {

	var request_url = url.parse(request.url, true);

	// If direct request to proxy, send found WSDLs
	if (request.headers['host'] == "localhost:"+config.proxy_port
        || request.headers['host'] == "127.0.0.1:"+config.proxy_port) {
		response.writeHead(200, {
			"Content-Type": "text/javascript"
		});
		response.write(getFoundWSDLs(request_url.query.callback));
		response.end();
		return;
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
	
	// Send request
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
		responseError(request, response, error.message);
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

	console.log("[INFO] Scraping: "+request_url.href);
	
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

		// Scraper request
		scraper_request = http.request(scraperOptions, function(scraper_response) {
		
      // Scraper response handling
        var data = "";
        scraper_response.on('data', function(chunk) {
          data += chunk.toString("ascii");
        });
        scraper_response.on('end', function() {
          try {
            var json = JSON.parse(data);
            if (json.foundLinks) {
              for (link in json.foundLinks) {
                var newLink = {
                      'url': json.foundLinks[link],
                      'serviceName': link,
                      'applicationName': json.applicationName
                    };
                console.log("[INFO] Found: "+newLink.url);
                if (clients != null) {
                  clients.broadcast(JSON.stringify(newLink));
                }
                wsdlList[link] = newLink;
              }
            }
          } catch (err) {
            console.log("[INFO] Note: "+err.message+" ("+data+")");
          }
        });
        
		});
		scraper_request.addListener('error', function(error) {
			status.error = "Scraper unavailable: "+config.scrapers[scraper];
			console.log("[WARN] "+status.error);
		});
		scraper_request.end();
		
	}
});

server.listen(config.proxy_port);

var io = io.listen(server);
io.on('connection', function(client){

  // Give global access to socket.io connections
  if (clients == null)
    clients = client;
  
  // Send stored WSDLs
  for (key in wsdlList) {
    client.send(JSON.stringify(wsdlList[key]));
  }
    
});
