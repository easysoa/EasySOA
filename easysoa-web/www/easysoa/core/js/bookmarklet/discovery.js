/**
 * Globals & constants
 */

var jQuery, underscore;
var templates = new Object();

LIBS_POLLING_INTERVAL = 20;
EASYSOA_WEB = 'http://localhost:8083';

/**
 * Init libs
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
 * HTML templates
 */

function initTemplates() {
	templates['results'] = underscore
			.template('<div class="easysoa easysoa-frame" id="easysoa-tpl-results">\
      Hello <%= name %>\
    </div>');
	templates['login'] = underscore
			.template('<div class="easysoa easysoa-frame" id="easysoa-tpl-login">\
      <div class="easysoa-form-label">User:</div><input type="text" id="easysoa-username" /><br />\
      <div class="easysoa-form-label">Password:</div><input type="password" id="easysoa-password" /><br />\
      <input type="submit" id="easysoa-submit" />\
	  <div id="easysoa-login-error"></div>\
    </div>');
}

function loadTemplate(name, data) {
	jQuery('body').append(templates[name](data));
	jQuery('#easysoa-tpl-' + name).show('fast');

	// Login template handlers
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
						console.log('cool');
					}
					else {
						var errorTag = jQuery('#easysoa-login-error');
						errorTag.html('ERROR: ' + data.error);
						errorTag.show('fast');
					}
				},
				error : function() {
					var errorTag = jQuery('#easysoa-login-error');
					errorTag.html('ERROR: Failed to run login request');
					errorTag.show('fast');
				}
			});
		});
	}
}

/**
 * Functions
 */

function checkIsLoggedIn(callbackOnSuccess, callbackOnError) {
	jQuery.ajax({
		url : EASYSOA_WEB + '/userdata',
		success : function(data, msg, xhr) {
			callbackOnSuccess();
		},
		error : function() {
			callbackOnError();
		}
	});
}

function showLoginForm() {
	loadTemplate('login');
}

function findWSDLs(url, callback) {
	// Send whole HTML to Nuxeo
	jQuery.ajax({
		type : 'POST',
		url : EASYSOA_WEB + '/servicefinder',
		data : {
			url : url,
			data : jQuery('html').html()
		},
		success : function(data, msg, xhr) {
			console.log(xhr.responseText);
			callback({
				name : "world" // TODO
			});
		},
		error : function(msg) {
			console.log(msg); // TODO
		}
	});
}

function showResults(data) { // TODO
	loadTemplate('results', data);
}

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
			findWSDLs(window.location.href, showResults);
		},
		// Else display login form
		function() {
			showLoginForm();
		});
	}
}

setTimeout(load, LIBS_POLLING_INTERVAL);
