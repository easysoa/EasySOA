/**
  * Fake JS scraper. Scrapers should be easily pluggable to the service finder.
  */

var http = require('http');

http.createServer(

	function(request, response) {
		
		console.log("Request: " + request.url);
		response.writeHead(200);
		response.write(JSON.stringify({
      "url": request.url,
      "foundLinks": {"link1": "url1"}
    }));
		response.end();
		
	}
	
).listen(8082);

