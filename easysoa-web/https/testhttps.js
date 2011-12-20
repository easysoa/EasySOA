var fs = require('fs'),
    http = require('http'),
    https = require('https'),
    url = require('url'),
    httpProxy = require('../js/node_modules/http-proxy');


var httpsCert = {
    key: fs.readFileSync('./server.key', 'utf8'),
    cert: fs.readFileSync('./server.crt', 'utf8')
};


// HTTPS server
https.createServer(httpsCert, function (req, res) {
  res.writeHead(200, { 'Content-Type': 'text/html' });
  res.write('<html><body><a href="https://localhost:8000">test https</a></body</html>');
	res.end();
}).listen(8000);


// HTTP server
http.createServer(function (req, res) {
  res.writeHead(200, { 'Content-Type': 'text/html' });
  res.write('<html><body><a href="https://localhost:8001">test http</a></body</html>');
	res.end();
}).listen(8001);


// HTTP proxy
httpProxy.createServer(function (req, res, proxy) {

  // Custom server logic
  var requestUrl = url.parse(req.url);
  var hostToProxy = requestUrl.hostname || 'localhost';
  var portToProxy = requestUrl.port || 80;
  console.log(hostToProxy + " " + portToProxy);
  
  // Proxy
  proxy.proxyRequest(req, res, {
    host: hostToProxy,
    port: portToProxy
  });
  
}).listen(8081);


// HTTPS proxy
httpProxy.createServer({
    https: httpsCert,
    target: { https: true }
  },
  function (req, res, proxy) {
    console.log(req.url);
    res.write('hello');
    res.end();
  }
).listen(8082);
