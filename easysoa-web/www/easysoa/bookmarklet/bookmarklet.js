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

loadJS('http://localhost:8083/easysoa/lib/jquery.js');
loadJS('http://localhost:8083/easysoa/lib/underscore.js');
loadCSS('http://localhost:8083/easysoa/bookmarklet/bookmarklet.css');

/**
 * Globals
 */
 
var jQuery, underscore;
var templates = new Object();

LIBS_POLLING_INTERVAL = 20;

/**
 * HTML templates
 */

function initTemplates() {
  templates.box = underscore.template(' \
   <div class="easysoa easysoa-box">\
   Hello <%= name %>\
   </div>\
  ');
}

/**
 * Functions
 */

function findWSDLs(url, callback) {
  callback({
    name: "world"
  });
}

function showResults(data) {  
  jQuery('body').append(templates.box(data));
}

function load() {
  if (window._ && window.jQuery) {  
    underscore = window._.noConflict();
    jQuery = window.jQuery.noConflict();
    initTemplates();
    start();
  }
  else {
    setTimeout(load, LIBS_POLLING_INTERVAL);
  }
}

function start() {
    findWSDLs(window.location.href, showResults);
}

setTimeout(load, LIBS_POLLING_INTERVAL);
