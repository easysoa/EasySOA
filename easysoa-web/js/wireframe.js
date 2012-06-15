var fs = require('fs');
var stylus = require('stylus');
var url = require('url');

var settings = require('./settings');

var WIREFRAME_PATH = '/easysoa/wireframe';
var VIEWS_FOLDER = settings.WWW_PATH + WIREFRAME_PATH + '/views';

// Configure routing

exports.configure = function(webServer) {

  webServer.set('views', VIEWS_FOLDER);

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
      try {
        if (fs.lstatSync(VIEWS_FOLDER + '/' + page + '.jade').isFile()) {
          result.render(page + '.jade', {page: page, param: parsedUrl.query});
        }
      }
      catch(e) {
        result.render('404.jade');
      }
    }
    else {
      next();
    }
  });

}
