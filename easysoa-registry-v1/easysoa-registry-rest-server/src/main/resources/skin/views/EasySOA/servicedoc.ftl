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
	padding: 15px;
}

#header {
	width: 100%;
	font-size: 14pt;
	font-weight: bold;
	height: 80px;
	background: white;
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
		<img id="headerLogo" src="easysoa/skin/EasySOA-50px.png" />
	</div>
	<div id="container">

		<h1>Documentation du service ${service.path} ${service.title}</h1>

		Vous voulez : * vous en servir par une application (* prototyper) * développer avec[doc,essai en ligne, test endpoint, simulation] * le développer(specs, protos(mocks))

		<h2>(description)</h2>

		<h2>Documentation(manuelle, extraite)</h2>

		<h2>Usages</h2>
		où
		exemples d'appel
		(autres tags)

		<h2>Interface(s)</h2>
		
		<h2>Implementation(test)</h2>
		et consomme, dépend de (en mode non test)

		<h2>Endpoint</h2>
		Déployé à : 
		Et a déploiements de test :

		<h2>log : all props</h2>
		<ul>
		<#list service as doc>

		</#list>
		</ul>
	</div>

</body>
