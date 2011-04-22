var http = require('http');
var nuxeo = http.createClient(8080, 'localhost');
var request = nuxeo.request('GET', '/nuxeo/site/automation',
  {'host':'localhost', 'Authorization': 'Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y','Accept':'application/json'});
request.end();
request.on('response', function (response) {
  console.log('STATUS: ' + response.statusCode);
  console.log('HEADERS: ' + JSON.stringify(response.headers));
  response.setEncoding('utf8');
  response.on('data', function (chunk) {
    console.log('BODY: ' + chunk);
  });
});

