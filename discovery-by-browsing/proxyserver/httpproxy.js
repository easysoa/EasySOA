var http = require('http');
var fs = require('fs');
var url = require('url');
var base64 = require('./tools/base64.js');
var io = require('./tools/socket.io');

eval(fs.readFileSync('proxyserver/httpproxy-config.js', 'ASCII'));

var wsdlList = new Array();
var clients = null;
var nuxeo_notification = url.parse(config.nuxeo_notification);

function responseError(request, response, msg) {
	console.error("[ERROR] " + msg + " (on request for "+request.url+")");
	response.writeHead(404);
	response.write(msg);
	response.end();
}

process.on('uncaughtException', function (err) {
  console.error("[ERROR] Uncaught exception: "+err);
});

// Source: http://keithdevens.com/weblog/archive/2007/Jun/07/javascript.clone
function clone(obj) {
    if(obj == null || typeof(obj) != 'object')
        return obj;
    var temp = new obj.constructor();
    for (var key in obj)
        temp[key] = clone(obj[key]);
    return temp;
}

/**** HTTP Proxying ****/

var server = http.createServer(function(request, response) {

	var request_url = url.parse(request.url, true);

  // Don't allow exceptions
	if (request.headers['host'] == "localhost:"+config.proxy_port
        || request.headers['host'] == "127.0.0.1:"+config.proxy_port) {
		response.writeHead(200, {
			"Content-type": "text/javascript"
		});
		response.end();
		return;
	}
  
  //// Proxying
  
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

  //// Scraping
  
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
                      'applicationName': json.applicationName,
                      'messageType': 'wsdl'
                    };
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
		  msg = "Scraper unavailable: "+config.scrapers[scraper];
		  if (clients != null) {
  		  clients.broadcast("ERROR: "+msg);
  		}
			console.log("[WARN] "+msg);
		});
		scraper_request.end();
		
	}
});

server.listen(config.proxy_port);

/**** Check that Nuxeo is ready ****/

var nuxeoReady = false;

function checkNuxeo() {

  var request_options = {
	  port : nuxeo_notification.port,
	  method : 'POST',
	  host : nuxeo_notification.hostname,
	  path : nuxeo_notification.href+"service",
	  headers : {
	    'Content-Type': 'application/x-www-form-urlencoded'
	  }
  };
  
  // Test request
  var nx_site = http.createClient(nuxeo_notification.port, nuxeo_notification.hostname);
  nx_site.on('error', function(error) {
     console.log("[INFO] Failed to connect to Nuxeo: "+error);
  });
  var nx_request = nx_site.request('POST', nuxeo_notification.href, {'host': nuxeo_notification.hostname});
  nx_request.on('response', function(res) {
    if (!nuxeoReady) {
      console.log('[INFO] Nuxeo is ready for scraping/upload');
      nuxeoReady = true;
      if (clients != null) {
        clients.broadcast(JSON.stringify({'messageType':'ready'}));
      }
    }
  });
  nx_request.end();
	
  if (!nuxeoReady) {
	  console.log("[INFO] Nuxeo is not ready yet...");
    setTimeout(checkNuxeo, 3000);
  }
}

checkNuxeo();

/**** Send found WSDLs on connection ****/

function sendRestRequest(client, nuxeo_upload_options, body) {

  rest_request = http.request(nuxeo_upload_options, function(rest_response) {
	
      // Nuxeo response handling
      var data = "";
      rest_response.on('data', function(chunk) {
        data += chunk.toString("ascii");
      });
      rest_response.on('end', function() {
        var json;
        try {
          json = JSON.parse(data);
        }
        catch (error) {
          error = 'ERROR: Nuxeo response cannot be parsed into JSON ('+error+')';
          json = {'result': error};
          console.log("[WARN] "+error);
        }
        json.messageType = 'upload';
        client.send(JSON.stringify(json));
      });
      
  });
  
  rest_request.addListener('error', function(error) {
    console.log("[WARN] Failure while sending REST request to Nuxeo "+error);
  });
  rest_request.write(body);
  rest_request.end();
    
}

var io = io.listen(server);

io.on('connection', function(client){

  // Give global access to socket.io connections
  if (clients == null)
    clients = client;
    
  // Send stored WSDLs
  for (key in wsdlList) {
    client.send(JSON.stringify(wsdlList[key]));
  }
  
  // Notify that Nuxeo is ready
  if (nuxeoReady) {
    client.send(JSON.stringify({'messageType':'ready'}));
  }

  /**** Send notifications to Nuxeo on client request ****/

  /**
   * Message from the client should have the following JSON format:
   * {
   *  'url': 'The URL',
   *  'applicationname': 'The Application Name',
   *  'servicename': 'The Service Name'
   * }
   */
  client.on('message', function(string) {

    try {

      data = JSON.parse(string);
        
      // TODO : Make use of the refactored logic from Nuxeo when available
      var apiUrl = data.url.substring(0, data.url.lastIndexOf('/')); 
      var appliUrl = data.url.substring(0, data.url.lastIndexOf(':'));

      //// Appli. notification
    
      var body = 'url='+appliUrl+
          '&title='+data.applicationname;
      var nuxeo_upload_options = {
			  port : nuxeo_notification.port,
			  method : 'POST',
			  host : nuxeo_notification.hostname,
			  path : nuxeo_notification.href+"appliimpl",
			  headers : {
			    'Content-Type': 'application/x-www-form-urlencoded',
			    'Content-Length': body.length
			  }
		  };
      
      sendRestRequest(client, nuxeo_upload_options, body);
      
      setTimeout(function () { // XXX : Hack to ensure previous call is over
    	  
          //// API notification
    	  
	      body = 'url='+apiUrl+
	          '&parentUrl='+appliUrl+
	          '&application='+data.applicationname+
	          '&title='+data.servicename+' API';
	      nuxeo_upload_options = {
				  port : nuxeo_notification.port,
				  method : 'POST',
				  host : nuxeo_notification.hostname,
				  path : nuxeo_notification.href+"api",
				  headers : {
				    'Content-Type': 'application/x-www-form-urlencoded',
				    'Content-Length': body.length
				  }
			  };  
	      sendRestRequest(client, nuxeo_upload_options, body);
	      
	      setTimeout(function () { 

	          //// Service notification
	        
	          body = 'url='+data.url+
	              '&fileUrl='+data.url+
	              '&parentUrl='+apiUrl+
	              '&title='+data.servicename+
	              '&discoveryTypeBrowsing=Discovered by browsing';
	          nuxeo_upload_options = {
	    			  port : nuxeo_notification.port,
	    			  method : 'POST',
	    			  host : nuxeo_notification.hostname,
	    			  path : nuxeo_notification.href+"service",
	    			  headers : {
	    			    'Content-Type': 'application/x-www-form-urlencoded',
	    			    'Content-Length': body.length
	    			  }
	    		  };
	          sendRestRequest(client, nuxeo_upload_options, body);
	    	  
	      }, 300);
	      
      }, 300);
		  
    }
    catch (error) {
      console.error("[ERROR] Client message badly formatted. "+error);
    }
    
  });
  
});
