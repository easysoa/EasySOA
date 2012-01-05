/**
 * Globals & constants
 */

var jQuery, underscore;
var templates = new Object();
var wsdls = new Array();
var username = null;
var frameDragged = false;

LIBS_POLLING_INTERVAL = 20;
EASYSOA_WEB = 'http://localhost:8083';

/**
 * Load libs
 */

function loadJS(url) {
	var scriptTag = document.createElement('script');
	scriptTag.type = 'text/javascript';
	scriptTag.src = url;
	document.getElementsByTagName('head')[0].appendChild(scriptTag);
}

function loadCSS(url) {
	var linkTag = document.createElement('link');
	linkTag.rel = 'stylesheet';
	linkTag.type = 'text/css';
	linkTag.href = url;
	document.getElementsByTagName('head')[0].appendChild(linkTag);
}

loadJS(EASYSOA_WEB + '/easysoa/lib/jquery.js');
loadJS(EASYSOA_WEB + '/easysoa/lib/underscore.js');
loadCSS(EASYSOA_WEB + '/easysoa/core/js/bookmarklet/bookmarklet.css');

/**
 * Core functions
 */

function load() {
	if (window._ && window.jQuery) {
		underscore = window._.noConflict();
		jQuery = window.jQuery.noConflict();
		initTemplates();
		start();
	} else {
		setTimeout(load, LIBS_POLLING_INTERVAL);
	}
}

function start() {
	// Remove any previous result
	var previousResults = jQuery('.easysoa-frame');
	if (previousResults.size() != 0) {
		previousResults.hide('fast', function() {
			previousResults.remove();
			start();
		});
	} else {
		checkIsLoggedIn(
		// If logged in, start WSDL search
		function(aUsername) {
			username = aUsername;
			findWSDLs();
		},
		// Else display login form
		function() {
			runTemplate('login');
		});
	}
}

function checkIsLoggedIn(callbackOnSuccess, callbackOnError) {
	jQuery.ajax({
		url : EASYSOA_WEB + '/userdata',
		dataType : 'jsonp',
		success : function(data) {
			if (data && data.username)  {
				callbackOnSuccess(data.username);
			}
			else {
				callbackOnError();
			}
		},
		error : function() {
			callbackOnError();
		}
	});
}

function findWSDLs() {
	runTemplate('results');
	runServiceFinder(window.location.href, appendWSDLs);
	var links = jQuery('a');
	links.each(function (key) {
		// Scraping to filter WSDLs and send them to Nuxeo,
		// in case it couldn't get access to the page
		var linkUrl = jQuery(this).attr('href');
		if (linkUrl && linkUrl.substr(-4) == 'wsdl') {
			runServiceFinder(linkUrl);
		}
	});
}

function runServiceFinder(theUrl) {
	// Send request to Nuxeo
	jQuery.ajax({
		url : EASYSOA_WEB + '/nuxeo/servicefinder/' + theUrl,
		dataType : 'jsonp',
		success : function(data) {
			if (data.foundLinks) {
				appendWSDLs(data);
			}
			else {
				for (errorKey in data.errors) {
					console.log("EasySOA ERROR: ", data.errors[errorKey]);
				}
			}
		},
		error : function(xhr) {
			console.log("EasySOA ERROR: ", xhr.responseText);
		}
	});
}

function appendWSDLs(data) {
	var resultsDiv = jQuery('#easysoa-tpl-results');
	var wsdlEntryTpl = templates['wsdl'];
	for (key in data.foundLinks) {
		var newWSDL = {
			id: wsdls.length,
			applicationName: data.applicationName,
			serviceName: key,
			serviceURL: data.foundLinks[key]
		};
		wsdls.push(newWSDL);
		resultsDiv.append(wsdlEntryTpl(newWSDL));
	}
}

function sendWSDL(domElement) {
	var $domElement = jQuery(domElement);
	var wsdlToSend = wsdls[$domElement.attr('id')];
	$domElement.animate({opacity:0.5}, 'fast', function() {
		jQuery.ajax({
			url : EASYSOA_WEB + '/nuxeo/discovery/service/jsonp',
			dataType : 'jsonp',
			data : {
				'title': wsdlToSend.serviceName,
				'url': wsdlToSend.serviceURL,
				'discoveryTypeBrowsing': 'Discovered by ' + username + ' (via bookmarklet)'
			},
			success : function(data) {
				if (data.result == "ok") {
					jQuery(domElement).css({ 'background-color': '#CD5', 'opacity' : 1 });
				}
				else {
					console.warn("EasySOA ERROR: ", data.result);
					jQuery(domElement).css({ 'background-color': '#C77', 'opacity' : 1 });
				}
			},
			error : function(msg) {
				console.warn("EasySOA ERROR: Request failure - ", msg);
			}
		});
	});
}

function exit() {
	jQuery('.easysoa-frame').hide('fast');
}

/**
 * HTML templates
 */

function initTemplates() {
	
	templates['results'] = underscore.template(
	'<div class="easysoa-frame" id="easysoa-tpl-results">\
	  <div class="easysoa-exit" onclick="exit()"></div>\
	  <div class="easysoa-title">Found WSDLs:</div>\
	  <span class="easysoa-doc">(click on those you want to submit)</span>\
    </div>');
	
	templates['login'] = underscore.template(
	'<div class="easysoa-frame" id="easysoa-tpl-login">\
	  <div class="easysoa-title">Login:</div>\
	  <div class="easysoa-exit" onclick="exit()"></div>\
	  <div class="easysoa-form-label">User </div><input type="text" id="easysoa-username" value="Administrator" /><br />\
      <div class="easysoa-form-label">Password </div><input type="password" id="easysoa-password" value="Administrator" /><br />\
      <input type="submit" id="easysoa-submit" />\
	  <div id="easysoa-login-error"></div>\
    </div>');
	
	templates['wsdl'] = underscore.template(
	'<div class="easysoa-wsdl-result" onclick="sendWSDL(this)" id="<%= id %>">\
      <span class="easysoa-wsdl-name"><%= serviceName %></span><br />\
	  <a href="<%= serviceURL %>" class="easysoa-wsdl-link"><%= serviceURL %></a>\
    </div>');
}

function runTemplate(name, data) {
	jQuery('body').append(templates[name](data));
	jQuery('#easysoa-tpl-' + name).show('fast');

	// Make frames draggables
	if (name == 'login' || name == 'results') {
		jQuery('.easysoa-title').mousedown(function() {
			frameDragged = true;
		});
		jQuery(window).mousemove(function(e) {
			if (frameDragged) {
				var frame = jQuery('.easysoa-frame');
				frame.css({
					left: e.clientX - (frame.width() / 2),
					top: e.clientY - 20
				});
			}
		});
		jQuery(window).mouseup(function() {
			frameDragged = false;
		});
	}
	
	// Login template handler
	if (name == 'login') {
		jQuery('#easysoa-submit').click(function() {
			jQuery.ajax({
				url : EASYSOA_WEB + '/login',
				data : {
					username : jQuery('#easysoa-username').attr('value'),
					password : jQuery('#easysoa-password').attr('value')
				},
				dataType : 'jsonp',
				success : function(data) {
					if (data.result == "ok") {
						start();
					}
					else {
						var errorTag = jQuery('#easysoa-login-error');
						errorTag.hide('fast');
						errorTag.html('ERROR: ' + data.error);
						errorTag.show('fast');
					}
				},
				error : function() {
					var errorTag = jQuery('#easysoa-login-error');
					errorTag.hide('fast');
					errorTag.html('ERROR: Failed to run login request');
					errorTag.show('fast');
				}
			});
		});
	}
}

/**
 * Bootstrap
 */

setTimeout(load, LIBS_POLLING_INTERVAL);