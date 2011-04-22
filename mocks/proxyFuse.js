var http = require('http');
var proxiedHost = 'localhost';
var proxiedPort = '8080'; // mostly for tests else port
var host = 'localhost';
var port = '8081';

var client = http.createClient(proxiedPort, proxiedHost);

var serviceCalls = {};

function deepObjCopy (dupeObj) {
	var retObj = new Object();
	if (typeof(dupeObj) == 'object') {
		if (typeof(dupeObj.length) != 'undefined')
			var retObj = new Array();
		for (var objInd in dupeObj) {	
			if (typeof(dupeObj[objInd]) == 'object') {
				retObj[objInd] = deepObjCopy(dupeObj[objInd]);
			} else if (typeof(dupeObj[objInd]) == 'string') {
				retObj[objInd] = dupeObj[objInd];
			} else if (typeof(dupeObj[objInd]) == 'number') {
				retObj[objInd] = dupeObj[objInd];
			} else if (typeof(dupeObj[objInd]) == 'boolean') {
				((dupeObj[objInd] == true) ? retObj[objInd] = true : retObj[objInd] = false);
			}
		}
	}
	return retObj;
}

function sendResponse(res, proxiedRes, proxiedResBody) {
  var resHeaders = proxiedRes.headers;
  resHeaders['content-type'] = 'text/html';
  res.writeHead(proxiedRes.statusCode, resHeaders);
  proxiedRes.setEncoding('utf8');
  var jsonProxiedRes = eval('(' + proxiedResBody + ')');
  var jsonRes = deepObjCopy(jsonProxiedRes);
  res.write('<html><body><pre>');  
  res.write(JSON.stringify(jsonRes), function(k, v) {
     if (k = 'path') {
        return 'http://' + host + ':' + port + '/nuxeo/site/' + v;
     }
     return v;
  });
  res.write('</pre></body></html>');
  res.end();
}

function proxyRequest(req, res) {
var method = req.method;
var url = req.url;
var serviceCall = serviceCalls[url];
var lastCallDate = serviceCall;
var reqDate = new Date();
if (!serviceCall || reqDate - lastCallDate > 10000) {
   serviceCalls[url] = reqDate;
} else {
   res.writeHead(500, {});
   res.write('<html><body>Error: service ' + url +  ' has been called too much: time since last call ' + (reqDate - lastCallDate) + '</body></html>');
   res.end();
}

var headers = { 'Authorization': 'Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y' };
for (h in req.headers) {
   headers[h] = req.headers[h];
}
headers.host = proxiedHost + ':' + proxiedPort;
headers.Authorization = 'Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y';
headers.cookie = null;
headers.COOKIE_SUPPORT = false;
headers.LOGIN = null;
//headers.a = 'override';
var request = client.request(method, url, headers);
console.log('SENT HEADERS: ' + JSON.stringify(headers));
//  {'host':proxiedHost + ':' + proxiedPort,
//   'Authorization': 'Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y',
//   'Accept': 'application/json+nxentity, */*',
//   'Content-Type': 'application/json+nxrequest; charset=UTF-8',
//   'X-NXVoidOperation': true,
//   'X-NXDocumentProperties': '*',
//   'Content-Length':body.length});
req.on('data', function (chunk) {
   request.write(chunk);
});
req.on('end', function () {
   request.end();
});

request.on('response', function (proxiedRes) {
  console.log('STATUS: ' + proxiedRes.statusCode);
  console.log('RECEIVED HEADERS: ' + JSON.stringify(proxiedRes.headers));

  var body = '';
  proxiedRes.on('data', function (chunk) {
    //res.write(chunk); // encoding='utf8'
    body += chunk;
  });
  proxiedRes.on('end', function() {
    console.log('END');
    //res.end();
    sendResponse(res, proxiedRes, body);
  });
});

}


http.createServer(function (req, res) {
    proxyRequest(req, res);
}).listen(port, host);
console.log('Server running at http://' + host + ':' + port + '/');

