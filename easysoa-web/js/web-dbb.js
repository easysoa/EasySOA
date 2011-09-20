/* 
 * Provides functions to communicate with the discovery by browsing tool.
 *
 * Author: Marwane Kalam-Alami
 */
 
var fs = require('fs');
var http = require('http');
var socketio = require('socket.io');
var easysoaNuxeo = require('./web-nuxeo.js');

eval(fs.readFileSync('./settings.js', 'ASCII'));

var nuxeoReady = false;
var io = null;
var wsdlList = new Array();

exports.forceSocketIOHandling = function(request, response) {
    io.handleRequest(request, response);
}

exports.broadcastemit = function(type, data) {
    if (io != null) {
        io.sockets.emit(type, data);
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
  
      io = socketio.listen(server);
      io.set('log level', 2);
      io.set('transports', [
      //      'websocket' // Doesn't work through the proxy
            'flashsocket'
          , 'htmlfile'
          , 'xhr-polling'
          , 'jsonp-polling'
        ]);
        
      io.sockets.on('connection', function(socket) {
          
          // Notify that Nuxeo is ready
          if (nuxeoReady) {
            socket.emit('ready');
          }
            
          // Send stored WSDLs
          for (key in wsdlList) {
            socket.emit('wsdl', wsdlList[key]);
          }

       });
      
}
