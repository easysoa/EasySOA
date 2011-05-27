<!DOCTYPE html>
<html>
	<head>
		<title>EasySOA REST Services Documentation</title>
	</head>
	<style type="text/css">
	<!--
		body {
			background-color: #EEF5FA;
			font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
			color: #023;
		}
	->
	</style>
</html>
<body>

<h1>EasySOA REST Services Documentation</h1>

<h2>Service notification</h2>

<h3>Full API</h3>

	<p>Method: <b>POST</b><br />
	Use: <b>.../nuxeo/site/easysoa/notification/(appliimpl|api|service)/</b></p>
	
	<p>Parameters have to be specified through the request content, following the content-type <i>application/x-www-form-urlencoded</i>.<br />
	Append <b>/doc</b> to the path of any of the 3 services to get further documentation.</p>
	
<h3>Cross-domain clients compliant API</h3>

	<p>Lessened API notification service (WSDL only), available for client cross-domain requests.<br />
	Uploads a WSDL to the Nuxeo repository, and fills business metadata given in parameters.</p>
	
	<p>Method: <b>GET</b><br />
	Use: <b>.../nuxeo/site/easysoa/notification/{applicationName}/{serviceName}/{url}</b></p>
	
	<p>Params:<br />
	 * <b>{applicationName}</b> The application name, optional (ignored if empty) <br />
	 * <b>{serviceName}</b> The service name, optional (ignored if empty)<br />
	 * <b>{url}</b> The file to upload, not encoded. Other protocols than HTTP are not supported.</p>
	
<h2>WSDL Scraper</h2>

	<p>Tries to find WSDLs from given web page.</p>
	
	<p>Method:<b>GET</b><br />
	Use: <b>.../nuxeo/site/easysoa/wsdlscraper/{url}</b></p>
	
	<p>Params: <b>{url}</b> The encoded URL of the page to consider.<br />
	Other protocols than HTTP are not supported.</p>
	
	<p>Return: <b>{ "applicationName": "(the application name)", "foundLinks": { "(link name)" : "(link url)", "(link2 name)" : "(link2 url)" ... } }</b>.</p>
	
	<p>JSONP Support: add a "callback" query parameter to make the request return a JSONP call instead of a JSON object.</p>


</body>