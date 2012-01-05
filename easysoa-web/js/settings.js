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

exports.WWW_PATH = __dirname + '/../www';

exports.NO_AUTH_NEEDED = [
  '^[/]?$',
  '^/easysoa[/]?$',
  '^/easysoa/index.html',
  '^/easysoa/users.html',
  '^/easysoa/style.css',
  '^/easysoa/js/*',
  '^/easysoa/lib/*',
  '^/easysoa/img/*',
  '^/easysoa/core/img/*',
  '^/easysoa/core/js/bookmarklet/*',
  '^/intranet/*'
];

/* ==============
 * Proxy settings
 * ==============
 */

exports.PROXY_PORT = '8081';

exports.NUXEO_URL = NUXEO_URL               = 'http://127.0.0.1:8080/nuxeo';
exports.NUXEO_REST_URL               		= NUXEO_URL + '/site';
exports.EASYSOA_ROOT_URL = EASYSOA_ROOT_URL = NUXEO_URL + '/site/easysoa';
exports.EASYSOA_DISCOVERY_PATH               = 'easysoa/discovery/service';
exports.EASYSOA_SERVICE_FINDER_PATH          = 'easysoa/servicefinder';

exports.SERVICE_FINDER_IGNORE = [
  '.css',
  '.jpg',
  '.gif',
  '.png',
  '.js',
  '.ico',
  'localhost:7001', // FraSCAti (part of EasySOA Light)
  '127.0.0.1:7001',
  NUXEO_URL
];
