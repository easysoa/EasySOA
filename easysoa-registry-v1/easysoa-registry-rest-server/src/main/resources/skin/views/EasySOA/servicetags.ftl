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
		<img id="headerLogo" src="/nuxeo/site/easysoa/skin/EasySOA-50px.png" />
	</div>
	<div id="container">

<#include "/views/EasySOA/macros.ftl">

		<h1>Service</h1>
		<@displayServiceShort service/>

		<h2>Tagged in</h2>

		<#-- compute currentTagIds for later "not yet used tags" display : -->
		<#assign currentTagIds=[]/>
		<#if service['proxies']?has_content>
		<ul>
		<#list service['proxies'] as serviceProxy>
			<#if serviceProxy['parent'].type = 'TaggingFolder'>
					<li><@displayTagShort serviceProxy['parent']/> -
					<form method="POST" action="${Root.path}/proxy${serviceProxy.path}">
						<input name="delete" type="hidden" value=""/>
						<a href="##" onClick="this.parentNode.submit();">Untag</a>
					</form>
					</li>
					<#assign currentTagIds=currentTagIds+[serviceProxy['parent'].id]/>
			</#if>
		</#list>
		</ul>
		</#if>

		<h2>Also tag in...</h2>

		<#list tags as tag>
			<#if !currentTagIds?seq_contains(tag.id)>
			<form method="POST" action="${Root.path}${service.path}/tags">
				<input name="tagPath" type="hidden" value="${tag.path}"/>
				<a href="##" onClick="this.parentNode.submit();">Tag</a> in <@displayTagShort tag/>
			</form>
			</#if>
		</#list>

	</div>

</body>
