// EasySOA Web
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/* 
 * Provides functions needed for EasySOA Light
 *
 * Author: Marwane Kalam-Alami
 */
 
var http = require('http');
var fs = require('fs');
var url = require('url');
var easysoaNuxeo = require('./web-nuxeo.js');

eval(fs.readFileSync('./settings.js', 'ASCII'));

exports.fetchServiceList = function(session, callback) {

    try {
    
    	if (session.username != null) {
			var operation = "Document.Query";
			var params = {
				"query": "SELECT * FROM Service WHERE ecm:currentLifeCycleState <> 'deleted'"
			};
			var headers = {
				'X-NXDocumentProperties': 'dublincore, servicedef'
			};
			easysoaNuxeo.automationQuery(session, operation, params, headers, function(data) {
				if (data) {
					data = JSON.parse(data);
					result = new Array();
					for (var documentIndex in data.entries) {
						document = data.entries[documentIndex];
						entry = new Object();
						entry.title = document.properties['dc:title'];
						entry.url = document.properties['serv:url'];
						entry.lightUrl = '/scaffoldingProxy/?wsdlUrl='+document.properties['serv:fileUrl'];
						result.push(entry);
					}
					callback(result);
				}
				else {
					callback(false);
				}
			});
    	}
    	else {
    		callback(false);
    	}
    
    }
    catch (error) {
      console.error("[ERROR] Failed to fetch service list: "+error.stack);
    }
        
}
