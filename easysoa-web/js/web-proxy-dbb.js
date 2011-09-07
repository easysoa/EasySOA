/* 
 * Provides functions to communicate with the discovery by browsing tool.
 *
 * Author: Marwane Kalam-Alami
 */
 
var base64 = require('base64');
var fs = require('fs');
var http = require('http');
var io = require('socket.io');
var easysoaNuxeo = require('./web-proxy-nuxeo.js');

eval(fs.readFileSync('./settings.js', 'ASCII'));

var nuxeoReady = false;
var io_server = null;
var wsdlList = new Array();

exports.forceSocketIOHandling = function(request, response) {
    io_server.handleRequest(request, response);
}

exports.broadcastemit = function(type, data) {
    if (io_server != null) {
        io_server.sockets.emit(type, data);
    }
}
    
exports.setNuxeoReady = function() {
    nuxeoReady = true;
    exports.broadcastemit('ready');
}

exports.provideWsdl = function(linkName, link) {
    wsdlList[linkName] = JSON.stringify(link);
    exports.broadcastemit('wsdl', wsdlList[linkName]);
}

exports.startDiscoveryByBrowsingHandler = function(server) {
  
      io_server = io.listen(server);
      io_server.set('log level', 2);
      io_server.set('transports', [
      //      'websocket' // Doesn't work through the proxy
            'flashsocket'
          , 'htmlfile'
          , 'xhr-polling'
          , 'jsonp-polling'
        ]);
      io_server.sockets.on('connection', function(client){
          
          // Notify that Nuxeo is ready
          if (nuxeoReady) {
            client.emit('ready');
          }
            
          // Send stored WSDLs
          for (key in wsdlList) {
            client.emit('wsdl', wsdlList[key]);
          }

          /**** Send notifications to Nuxeo on client request ****/

          /**
           * Message from the client should have the following JSON format:
           * {
           *  'url': 'The URL',
           *  'applicationname': 'The Application Name',
           *  'servicename': 'The Service Name'
           * }
           */
          client.on('message', function(data) {
            easysoaNuxeo.registerWsdl(JSON.parse(data), function(response_data) {
                try {
                  json = JSON.parse(response_data);
                }
                catch (error) {
                  error = 'ERROR: Nuxeo response cannot be parsed into JSON ('+error+')';
                  json = {'result': error};
                  console.log("[WARN] "+error);
                }
                client.emit('upload', json.result);
            });
            
          });
      
      });
    
};
