// HTTP proxy that dumps requests to CSV using https://github.com/koles/ya-csv
//
// written starting from proxyFuse.js
// see also http://www.catonmat.net/http-proxy-in-nodejs
// existing node.js alternative (providing reverse proxy...) : https://github.com/nodejitsu/node-http-proxy
// existing alternative software : TODO
//
// possible improvements :
// more flexible column ordering
// allow to regroup several pathnames in a single column
// filters : ex. filteredContentTypes (image/jpeg...)
// "user operation" tag / column, entered from user input (command line, or even web ??), to mark distinct user operations
//
// & always : packaging (module), improve command line options, share code with other proxy*.js

var http = require('http');
var fs = require('fs');
var csv = require('ya-csv');


//var proxiedPort = port; 
var defaultProxiedHost = null; // true http proxy
//var defaultProxiedHost = '192.168.2.196'; // forced http (reverse) proxy
var proxiedPort = '8080'; // mostly for tests else port
//var proxiedPort = '80'; // mostly for tests else port
var host = 'localhost';
//var host = '192.168.2.211'; // if remote
var port = '8081';

var urlElementsToExtract = { 'pathname':null };
var urlPathElementsToExtract = [ [1], [2, 3], [-1] ]; // NB. 0 is /, 1 usually the application, -1 (latest) the service
//var parametersToExtract = {};
//var parametersToExtract = { 'q':null }; // for google
var parametersToExtract = { 'params.query':null, 'input':null, 'context':null }; // for nuxeo
var headersToExtract = { 'host':null };
var responseHeadersToExtract = { 'content-type':null, 'date':null };

var csvSeparator = ';';


// init
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

function flatify (dupeObj, res, nameContext) {
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
				flatify(dupeObj[objInd], retObj, flatifiedObjInd + '.');
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


var csvOut = fs.createWriteStream('proxy_log.csv', { flags:'a', encoding:'Cp1252'});
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


function proxyRequest(req, res) {
var proxiedHost = req.headers['host'];
if (defaultProxiedHost != null) {
   proxiedHost = defaultProxiedHost;
}
var client = http.createClient(proxiedPort, proxiedHost);

var method = req.method;
var url = req.url;
var headers = req.headers;
//var headers = {};
//for (h in req.headers) {
//   headers[h] = req.headers[h];
//}
//headers.host = proxiedHost + ':' + proxiedPort;
//headers.Authorization = 'Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y';
//headers.cookie = null;
//headers.COOKIE_SUPPORT = false;
//headers.LOGIN = null;

var request = client.request(method, url, headers);
console.log('SENT HEADERS: ' + JSON.stringify(headers));
//  {'host':proxiedHost + ':' + proxiedPort,
//   'Authorization': 'Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y',
//   'Accept': 'application/json+nxentity, */*',
//   'Content-Type': 'application/json+nxrequest; charset=UTF-8',
//   'X-NXVoidOperation': true,
//   'X-NXDocumentProperties': '*',
//   'Content-Length':body.length});
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
    var parameters = flatify(parsedReqBody);
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
    record.push(JSON.stringify(parameters)); //.replace(csvSeparator,'!'));
    record.push(JSON.stringify(headers)); //.replace(csvSeparator,'!'));
    record.push(JSON.stringify(proxiedRes.headers)); //.replace(csvSeparator,'!'));
    csvWriter.writeRecord(record);
  });
});

}


http.createServer(function (req, res) {
    proxyRequest(req, res);
}).listen(port, host);
console.log('Server running at http://' + host + ':' + port + '/');

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
