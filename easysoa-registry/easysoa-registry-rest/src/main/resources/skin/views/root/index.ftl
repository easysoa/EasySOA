<!DOCTYPE html>
<html>
	<head>
		<title>EasySOA REST Services Documentation</title>
		<style rel="stylesheet" type="text/css">
		<!--
	  * {
	    margin: 0;
	    padding: 0;
    }

    body {
	    width: 100%;
	    height: 820px;
	    background-color: #EEF5FA;
	    font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
      font-size: 10pt;
    }

    h1 {
	    padding-top: 20px;
	    margin-bottom: 10px;
	    border-bottom: 1px solid grey;
	    color: #446;
	    font-size: 15pt;
    }

    h2 {
	    font: 12pt Arial;
	    font-weight: bold;
	    padding-top: 10px;
	    padding-bottom: 5px;
	    color: #248;
    }

    li {
	    margin-left: 20px;
	    margin-bottom: 10px;
    }

    #headerLogo {
      width: 100%;
      padding-top: 10px;
      padding-left: 280px;
      font-size: 14pt;
      height: 60px;
      background: url('/nuxeo/site/easysoa/skin/EasySOA-50px.png') white;
      background-repeat: no-repeat;
      background-position: 20px 10px;
      border-bottom: 2px solid #444;
    }

    #header {
      width: 100%;
      padding-top: 23px;
      font-size: 14pt;
      font-weight: bold;
      height: 47px;
      background: white;
      background-repeat: no-repeat;
      background-position: 20px 15px;
      border-bottom: 2px solid #444;
    }
    
    #container { 
      padding: 20px;
    }

    p {
      font-size: 10pt;
      margin-bottom: 10px;
    }

    table {
      border-spacing: 0;
      border: 1px solid black;
      margin: 10px;
      width: 800px;
    }

    td {
      border: 1px solid grey;
      padding: 5px;
      background-color: white;
    }

    th {
      border: 1px solid grey;
      padding: 5px;
      background-color: #cde;
    }

    a {
      color: #036;
      text-decoration: none;
      font-weight: bold;
    }
    
    .url {
      font-family: monospace;
      background-color: #DDD;
      border: 1px solid #AAA;
    }
    
    .param {
      font-family: monospace;
      background-color: #DDF;
      border: 1px solid #AAF;
      margin-right: 5px;
    }

    .code {
      font-family: monospace;
      background-color: #EEE;
    }
    
    .exampleRequest {
      font-weight: bold;
      padding-bottom: 10px;
      margin-bottom: 10px;
      border-bottom: 1px solid #AAA;
    }
    
    .exampleResponse {
      color: #222;
    }
    
    li {
      margin-bottom: 0;
    }
    
    th {
      font-size: 12pt;
    }
    
    td:first-child {
      font-size: 12pt;
    }

    -->
	  </style>
	</head>
</html>
<body>

<div id="header">
  <div id="headerLogo"></div>
</div>
<div id="container">

  <h1>EasySOA REST Services Documentation</h1>

  <h2>Service Finder</h2>
  
  <table style="width: 100%; max-width: 1000px">
    <tr><th colspan="2">Service Finder</th></tr>
    <tr><td style="width: 150px">Description</td><td>Allows to find services by providing any kind of URL. The adress is then run through several strategies to find WSDLs.</td></tr>
    <tr><td>URL</td><td><span class="url">/nuxeo/site/easysoa/servicefinder</span></td></tr>
    <tr><td>Method</td><td>GET</td></tr>
    <tr><td>Parameters</td><td><span class="param">url</span> The URL to explore<br />
    <tr><td>Parameters format</td><td>Append the URL directly like this: <span class="url">.../servicefinder/<span class="param">http://...</span></span></td></tr>
    <tr><td>Result</td><td>A JSON object containing:
      <ul>
        <li><span class="param">/applicationName</span> The application name</li>    
        <li><span class="param">/foundLinks</span> A list of WSDLs (the key is the service name, the value is its URL)</li>
      </ul>
    </td></tr>
    <tr><td>Example</td><td class="code">
      <p class="exampleRequest">/nuxeo/site/easysoa/servicefinder/http://www.myapp.com/services</p>
      <pre class="exampleResponse">
{
  "applicationName": "My App", 
  "foundLinks": {
    "My service name": "http://www.myapp.com/services/myservice?wsdl",
    "My other service name": "http://www.myapp.com/services/myotherservice?wsdl"
  }
}
      </pre>
    </td></tr>
  </table>
  
  <h2>Discovery API</h2>
  
  <table style="width: 100%; max-width: 1000px">
    <tr><th colspan="2">Application Discovery</th></tr>
    <tr><td style="width: 150px">Description</td><td>Allows to notify information about an application.</td></tr>
    <tr><td>URL</td><td><span class="url">/nuxeo/site/easysoa/discovery/appliimpl</span></td></tr>
    <tr><td>Method</td><td>POST</td></tr>
    <tr><td>Parameters</td><td>
      <ul>
        <li><span class="param">url</span> <b>(mandatory)</b> Services root</li>
        <li><span class="param">title</span> Application title</li>
        <li>...</li>
      </ul>
      For all parameters, browse to <span class="url">/nuxeo/site/easysoa/discovery/appliimpl</span> (same URL, GET method)
    </td></tr>
    <tr><td>Parameters format</td><td>Through the body, in the "application/x-www-form-urlencoded" format.</td></tr>
    <tr><td>Result</td><td>A JSON object containing a <span class="param">result</span> entry, that should contain "ok" if the request succeeded.</td></tr>
    <tr><td>Example</td><td class="code">
      <p class="exampleRequest">/nuxeo/site/easysoa/notification/appliimpl<br />
      <u><i>Body</i></u>: url=http://app.com/&title=My App</p>
      <pre class="exampleResponse">
{
  "result": "ok"
}
      </pre>
    </td></tr>
  </table>
  
  <table style="width: 100%; max-width: 1000px">
    <tr><th colspan="2">Service API Discovery</th></tr>
    <tr><td style="width: 150px">Description</td><td>Allows to notify information about an API.</td></tr>
    <tr><td>URL</td><td><span class="url">/nuxeo/site/easysoa/discovery/api</span></td></tr>
    <tr><td>Method</td><td>POST</td></tr>
    <tr><td>Parameters</td><td>
      <ul>
        <li><span class="param">url</span> <b>(mandatory)</b> Services root</li>
        <li><span class="param">parentUrl</span> The parent application or API URL</li>
        <li><span class="param">title</span> Application title</li>
        <li>...</li>
      </ul>
      For all parameters, browse to <span class="url">/nuxeo/site/easysoa/discovery/api</span> (same URL, GET method)
    </td></tr>
    <tr><td>Parameters format</td><td>Through the body, in the "application/x-www-form-urlencoded" format.</td></tr>
    <tr><td>Result</td><td>A JSON object containing a <span class="param">result</span> entry, that should contain "ok" if the request succeeded.</td></tr>
    <tr><td>Example</td><td class="code">
      <p class="exampleRequest">/nuxeo/site/easysoa/notification/api<br />
      <u><i>Body</i></u>: url=http://app.com/api&parentUrl=http://app.com/&title=My App</p>
      <pre class="exampleResponse">
{
  "result": "ok"
}
      </pre>
    </td></tr>
  </table>
  
  <table style="width: 100%; max-width: 1000px">
    <tr><th colspan="2">Service Discovery</th></tr>
    <tr><td style="width: 150px">Description</td><td>Allows to notify information about a service.</td></tr>
    <tr><td>URL</td><td><span class="url">/nuxeo/site/easysoa/discovery/service</span></td></tr>
    <tr><td>Method</td><td>POST</td></tr>
    <tr><td>Parameters</td><td>
      <ul>
        <li><span class="param">url</span> <b>(mandatory)</b> Services root</li>
        <li><span class="param">parentUrl</span> <b>(mandatory)</b> The parent API URL</li>
        <li><span class="param">title</span> Service title</li>
        <li>...</li>
      </ul>
      For all parameters, browse to <span class="url">/nuxeo/site/easysoa/discovery/service</span> (same URL, GET method)
    </td></tr>
    <tr><td>Parameters format</td><td>Through the body, in the "application/x-www-form-urlencoded" format.</td></tr>
    <tr><td>Result</td><td>A JSON object containing a <span class="param">result</span> entry, that should contain "ok" if the request succeeded.</td></tr>
    <tr><td>Example</td><td class="code">
      <p class="exampleRequest">/nuxeo/site/easysoa/notification/service<br />
      <u><i>Body</i></u>: url=http://app.com/api/service&parentUrl=http://app.com/api&title=My App</p>
      <pre class="exampleResponse">
{
  "result": "ok"
}
      </pre>
    </td></tr>
  </table>
  
  <table style="width: 100%; max-width: 1000px">
    <tr><th colspan="2">Service Reference Discovery</th></tr>
    <tr><td style="width: 150px">Description</td><td>Allows to notify information about a service reference.</td></tr>
    <tr><td>URL</td><td><span class="url">/nuxeo/site/easysoa/discovery/servicereference</span></td></tr>
    <tr><td>Method</td><td>POST</td></tr>
    <tr><td>Parameters</td><td>
      <ul>
        <li><span class="param">parentUrl</span> <b>(mandatory)</b> The parent application URL</li>
        <li><span class="param">archiPath</span> <b>(mandatory)</b> Reference Path in architecture</li>
        <li><span class="param">title</span> Service reference title</li>
        <li>...</li>
      </ul>
      For all parameters, browse to <span class="url">/nuxeo/site/easysoa/discovery/servicereference</span> (same URL, GET method)
    </td></tr>
    <tr><td>Parameters format</td><td>Through the body, in the "application/x-www-form-urlencoded" format.</td></tr>
    <tr><td>Result</td><td>A JSON object containing a <span class="param">result</span> entry, that should contain "ok" if the request succeeded.</td></tr>
    <tr><td>Example</td><td class="code">
      <p class="exampleRequest">/nuxeo/site/easysoa/notification/servicereference<br />
      <u><i>Body</i></u>: archiPath=/api/service&parentUrl=http://app.com&title=My Service Reference</p>
      <pre class="exampleResponse">
{
  "result": "ok"
}
      </pre>
    </td></tr>
  </table>

</div>

</body>
