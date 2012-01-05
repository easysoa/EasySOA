// EasySOA Web
// Copyright (c) 2011 -2012Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

var nuxeo = require('./nuxeo');

/**
 * EasySOA light web services
 *
 * Author: Marwane Kalam-Alami
 */

//================ I/O =================

exports.configure = function(webServer) {
	// Router configuration
	webServer.get('/light/serviceList', serviceList);
};

//============= Controller =============

serviceList = function(request, response, next) {
	fetchServiceList(request.session, function(data) {
		var responseData = new Object();
		responseData.success = (data !== false);
		responseData.data = data;
		response.write(JSON.stringify(responseData));
		response.end();
	});
};

fetchServiceList = function(session, callback) { 
    try {
    	nuxeo.runAutomationDocumentQuery(session, 
			"SELECT * FROM Service WHERE ecm:currentLifeCycleState <> 'deleted'",
			'dublincore, servicedef',
			function(data) {
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
    catch (error) {
      console.error("[ERROR] Failed to fetch service list: "+error.stack);
    }
};