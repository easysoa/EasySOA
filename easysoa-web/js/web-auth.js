/* 
 * Provides functions to handle authentication.
 *
 * Author: Marwane Kalam-Alami
 */

var url = require('url');
var easysoaNuxeo = require('./web-nuxeo.js');

// INTERNAL FUNCTIONS

redirectTo = function(result, url) {
    result.redirect(url);
}

isLoginValid = function(username, password, callback) {
    easysoaNuxeo.checkNuxeo(username, password, function(data) {
        var res = data[0] == "{";
        console.log('easysoaNuxeo.checkNuxeo='+res);
        callback(data[0] == "{"); // If login succeeded, we get the automation doc
    });
}

isAnonymouslyAvailable = function(url) {
    return url.pathname.indexOf('easysoa/core') == -1 
        && url.pathname.indexOf('easysoa/light') == -1;
}

// EXPORTS
   
exports.authFilter = function (request, result, next) {
    reqUrl = request.urlp = url.parse(request.url, true);

    // Logout
    if (reqUrl.pathname == "/logout") {
      request.session.destroy();
      return;
    }

    // Handle already logged user
    if (request.session && request.session.auth == true) {
      console.log('Already logged: '+request.session.username);
      next();
      return;
    }

    // Authentication
    if (reqUrl.pathname.indexOf('login.html') != -1) {
        next();
        return;
    }
    else if (reqUrl.pathname == "/login") {
        isLoginValid(request.body.username, request.body.password, function (callbackResult) {
          // Logged successfully
          if (callbackResult) {
                request.session.auth = true;
                request.session.username = request.body.username;
                request.session.password = request.body.password;
              console.log('Login successful for ' + request.body.username);
              console.log("Redirecting to : "+request.body.prev);
              result.redirect((request.body.prev != '') ? request.body.prev : '/easysoa');
              return;
          // Unauthorized
          } else {
            result.redirect('/easysoa/login.html?error=true');
            return;
          }
       });
    }
    else if (isAnonymouslyAvailable(reqUrl)) {
        next();
    }
    else {
        result.redirect('/easysoa/login.html?prev='+reqUrl.href);
    }
}


exports.handleLogin = function(request, result) {
    if (request.body != undefined) {
        exports.isLoginValid(request.body.username, request.body.password, function(isValid) {
            if (isValid) {
                request.session.auth = true;////
                request.session.username = request.body.username;
                request.session.password = request.body.password;
                console.log("[INFO] Session created for: "+request.body.username);
                redirectTo(result, request.query.prev || '/easysoa');
            }
            else {
                exports.redirectToLogin(result, request.url, true);
            }
        });
    }
    else {
        exports.redirectToLogin(result, request.url, true);
    }
}

exports.redirectToLogin = function(result, prevUrl, error) {
    redirectTo(result, '/easysoa/login.html' + ((prevUrl) ? '?prev='+prevUrl : '') + ((error) ? '?error=true' : ''));
}

exports.isLoggedIn = function(request) {
    return request.session != undefined && request.session.user != undefined;
}

