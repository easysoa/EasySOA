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

		<h1>Documentation du service ${service.path} ${service.title} (${service['soan:name']})</h1>

		Vous voulez :
		<ul>
			<li>vous en servir par une application</li>
			<li>prototyper)</li>
			<li>développer avec[doc,essai en ligne, test endpoint, simulation]</li>
			<li>le développer(specs, protos(mocks))</li>
		</ul>

		<h2>(description)</h2>
		${service['dc:description']}

		<h2>Documentation(manuelle, extraite)</h2>
		<#-- doc of first impl, since none on service itself : -->
		<#if actualImpls?size != 0>
		${actualImpls[0]['impl:documentation']}
		<@displayProps actualImpls[0]['impl:operations'] ''/>
		</#if>

		<h2>Usages</h2>
		oé (applications : le déployant ; architecture : le consommant)

		<#-- IntelligentSystems tagging it, since only Applications from now : -->
		<b>Applications :</b><br/>
		<#if service['proxies']?has_content>
		<ul>
		<#list service['proxies'] as serviceProxy>
			<#if serviceProxy['parent'].type = 'IntelligentSystem'>
					<li><@displayDocShort serviceProxy/></li>
			</#if>
		</#list>
		</ul>
		</#if>

		<#-- TaggingFolder tagging it, since only Applications from now : -->
		<br/><b>Business Processes :</b><br/>
		<#if service['proxies']?has_content>
		<ul>
		<#list service['proxies'] as serviceProxy>
			<#if serviceProxy['parent'].type = 'TaggingFolder'>
					<li><@displayTagShort serviceProxy/></li>
			</#if>
		</#list>
		</ul>
		</#if>

		<br/><a href="${Root.path}${service.path}/tags">Also tag in...</a>

		<br/>exemples d'appel

		<br/>(autres tags)

		<h2>Interface(s)</h2>
		WSDL, JAXRS...
		
		<h2>Implementation(test)</h2>
		et consomme, dépend de (en mode non test)

		<br/><b>Implementations :</b><br/>
      <#-- @displayDocsShort actualImpls/ -->
      <@displayDocs actualImpls shortDocumentPropNames + serviceImplementationPropNames/>
		<br/>
		<br/><b>Mocks :</b><br/>
		<#-- @displayDocsShort mockImpls/ -->
      <@displayDocs mockImpls shortDocumentPropNames + serviceImplementationPropNames/>
		<#-- TODO TEST ismock : ${mockImpls[0]['impl:ismock']} -->

		<h2>Endpoint</h2>
		Déployé à : URL
		<br/>Et a déploiements de test :


		<h1>test</h1>

		<h2>Contenu dans</h2>
		<#if service['proxies']?has_content><#list service['proxies'] as proxy><@displayDocShort proxy['parent']/> ; </#list></#if>

		<h2>Contient</h2>
		<#if service['children']?has_content><@displayDocsShort service['children']/></#if>

		<h2>log : all props</h2>
		<@displayDoc service/>
	</div>

</body>
