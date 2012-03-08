var http = require('http');
var nuxeo = http.createClient(8080, 'localhost');
//var body = '{"params":{"query":"select * from Document where dc:title=\'pivotal\'"}}';
var body = '{"params":{"query":"select * from Document"}}';
var request = nuxeo.request('POST', '/nuxeo/site/automation/Document.Query',
  {'host':'localhost', 'Authorization': 'Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y',
   'Accept': 'application/json+nxentity, */*',
   'Content-Type': 'application/json+nxrequest; charset=UTF-8',
   'X-NXDocumentProperties': '*',
   'Content-Length':body.length});
request.write(body);
request.end();
request.on('response', function (response) {
  console.log('STATUS: ' + response.statusCode);
  console.log('HEADERS: ' + JSON.stringify(response.headers));
  response.setEncoding('utf8');
  var res = '';
  response.on('data', function (chunk) {
    res += chunk;
  });
  response.on('end', function() {
    console.log('BODY: ' + res);
    var a = eval('(' + res + ')');
    console.log(a.entries[6]);
  });
});

