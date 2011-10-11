// EasySOA Web
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@groups.google.com

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
    if (easysoaNuxeo.isNuxeoReady()) {
        easysoaNuxeo.checkNuxeo(username, password, function(data) {
            callback(data[0] == "{"); // If login succeeded, we get the automation doc
        });
    }
    else {
        throw "Nuxeo is not ready";
    }
}

isAnonymouslyAvailable = function(url) {
    return url.pathname.indexOf('easysoa/core') == -1 
        && url.pathname.indexOf('easysoa/light') == -1
        && url.pathname.indexOf('scaffoldingProxy') == -1;
}

// EXPORTS
   
exports.authFilter = function (request, result, next) {
    reqUrl = request.urlp = url.parse(request.url, true);

    // Logout
    if (reqUrl.pathname == "/logout") {
      request.session.destroy();
      result.redirect('/easysoa');
      return;
    }
    
    // User data
    if (reqUrl.pathname == "/userdata") {
        if (request.session && request.session.username) {
            var responseData = new Object();
            responseData.username = request.session.username;
            result.write(JSON.stringify(responseData));
        }
        else {
            result.writeHead(403);
        }
        result.end();
        return;
    }

    // Handle already logged user
    if (request.session && request.session.username != undefined) {
        if (request.url == '/login') {
            if (request.body && request.body.prev) {
               result.redirect((request.body.prev != '') ? request.body.prev : '/easysoa');
               return;
            }
            else {
               result.redirect('/easysoa');
               return;
            }
        }
        else {
            next();
            return;
        }
    }

    // Authentication
    if (reqUrl.pathname.indexOf('login.html') != -1) {
        next();
        return;
    }
    else if (reqUrl.pathname == "/login" && request.body) {
        try {
            isLoginValid(request.body.username, request.body.password, function (callbackResult) {
              // Logged successfully
              if (callbackResult) {
                    request.session.username = request.body.username;
                    request.session.password = request.body.password; // XXX: Could store the Base64 hash instead
                  console.log('[INFO] User `' + request.body.username + '` just logged in');
                  result.redirect((request.body.prev != '') ? request.body.prev : '/easysoa');
                  return;
              // Unauthorized
              } else {
                result.redirect('/easysoa/login.html?error=true&prev='+request.body.prev);
                return;
              }
           });
         }
         catch (error) {
            console.log("[INFO] Request error, assuming Nuxeo is not started: "+error);
            result.redirect('/easysoa/login.html?nuxeoNotReady=true&prev='+request.body.prev);
            return;
         }
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

