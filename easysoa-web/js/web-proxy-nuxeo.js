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

var AUTHORIZATION_VALUE = "Basic " + base64.encode('Administrator:Administrator');

var nuxeoReady = false;
var nuxeoNotification = url.parse(settings.nuxeoNotification);

function sendNotification(nuxeo_upload_options, body, callback) {

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

/*
 *
 */
exports.checkNuxeo = function(callback) { /* TODO Rename function */

  var request_options = {
	  port : nuxeoNotification.port,
	  method : 'POST',
	  host : nuxeoNotification.hostname,
	  path : nuxeoNotification.href+"service",
	  headers : {
	    'Content-Type': 'application/x-www-form-urlencoded'
	  }
  };
  
  // Test request
  var nx_site = http.createClient(nuxeoNotification.port, nuxeoNotification.hostname);
  nx_site.on('error', function(error) {
     console.log("[INFO] Failed to connect to Nuxeo: "+error);
  });
  var nx_request = nx_site.request('POST', nuxeoNotification.href, {'host': nuxeoNotification.hostname});
  nx_request.on('response', function(res) {
    if (!nuxeoReady) {
      console.log('[INFO] Nuxeo is ready for scraping/upload');
      nuxeoReady = true;
      callback();
    }
  });
  nx_request.end();
	
  if (!nuxeoReady) {
	  console.log("[INFO] Nuxeo is not ready yet...");
      setTimeout(function() { 
              exports.checkNuxeo(callback) 
          }, 3000);
  }
  
};


/*
 *
 */
exports.registerWsdl = function(data, callback) {

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
	            'Authorization': AUTHORIZATION_VALUE
	          }
          };
          
      sendNotification(nuxeo_upload_options, body, callback);
        
    }
    catch (error) {
      console.error("[ERROR] Client message badly formatted. "+error);
    }
        
}
