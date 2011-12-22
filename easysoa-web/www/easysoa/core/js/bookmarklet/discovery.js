/**
 * Globals & constants
 */

var jQuery, underscore;
var templates = new Object();

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
		function() {
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
				callbackOnSuccess();
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
			runServiceFinder(linkUrl, appendWSDLs);
		}
	});
}

function runServiceFinder(theUrl, callback) {
	// Send whole HTML to Nuxeo
	console.log(theUrl);
	jQuery.ajax({
		url : EASYSOA_WEB + '/servicefinder/' + theUrl,
		dataType : 'jsonp',
		success : function(data) {
			callback(data);
		},
		error : function(msg) {
			console.log("ERROR"); // TODO
			console.log(msg); // TODO
		}
	});
}

function appendWSDLs(data) {
	console.log("Found WSDLs!");
	console.log(data);
	// TODO
}


/**
 * HTML templates
 */

function initTemplates() {
	
	templates['results'] = underscore.template(
	'<div class="easysoa easysoa-frame" id="easysoa-tpl-results">\
      Found WSDLs:\
    </div>');
	
	templates['login'] = underscore.template(
	'<div class="easysoa easysoa-frame" id="easysoa-tpl-login">\
      <div class="easysoa-form-label">User:</div><input type="text" id="easysoa-username" /><br />\
      <div class="easysoa-form-label">Password:</div><input type="password" id="easysoa-password" /><br />\
      <input type="submit" id="easysoa-submit" />\
	  <div id="easysoa-login-error"></div>\
    </div>');
}

function runTemplate(name, data) {
	jQuery('body').append(templates[name](data));
	jQuery('#easysoa-tpl-' + name).show('fast');

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