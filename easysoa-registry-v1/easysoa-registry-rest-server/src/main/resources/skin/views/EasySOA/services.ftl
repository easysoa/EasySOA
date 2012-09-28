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

<#include "/views/EasySOA/macros.ftl">

		<h1>Services</h1>

		<@displayServicesShort services/>

		<h1>By tags</h1>

		<#list tags as tag>
		<#if tagId2Services?keys?seq_contains(tag.id)>
		<h3>Services (${tagId2Services[tag.id]?size}) of tag <@displayTagShort tag/></h3>
		<@displayServicesShort tagId2Services[tag.id]/>
		</#if>
		</#list>

		<h2>Untagged services (${untaggedServices?size})</h2>

		<@displayServicesShort untaggedServices/>

		<h2>Tags without services (${tags?size - tagId2Services?keys?size})</h2>

		<ul>
			<#list tags as tag>
			<#if !tagId2Services?keys?seq_contains(tag.id)>
			<li><@displayTagShort tag/></li>
			</#if>
			</#list>
		</ul>

	</div>

</body>
