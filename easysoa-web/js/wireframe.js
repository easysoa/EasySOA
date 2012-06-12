var fs = require('fs');
var stylus = require('stylus');
var url = require('url');

var settings = require('./settings');

var WIREFRAME_PATH = '/easysoa/wireframe';

// Configure routing

exports.configure = function(webServer) {

  webServer.set('views', settings.WWW_PATH + WIREFRAME_PATH + '/views');

  webServer.get(WIREFRAME_PATH + '/css/style.css', function(request, result) {
    var cssStr = fs.readFileSync(settings.WWW_PATH + WIREFRAME_PATH + '/css/style.styl', 'utf8');
    stylus.render(cssStr, {}, function(err, css) {
      result.end(css);
    });
  });
  webServer.get(WIREFRAME_PATH + '/*', function(request, result, next) {
    var parsedUrl = url.parse(request.url);
    var page = parsedUrl.pathname.replace(WIREFRAME_PATH, '').substr(1) || 'index';
    if (page.indexOf('/') == -1) {
      result.render(page + '.jade', {param: parsedUrl.query});
    }
    else {
      next();
    }
  });

}
