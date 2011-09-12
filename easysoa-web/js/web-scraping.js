/* 
 * Provides functions to gather data from scrapers.
 *
 * Author: Marwane Kalam-Alami
 */
 
var http = require('http');
var fs = require('fs');
var url = require('url');
var easysoaDbb = require('./web-dbb.js');


eval(fs.readFileSync('./settings.js', 'ASCII'));

exports.sendUrlToScrapers = function(request_url, callback) {
    
    for (scraper in settings.scrapers) {
    
	    var scraperURL = url.parse(settings.scrapers[scraper]);
	    headers = "";
	    if (!scraperURL.port)
		    scraperURL.port = 80;
	
	    var scraperOptions = {
		    port : scraperURL.port,
		    method : 'GET',
		    host : scraperURL.hostname,
		    path : scraperURL.href.replace('?', request_url.href)
	    };
              
	    // Scraper request
	    scraperRequest = http.request(scraperOptions, function(scraperResponse) {
		
            // Scraper response handling
            var data = "";
            scraperResponse.on('data', function(chunk) {
              data += chunk.toString("ascii");
            });
            scraperResponse.on('end', function() {
              try {
                 var scraperResults = new Array();
                 var json = JSON.parse(data);
                 if (json.foundLinks) {
                   for (link in json.foundLinks) {
                     console.log("[INFO] Found: "+json.foundLinks[link]);
                     var newLink = {
                          'url': json.foundLinks[link],
                          'serviceName': link,
                          'applicationName': json.applicationName
                        };
                     scraperResults[link] = newLink;
                   }
                 }
                 callback(scraperResults);
              } catch (err) {
                console.log("[INFO] Note: "+err.message+" ("+data+")");
              }
            });
           
        });
            
        scraperRequest.addListener('error', function(error) {
            msg = "Scraper unavailable: "+settings.scrapers[scraper];
            easysoaDbb.broadcastemit('error', "ERROR: "+msg);
      		console.log("[WARN] "+msg);
        });
	     
	    scraperRequest.end();
	
    }

}
