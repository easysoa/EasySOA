var fs = require('fs');
var stylus = require('stylus');

var settings = require('./settings');

var WIREFRAME_PATH = '/easysoa/wireframe';

// Configure routing

exports.configure = function(webServer) {

  webServer.set('views', settings.WWW_PATH + WIREFRAME_PATH + '/views');

  webServer.get(WIREFRAME_PATH + '/style.css', function(request, result) {
    var cssStr = fs.readFileSync(settings.WWW_PATH + WIREFRAME_PATH + '/style.styl', 'utf8');
    stylus.render(cssStr, {}, function(err, css) {
      result.end(css);
    });
  });
  webServer.get(WIREFRAME_PATH + '/*', function(request, result, next) {
    var page = request.url.replace(WIREFRAME_PATH, '').substr(1) || 'index';
    if (page.indexOf('/') == -1 && page.indexOf('.') == -1) {
      result.render(page + '.jade');
    }
    else {
      next();
    }
  });

}
