/*!
 * EasySOA Incubation - Node Tools - proxyDump
 * Copyright (c) 2010 Open Wide http://www.openwide.fr
 * MIT Licensed
 */

/**
 * proxyDump - tool for gathering data about HTTP exchanges, in order to help designing the easysoa model.
 * 
 *
 * How to use it :
 * * in proxy mode : start it on an unused port, configure your HTTP client application to use it as a proxy.
 * * in tunnel mode : configure it to use your HTTP server application as a server (tunneledHost, proxiedPort), start it on an unused port, configure your HTTP client application to use it as a server.
 * * use your HTTP client application (with a Nuxeo server, samples/nuxeo/nuxeoAutomationQuery[Fetch[Children]]_proxied.js can be used) and watch lines being added to proxy-log.csv .
 * * fine-tune what is extracted to columns, more about that below.
 *
 *
 * Each CSV line is as follows :
 * "businessUseCase";"method";"res.statusCode"[;url elements][;path elements][;request headers][;params][;response headers][;results];"url";"req.headers";"parameters";"res.headers";"results"
 * where the following parts can be configured here :
 *   businessUseCase : "user operation" tag / column, entered from command line.
 *   path elements : Each path column contains one or a range of path elements, addressed by index or -m for the n-m path element, where n+1 is the total count of path elements.
 *   params : Each parameter column contains a request parameter extracted from the query or from the json or xml response body, defined by a bit of json
 *   request headers : Each request header column contains a request header.
 *   result : Each result column contains a result parameter extracted from the json or xml response body, defined by a bit of json
 *   response headers : Each response header column contains a response header.
 *
 * See sample configurations below.
 * Choose on by setting the 'config' variable. Add yours by copy-pasting one in the 'init()' function.
 */

var configs = init();

//var config = configs.google;
//var config = configs.alfrescoShareBack;
//var config = configs.nuxeoContentAutomation;
var config = configs.javaEsbDotNet;



function init() {
return {


google : {

businessUseCase: 'Google query',

// HTTP configuration
http: {
  tunneledHost: null, // http proxy
  proxiedPort: '80', // web
  host: '127.0.0.1',
  port: '8081'
},

// CSV line configuration
extract: {
  urlElements: [ 'pathname' ],
  urlPathElements: [ ], // NB. 0 is /, 1 usually the application, -1 (latest) the service
  parameters: [ 'params.q' ], // for google
  results: [], // html
  headers: [ 'host' ],
  responseHeaders: [ 'content-type', 'date' ]
},

  // CSV configuration
csv: {
  filePath: 'proxy_log.csv',
  separator: ';'
}

},


alfrescoShareBack: {

businessUseCase: 'Alfresco share browsing',

// HTTP configuration
http: {
  tunneledHost: '192.168.2.196', // tunnel to remote alfresco back on mdutoo-laptop
  proxiedPort: '8080',
  host: '192.168.2.211', // remote alfresco on mdutoo-laptop, conf'd in share's webscript-framework-config.xml
  port: '8081'
},

// CSV line configuration
extract: {
  urlElements: [ 'pathname' ],
  urlPathElements: [ [1], [2], [3, -2], [-1] ], // NB. 0 is /, 1 usually the application, -1 (latest) the service
  parameters: [ 'shortName.match', 's', 'alf_ticket' ], // for alfresco
  results: [ 'node' ], // for alfresco
  headers: [ 'host' ],
  responseHeaders: [ 'content-type', 'date' ]
},

  // CSV configuration
csv: {
  filePath: 'proxy_log.csv',
  separator: ';'
}

},


nuxeoContentAutomation: {

businessUseCase: 'Nuxeo Content Automation node.js scripted scenario',

// HTTP configuration
http: {
  tunneledHost: null, // http proxy
  proxiedPort: '8080',
  host: '127.0.0.1',
  port: '8081'
},

// CSV line configuration
extract: {
  urlElements: [ 'pathname' ],
  urlPathElements: [ [1], [2, 3], [-1] ], // NB. 0 is /, 1 usually the application, -1 (latest) the service
  parameters: [ 'params.query', 'input', 'context' ], // for nuxeo
  results: [ 'node' ], // for nuxeo
  headers: [ 'host' ],
  responseHeaders: [ 'content-type', 'date' ]
},

  // CSV configuration
csv: {
  filePath: 'proxy_log.csv',
  separator: ';'
}

},


javaEsbDotNet: {

businessUseCase: 'ESB to .NET Client update',

// HTTP configuration
http: {
  tunneledHost: '127.0.0.1', proxiedPort: '7080', // http tunnel to soapui
  host: '127.0.0.1', port: '80' // .NET
},

// CSV line configuration
extract: {
  urlElements: [ 'pathname' ],
  urlPathElements: [ [1], [2] ], // NB. 0 is /, 1 usually the application, -1 (latest) the service
  parameters: [ '["soapenv:Body"]["Client"]["tns:Identifiant_Client"]' ], // ws // ["soapenv:Envelope"] is let outside
  results: [ '["soapenv:Body"]["fr:ClientResponse"]["tns:string"][0]' ], // for crm app // ["soapenv:Envelope"] is let outside
  headers: [ 'host', 'soapaction' ], // for ws
  responseHeaders: [ 'content-type', 'date' ]
},

  // CSV configuration
csv: {
  filePath: 'proxy_log.csv',
  separator: ';'
}

}

};


}