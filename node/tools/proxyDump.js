/*!
 * EasySOA Incubation - Node Tools - proxyDump
 * Copyright (c) 2010 Open Wide http://www.openwide.fr
 * MIT Licensed
 */


/**
 * proxyDump - tool for gathering data about HTTP exchanges, in order to help designing the easysoa model.
 *
 * See how to use & configure it in proxyDump-config.js
 *
 * Author : Marc Dutoo
 *
 * 
 * Listens to HTTP exchanges in proxy or tunnel mode. For each request / response,
 * parses a few fields (http, json or xml thanks to xml2js) and write them to file
 * as a CSV line using https://github.com/koles/ya-csv .
 *
 * Written starting from proxyFuse.js .
 * see also http://www.catonmat.net/http-proxy-in-nodejs
 * existing node.js alternative (providing reverse proxy...) : https://github.com/nodejitsu/node-http-proxy
 * existing alternative software : soapui, wireshark
 *
 * possible improvements :
 * more flexible column ordering
 * filters : ex. filteredContentTypes (image/jpeg...)
 * packaging (module ?), command line options, share code with other proxy*.js
 */


var http = require('http');
var fs = require('fs');
var xml2js = require('xml2js');
var csv = require('ya-csv');
var sys = require('sys'); // for logging

eval(fs.readFileSync('proxyDump-config.js', 'ASCII'));


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

// NOT USED (also allows for jsonpath like lookup of elements)
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
				flatten(dupeObj[objInd], retObj, flatifiedObjInd + '.'); // TODO better to handle "bad" names ex. containing ':'
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

// for test only
function parseXml2jsTest(xml) {
  var fs = require('fs');
  var xml2js = require('xml2js');
  var sys = require('sys');

  var parser = new xml2js.Parser();
  var parsedXml = null;
  parser.addListener('end', function(result) {
    console.log(sys.inspect(result));
    parsedXml = eval('result' + '["soapenv:Body"]["Client"]["tns:Identifiant_Client"]');
  });
  fs.readFile('xml.xml', function(err, data) {
    parser.parseString(data);
    console.log('Done. ' + parsedXml); // null if not xml
  });
}

function parseXml2js(xml) {
  var parser = new xml2js.Parser();
  var parsedXml = null;
  parser.addListener('end', function(result) {
    parsedXml = result;
  });
  parser.parseString(xml);
  return parsedXml;
}

function getParamFromJson(json, param) {
  var prefixedParam = (parameter.charAt(0) != '[' &&  parameter.charAt(0) != '.') ? '.' + parameter : parameter;
  try {
	 return eval('json' + prefixedParam);
  } catch (e) {
	 console.log('Parameter : no ' + param + ' found in\n' + sys.inspect(json));
	 return '';
  }
}


var businessUseCase = config.businessUseCase;

var csvOut = fs.createWriteStream(config.csv.filePath, { flags: 'a', encoding: 'Cp1252'});
var csvWriter = csv.createCsvStreamWriter(csvOut, { "separator": config.csv.separator });

// csv header line
var headerRecord = ['businessUseCase', 'method', 'res.statusCode'];
for (var i in config.extract.urlElements) {
  var urlElement = config.extract.urlElements[i];
  headerRecord.push(urlElement);
}
for (var i in config.extract.urlPathElements) {
  var currentUrlPathElements = config.extract.urlPathElements[i];
  if (currentUrlPathElements.length > 1) {
    var start = currentUrlPathElements[0];
    if (start < 0) {
      start = 'n' + start;
    }
    var end = currentUrlPathElements[currentUrlPathElements.length - 1];
    if (end < 0) {
      end = 'n' + end;
    }
    headerRecord.push('path.' + start + '-' + end);
  } else if (currentUrlPathElements.length > 0) {
    var single = currentUrlPathElements[0];
    if (single < 0) {
      single = 'n' + single;
    }
    headerRecord.push('path.' + single);
  }
}
for (var i in config.extract.headers) {
  var header = config.extract.headers[i];
  headerRecord.push('req.' + header);
}
for (var i in config.extract.parameters) {
  var parameter = config.extract.parameters[i];
  headerRecord.push('param.' + parameter);
}
for (var i in config.extract.responseHeaders) {
  var header = config.extract.responseHeaders[i];
  headerRecord.push('res.' + header);
}
for (var i in config.extract.results) {
  var result = config.extract.results[i];
  headerRecord.push('result.' + result);
}

// adding raw data columns
headerRecord.push('url');
headerRecord.push('req.headers');
headerRecord.push('parameters');
headerRecord.push('res.headers');
headerRecord.push('results');
csvWriter.writeRecord(headerRecord);


// HTTP proxy / tunnel
function proxyRequest(req, res) {

var proxiedHost = req.headers['host'];
if (config.http.tunneledHost != null) {
   proxiedHost = config.http.tunneledHost;
}
var client = http.createClient(config.http.proxiedPort, proxiedHost);

var method = req.method;
var url = req.url;
var headers = req.headers;

var request = client.request(method, url, headers);
console.log('SENT HEADERS: ' + JSON.stringify(headers));
var reqBody = '';
req.on('data', function (chunk) {
  reqBody += chunk;
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

  var resBody = '';
  proxiedRes.on('data', function (chunk) {
    resBody += chunk;
    res.write(chunk, 'binary');
  });
  proxiedRes.on('end', function() {
    res.end();
    console.log('END');

    // preparing url
    var parsedUrl = require('url').parse(url, true);
    console.log('parsedUrl: ' + JSON.stringify(parsedUrl));
    var splitUrlPathNames = parsedUrl.pathname.split('/');
    //console.log('reqBody: ' + reqBody);
    
    // preparing req body
    var parsedReqBody;
    if (resBody.indexOf('</') > -1 || resBody.indexOf('/>') > -1) {
       // xml
       parsedReqBody = parseXml2js(reqBody);
       if (parsedReqBody == null) {
         parsedReqBody = { "XML":reqBody }; // failed to parse
       }
    } else if (reqBody.indexOf('{') === 0) {
       // json
       try {
         parsedReqBody = eval('(' + reqBody + ')');
       } catch (e) {
         parsedResBody = { "JSON":resBody }; // failed to parse
       }
    } else {
      parsedReqBody = { "UNKNOWN":reqBody };
    }

    // preparing res body
    var parsedResBody;
    if (resBody.indexOf('</') > -1 || resBody.indexOf('/>') > -1) {
       // xml
       parsedResBody = parseXml2js(resBody);
       if (parsedReqBody == null) {
         parsedResBody = { "XML":resBody }; // failed to parse
       }
    } else if (resBody.indexOf('{') === 0) {
       // json
       try {
         parsedResBody = eval('(' + resBody + ')');
       } catch (e) {
         parsedResBody = { "JSON":resBody }; // failed to parse
       }
    } else {
      parsedResBody = { "UNKNOWN":resBody };
    }
    
    // filling columns
    var record = [ businessUseCase, method, proxiedRes.statusCode ];
    for (var i in config.extract.urlElements) {
      var urlElement = config.extract.urlElements[i];
      record.push(parsedUrl[urlElement]);
    }
    for (var i in config.extract.urlPathElements) {
      var currentUrlPathElements = config.extract.urlPathElements[i];
      var currentUrlPath = '';
      var firstTime = true;
      for (var j in currentUrlPathElements) {
        var urlPathElementsInd = currentUrlPathElements[j];
        if (urlPathElementsInd < 0) {
          urlPathElementsInd += splitUrlPathNames.length;
        }
        if (urlPathElementsInd < splitUrlPathNames.length) {
          if (firstTime) {
            firstTime = !firstTime;
          } else {
            currentUrlPath += '/';
          }
          currentUrlPath += splitUrlPathNames[urlPathElementsInd];
        } else {
          break;
        }
      }
      record.push(currentUrlPath);
    }
    for (var i in config.extract.headers) {
      var header = config.extract.headers[i];
      record.push(req.headers[header]);
    }
    // merging req and body parameters
    //if (method == 'POST') {      
    //}
    for (var i in config.extract.parameters) {
      var parameter = config.extract.parameters[i];
      var value = parsedUrl.query[parameter];
      if (typeof(value) != 'undefined') {
        record.push(value);
      } else {
        record.push(getParamFromJson(parsedReqBody, parameter)); // ex. '["soapenv:Body"]["Client"]["tns:Identifiant_Client"]'
      }
    }
    for (var i in config.extract.responseHeaders) {
      var header = config.extract.responseHeaders[i];
      record.push(proxiedRes.headers[header]);
    }
    for (var i in config.extract.results) {
      var result = config.extract.results[i];
	   record.push(getParamFromJson('parsedResBody', result)); // ex. '["soapenv:Body"]["fr:ClientResponse"]["tns:string"][0]'
    }

    // adding raw data columns
    record.push(url);
    record.push(JSON.stringify(headers));
    record.push(JSON.stringify(parameters));
    record.push(JSON.stringify(proxiedRes.headers));
    record.push(JSON.stringify(results));
    csvWriter.writeRecord(record);
  });
});

}


http.createServer(function (req, res) {
    proxyRequest(req, res);
}).listen(config.http.port, config.http.host);
console.log('proxyDump.js server running at http://' + config.http.host + ':' + config.http.port + '/.');
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
