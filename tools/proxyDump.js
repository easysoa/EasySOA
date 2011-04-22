//
// Tool for gathering data about HTTP exchanges, in order to help designing the easysoa model.
//
//
// Listens to HTTP exchanges in proxy or tunnel mode. For each request / response, parses a few fields and write them to file as a CSV line using https://github.com/koles/ya-csv .
//
// How to use it :
// * in proxy mode : start it on an unused port, configure your HTTP client application to use it as a proxy.
// * in tunnel mode : configure it to use your HTTP server application as a server (tunneledHost, proxiedPort), start it on an unused port, configure your HTTP client application to use it as a server.
// * use your HTTP client application (with a Nuxeo server, samples/nuxeo/nuxeoAutomationQuery[Fetch[Children]]_proxied.js can be used) and watch lines being added to proxy-log.csv .
//
//
// Each CSV line is as follows :
// "businessUseCase";"method";"res.statusCode"[;url elements][;path elements][;params][;request headers][;response headers];"url";"parameters";"req.headers";"res.headers"
// where the following parts can be configured below :
//   businessUseCase : "user operation" tag / column, entered from command line.
//   path elements : Each path column contains one or a range of path elements, addressed by index or -m for the n-m path element, where n+1 is the total count of path elements.
//   params : Each parameter column contains a request parameter. If the body is JSON, it is parsed and flattened. It is then merged with URL parameters.
//   request headers : Each request header column contains a request header.
//   response headers : Each response header column contains a response header.
//
// Sample title and first lines :
// "businessUseCase";"method";"res.statusCode";"pathname";"path.1";"path.2-3";"path.n-1";"param.params.query";"param.input";"param.context";"req.host";"res.content-type";"res.date";"url";"parameters";"req.headers";"res.headers"
// "nuxeoAutomationQuery";"POST";"200";"/nuxeo/site/automation/Document.Query";"nuxeo";"site/automation";"Document.Query";"select * from Document";"";"";"localhost";"application/json+nxentity";"Fri, 15 Apr 2011 10:39:15 GMT";"/nuxeo/site/automation/Document.Query";"{""params.query"":""select * from Document""}";"{""host"":""localhost"",""authorization"":""Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y"",""accept"":""application/json+nxentity, */*"",""content-type"":""application/json+nxrequest; charset=UTF-8"",""x-nxdocumentproperties"":""*"",""content-length"":""45"",""connection"":""close""}";"{""date"":""Fri, 15 Apr 2011 10:39:15 GMT"",""expires"":""Thu, 01 Jan 1970 00:00:00 GMT"",""set-cookie"":[""JSESSIONID=15a2p1j6fumfi;Path=/nuxeo""],""content-type"":""application/json+nxentity"",""connection"":""close"",""server"":""Jetty(6.1H.7)""}"
//
//
// Written starting from proxyFuse.js .
// see also http://www.catonmat.net/http-proxy-in-nodejs
// existing node.js alternative (providing reverse proxy...) : https://github.com/nodejitsu/node-http-proxy
// existing alternative software : wireshark
//
// possible improvements :
// parse xml request body
// more flexible column ordering
// filters : ex. filteredContentTypes (image/jpeg...)
// packaging (module), command line options, share code with other proxy*.js
//


var http = require('http');
var fs = require('fs');
var csv = require('ya-csv');


// HTTP configuration
var tunneledHost = null; // http proxy
//var tunneledHost = '192.168.2.196'; // http tunnel
//var proxiedPort = port; 
var proxiedPort = '8080'; // mostly for tests
//var proxiedPort = '80'; // mostly for tests
var host = 'localhost';
//var host = '192.168.2.211'; // if remote
var port = '8081';

// CSV line configuration
var urlElementsToExtract = { 'pathname':null };
var urlPathElementsToExtract = [ [1], [2, 3], [-1] ]; // NB. 0 is /, 1 usually the application, -1 (latest) the service
//var parametersToExtract = {};
//var parametersToExtract = { 'q':null }; // for google
var parametersToExtract = { 'params.query':null, 'input':null, 'context':null }; // for nuxeo
var headersToExtract = { 'host':null };
var responseHeadersToExtract = { 'content-type':null, 'date':null };

// CSV configuration
var csvFilePath = 'proxy_log.csv';
var csvSeparator = ';';


// default init
var businessUseCase = '';



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

function flatten (dupeObj, res, nameContext) {
        if (typeof nameContext == 'undefined') {
          var nameContext = '';
        }
	var retObj = new Object();
	if (typeof(dupeObj) == 'object') {
		if (typeof(dupeObj.length) != 'undefined')
                   var retObj = new Array();
		if (typeof res != 'undefined') {
                  retObj = res;
		}
		for (var objInd in dupeObj) {
                        var flatifiedObjInd = nameContext + objInd;
			if (typeof(dupeObj[objInd]) == 'object') {
				flatten(dupeObj[objInd], retObj, flatifiedObjInd + '.');
			} else if (typeof(dupeObj[objInd]) == 'string') {
				retObj[flatifiedObjInd] = dupeObj[objInd];
			} else if (typeof(dupeObj[objInd]) == 'number') {
				retObj[flatifiedObjInd] = dupeObj[objInd];
			} else if (typeof(dupeObj[objInd]) == 'boolean') {
				((dupeObj[objInd] == true) ? retObj[flatifiedObjInd] = true : retObj[flatifiedObjInd] = false);
			}
		}
	}
	return retObj;
}


var csvOut = fs.createWriteStream(csvFilePath, { flags:'a', encoding:'Cp1252'});
//var csvOut = process.stdout; // for tests
//process.on('exit', function() {
//   console.log('exit');
//});
var csvWriter = csv.createCsvStreamWriter(csvOut, { "separator":csvSeparator });

// csv header line
var headerRecord = ['businessUseCase', 'method', 'res.statusCode'];
for (urlElement in urlElementsToExtract) {
  headerRecord.push(urlElement);
}
for (var i = 0; i < urlPathElementsToExtract.length; i++) {
  var currentUrlPathElementsToExtract = urlPathElementsToExtract[i];
  if (currentUrlPathElementsToExtract.length > 1) {
    var start = currentUrlPathElementsToExtract[0];
    if (start < 0) {
      start = 'n' + start;
    }
    var end = currentUrlPathElementsToExtract[currentUrlPathElementsToExtract.length - 1];
    if (end < 0) {
      end = 'n' + end;
    }
    headerRecord.push('path.' + start + '-' + end);
  } else if (currentUrlPathElementsToExtract.length > 0) {
    var single = currentUrlPathElementsToExtract[0];
    if (single < 0) {
      single = 'n' + single;
    }
    headerRecord.push('path.' + single);
  }
}
for (parameter in parametersToExtract) {
   headerRecord.push('param.' + parameter);
}
for (header in headersToExtract) {
  headerRecord.push('req.' + header);
}
for (header in responseHeadersToExtract) {
  headerRecord.push('res.' + header);
}
headerRecord.push('url');
headerRecord.push('parameters');
headerRecord.push('req.headers');
headerRecord.push('res.headers');
csvWriter.writeRecord(headerRecord);


// HTTP proxy / tunnel
function proxyRequest(req, res) {

var proxiedHost = req.headers['host'];
if (defaultProxiedHost != null) {
   proxiedHost = defaultProxiedHost;
}
var client = http.createClient(proxiedPort, proxiedHost);

var method = req.method;
var url = req.url;
var headers = req.headers;

var request = client.request(method, url, headers);
console.log('SENT HEADERS: ' + JSON.stringify(headers));
var reqBody = '';
req.on('data', function (chunk) {
  reqBody = reqBody + chunk;
  request.write(chunk);
});
req.on('end', function () {
  request.end();
  console.log('SENT BODY: ' + reqBody);
});

request.on('response', function (proxiedRes) {
  console.log('STATUS: ' + proxiedRes.statusCode);
  console.log('RECEIVED HEADERS: ' + JSON.stringify(proxiedRes.headers));

  var resHeaders = proxiedRes.headers;
  //resHeaders['content-type'] = 'text/html';
  res.writeHead(proxiedRes.statusCode, resHeaders);
  //proxiedRes.setEncoding('utf8'); // default is 'binary' ?

  proxiedRes.on('data', function (chunk) {
    res.write(chunk);
  });
  proxiedRes.on('end', function() {
    res.end();
    console.log('END');

    var record = [ businessUseCase, method, proxiedRes.statusCode ];

    var parsedUrl = require('url').parse(url, true);
    console.log('parsedUrl: ' + JSON.stringify(parsedUrl));
    var splitUrlPathNames = parsedUrl.pathname.split('/');
    //console.log('reqBody: ' + reqBody);
    var parsedReqBody;
    if (reqBody.indexOf('{') === 0) {
       parsedReqBody = eval('(' + reqBody + ')');
    //} else if (reqBody.indexOf('<') === 0) {
       // TODO XML
    } else {
      parsedReqBody = { "UNKNOWN":reqBody };
    }
    

    for (urlElement in urlElementsToExtract) {
      record.push(parsedUrl[urlElement]);
    }
    for (var i = 0; i < urlPathElementsToExtract.length; i++) {
      var currentUrlPathElementsToExtract = urlPathElementsToExtract[i];
      var currentUrlPathElements = '';
      var firstTime = true;
      for (var j = 0; j < currentUrlPathElementsToExtract.length; j++) {
        var urlPathElementsInd = currentUrlPathElementsToExtract[j];
        if (urlPathElementsInd < 0) {
          urlPathElementsInd += splitUrlPathNames.length;
        }
        if (urlPathElementsInd < splitUrlPathNames.length) {
          if (firstTime) {
            firstTime = !firstTime;
          } else {
            currentUrlPathElements += '/';
          }
          currentUrlPathElements += splitUrlPathNames[urlPathElementsInd];
        } else {
          break;
        }
      }
      record.push(currentUrlPathElements);
    }
    // merging req and body parameters
    //if (method == 'POST') {      
    //}
    var parameters = flatten(parsedReqBody);
    for (parameter in parsedUrl.query) {
      parameters[parameter] = parsedUrl.query[parameter];
    }
    for (parameter in parametersToExtract) {
      record.push(parameters[parameter]);
    }
    for (header in headersToExtract) {
      record.push(req.headers[header]);
    }
    for (header in responseHeadersToExtract) {
      record.push(proxiedRes.headers[header]);
    }
    record.push(url);
    record.push(JSON.stringify(parameters));
    record.push(JSON.stringify(headers));
    record.push(JSON.stringify(proxiedRes.headers));
    csvWriter.writeRecord(record);
  });
});

}


http.createServer(function (req, res) {
    proxyRequest(req, res);
}).listen(port, host);
console.log('proxyDump.js server running at http://' + host + ':' + port + '/.');
console.log('A new businessUseCase can be entered from command line followed byt ENTER.');

process.stdin.resume();
process.stdin.on('data', function(data) {
  var dataString = data.toString('utf8');
  var lastCharInd = data.length - 1;
  var lastChar;
  for (; lastCharInd >= 0; lastCharInd--) {
    lastChar = dataString[lastCharInd];
    if (lastChar == '\n' || lastChar == '\r') {
      continue;
    }
    
    businessUseCase = dataString.substring(0, lastCharInd + 1);
    console.log('');
    console.log('');
    console.log('USING businessUseCase: \'' + businessUseCase + '\'');
    console.log('');
    break;
  }
})
