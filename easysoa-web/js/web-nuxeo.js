/* 
 * Provides functions to communicate with the Nuxeo server.
 *
 * Author: Marwane Kalam-Alami
 */
 
var http = require('http');
var fs = require('fs');
var url = require('url');
var base64 = require('base64');

eval(fs.readFileSync('./settings.js', 'ASCII'));

var nuxeoReady = false;
var nuxeoAutomation = url.parse(settings.nuxeoAutomation);

// INTERNAL FUNCTIONS

computeAuthorization = function(username, password) {
    if (username != null && password != null) {
        return "Basic " + base64.encode(username + ':' + password);
    }
    else {
        return null;
    }
}

sendNotification = function(nuxeo_upload_options, body, callback) {

      rest_request = http.request(nuxeo_upload_options, function(rest_response) {
	
          // Nuxeo response handling
          var data = "";
          rest_response.on('data', function(chunk) {
            data += chunk.toString("ascii");
          });
          rest_response.on('end', function() {
            callback(data);
          });
          
      });
      
      rest_request.addListener('error', function(error) {
        console.log("[WARN] Failure while sending REST request to Nuxeo "+error);
        callback(error);
      });
      rest_request.write(body);
      rest_request.end();
    
};

// EXPORTS

/*
 *
 */
exports.checkNuxeo = function(username, password, callback) { /* TODO Rename function */

  var requestOptions = {
	  port : nuxeoAutomation.port,
	  method : 'GET',
	  host : nuxeoAutomation.hostname,
	  path : nuxeoAutomation.href,
	  headers : {
	    'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': computeAuthorization(username, password)
	  }
  };
  
  // Test request

  var nxRequest = http.request(requestOptions, function(response) {
        var responseData = "";
        response.on('data', function(data) {
              responseData += data;
          });
		response.on('end', function() {
            if (!nuxeoReady) {
                nuxeoReady = true;
            }
			callback(responseData);
		});
  });
	
  nxRequest.end();
	
  if (!nuxeoReady) {
	  console.log("[INFO] Nuxeo is not ready yet...");
      setTimeout(function() { 
              exports.checkNuxeo(username, password, callback) 
          }, 3000);
  }
  
};


/*
 *
 */
exports.registerWsdl = function(data, session, callback) {

    try {
    
      // Service notification
      
      console.log("[INFO] Registering: "+data.url);
        
      var body = 'url='+data.url+
          '&fileUrl='+data.url+
          '&title='+data.servicename+
          '&discoveryTypeBrowsing=Discovered by browsing';
      var nuxeo_upload_options = {
	          port : nuxeoNotification.port,
	          method : 'POST',
	          host : nuxeoNotification.hostname,
	          path : nuxeoNotification.href+"service",
	          headers : {
	            'Content-Type': 'application/x-www-form-urlencoded',
	            'Content-Length': body.length,
	            'Authorization': computeAuthorization(session.username, session.password)
	          }
          };
          
      sendNotification(nuxeo_upload_options, body, callback);
        
    }
    catch (error) {
      console.error("[ERROR] Client message badly formatted. "+error);
    }
        
}
