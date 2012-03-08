var http = require('http');
var nuxeo = http.createClient(8080, 'localhost');

function nuxeoRequest(operation, params, input) {
var body;
if (input) {
   body = JSON.stringify({params:params, input:input});
} else {
   body = JSON.stringify({params:params});
}
console.log('nuxeoRequest ' + operation + ' ' + body);
var request = nuxeo.request('POST', '/nuxeo/site/automation/' + operation,
  {'host':'localhost', 'Authorization': 'Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y',
   'Accept': 'application/json+nxentity, */*',
   'Content-Type': 'application/json+nxrequest; charset=UTF-8',
//   'X-NXVoidOperation': true,
   'X-NXDocumentProperties': '*',
   'Content-Length':body.length});
request.write(body);
request.end();
return request;
}

function getBlob(docRef) {
//var request = nuxeoRequest('Blob.Get', {}, 'doc:' + docRef);
var request = nuxeoRequest('Blob.GetList', {}, 'doc:' + docRef);
//var request = nuxeoRequest('Blob.GetList', {xpath:xpath}, 'doc:' + docRef);
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
  });
});
}

function fetchDocument(path) {
var params = {value:path};
var request = nuxeoRequest('Document.Fetch', params);
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
    var doc = eval('(' + res + ')');
    getBlob(doc.path);
  });
});
}

//var params = {query:'select * from File'};
var params = {query:'select * from Document'};
//http://localhost:8080/nuxeo/site/admin/repository/default-domain/workspaces/@search?query=SELECT * FROM File where filename='infoApv_vm_tiers_client.xml' and mime-type='application/octet-stream'
var request = nuxeoRequest('Document.Query', params);
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
    var docs = eval('(' + res + ')');
    //var doc = docs.entries[5];
    var doc = docs.entries[6];
    console.log(doc);
    fetchDocument(doc.path);
  });
});

