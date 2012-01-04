// EasySOA Web
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/* ===================
 * Web server settings
 * ===================
 */

exports.WEB_PORT = '8083';

exports.WWW_PATH = __dirname + '/www';

exports.NO_AUTH_NEEDED = [
  '^core/img/*',
  '^core/js/bookmarklet/*',
  '^img/*',
  '^index.html$'
];

/* ==============
 * Proxy settings
 * ==============
 */

exports.PROXY_PORT = '8081';

exports.NUXEO_URL = NUXEO_URL               = 'http://127.0.0.1:8080/nuxeo';
exports.NUXEO_AUTOMATION_URL                = NUXEO_URL + '/site/automation';
exports.EASYSOA_ROOT_URL = EASYSOA_ROOT_URL = NUXEO_URL + '/site/easysoa';
exports.EASYSOA_DISCOVERY_URL               = EASYSOA_ROOT_URL + '/discovery';
exports.EASYSOA_SERVICE_FINDER_URL          = EASYSOA_ROOT_URL + '/servicefinder';

exports.SERVICE_FINDER_IGNORE = [
  '*.css',
  '*.jpg',
  '*.gif',
  '*.png',
  '*.js',
  '*.ico',
  '*:7001', // FraSCAti (part of EasySOA Light)
  NUXEO_URL
];
