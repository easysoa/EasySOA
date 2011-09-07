var http = require('http');
var fs = require('fs');
var url = require('url');
var io = require('socket.io');

eval(fs.readFileSync('settings.js', 'ASCII'));

exports.proxyRequest = function(request, response) {
  
    request_url = url.parse(request.url.replace('/http', 'http'), true);
    request.url = request_url.href + request_url.search;
  
	// Create request to wanted server
	var proxy_options = {
		port : request_url.port || 80,
		method : request.method,
		host : request_url.hostname,
		path : request_url.href,
		headers : request.headers
	};
	
	// Clean request
	if (proxy_options.host == "localhost") {
		proxy_options.host = "127.0.0.1"; // else node.js raises "ENOTFOUND, Domain name not found" error
	}
  
	// Send request
	var proxy_request = http.request(proxy_options, function(proxy_response) {

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
	
}
