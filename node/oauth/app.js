var http = require('http');
var util = require('util');
var OAuth = require('./lib/oauth.js').OAuth;
var url = require('url');

// Initialization

console.log("Starting app");

var oa_session = {
  oauth_token: undefined,
  oauth_token_secret: undefined
}

var PORT = 8001;

var oa = new OAuth("http://localhost:8080/nuxeo/oauth/request-token", // Request-token URL
	"http://localhost:8080/nuxeo/oauth/access-token", // Access token URL
	"NodeTest", // Consumer key (must be registered to Nuxeo)
	"nodetest", // Consumer secret (must be registered to Nuxeo)
	"1", // OAuth version (/!\ Nuxeo parses it as an integer, "1.0A" is invalid)
	"http://localhost:8001/afterlogin", // Callback
	"HMAC-SHA1" // Signature type (Nuxeo supports both RSA/HMAC, but this api only supports HMAC
);

/*var nuxeoLoginOptions = {
  host: 'localhost',
  port: 8080,
  path: '/nuxeo/site/automation/login',
  auth: 'Administrator:Administrator'
};
http.request(nuxeoLoginOptions, function(res) {
  console.log("Login result: " + res.statusCode);
}).end();*/

// HTTP server

var server = http.createServer(function(req, res) {

  var parsedUrl = url.parse(req.url, true);

  // Step 2
  if (parsedUrl.pathname == '/login') {
    requestToken(res);
  }
  
  // Step 3
  else if (parsedUrl.pathname == '/afterlogin') {
    oa.getOAuthAccessToken(
      oa_session.oauth_token, // OAuth token
      oa_session.oauth_token_secret, // OAuth secret
        function accessTokenCallback(error, access_token, access_token_secret, results) {
	        if (error) {
		        console.log('error during token access');
		        res.end(error.data);
	        } else {
		        console.log('oauth_access_token: ' + access_token);
		        console.log('oauth_access_token_secret: ' + access_token_secret);
		        console.log('accesstoken results: ' + util.inspect(results));
	          oa_session.access_token = access_token;
		        oa_session.access_token_secret = access_token_secret;
		        redirect(res, 'http://localhost:8001/app');
	        }
        }
      );
  }
  
  // Step 4
  else if (parsedUrl.pathname == '/app') {
  
    if (oa_session.access_token == undefined) {
      redirect(res, '/');
    }
  
    res.write('<h1>I can now delete all your Nuxeo documents.</h1>');
    
		var request = oa.post('http://localhost:8080/nuxeo/site/automation/Document.Fetch',
		  oa_session.access_token,
		  oa_session.access_token_secret,
		  '{params:{"value":"/default-domain"},context:{}}',
		  'application/json+nxrequest',
		  function(error, data) {
			  if (error) {
			    res.write('<h3>Or not...</h3>');
				  res.write(error.data);
          res.end();
			  } else {
			    res.write('<h3>Proof below</h3>');
			    res.write(JSON.stringify(data));
			    res.end();
			  }
			}
		);
	//	request.end();
  }
  
  // Step 1
  else {
    res.write('<h1>Welcome</h1>');
    res.write('Enable the app by logging to Nuxeo: <br />');
    res.write('<form action="/login"><input type="submit" value="Login" /></form>');
    res.end();
  }
});

// OAuth functions

function requestToken(res) {
  oa.getOAuthRequestToken({}, function(error, oauth_token, oauth_token_secret, results) {
	    if (error) {
		    console.log('Token request error!');
		    res.end(error.data);
	    } else {
		    console.log('oauth_token: ' + oauth_token);
		    console.log('oauth_token_secret: ' + oauth_token_secret);
		    console.log('requestoken results: ' + util.inspect(results));
		    console.log("Requesting access token, redirecting to Nuxeo login form");
		    oa_session.oauth_token = oauth_token;
		    oa_session.oauth_token_secret = oauth_token_secret;
		    redirect(res, 'http://localhost:8080/nuxeo/oauth/authorize?oauth_token=' + oauth_token);
		  }
  });
};



function redirect(res, theUrl) {
		res.writeHead(307, {'Location': theUrl});
		res.end();
}


// Start server

server.listen(PORT);
