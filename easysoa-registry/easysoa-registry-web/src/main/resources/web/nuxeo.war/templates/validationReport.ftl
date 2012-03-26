<!doctype html>
<html>
<head>
<title>EasySOA - Scheduled validation report</title>
<style type="text/css">
* {
	margin: 0;
	padding: 0;
}

body {
	width: 100%;
	height: 820px;
	background-color: #EEF5FA;
	font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
}

h1, h2 {
	clear: both;
	max-width: 900px;
	margin-left: 20px;
}

h1 {
	padding-top: 20px;
	margin-bottom: 10px;
	border-bottom: 1px solid grey;
	color: #446;
	font-size: 15pt;
}
h1:first-child {
  padding-top: 10px;
}

h2 {
	font: 12pt Arial;
	font-weight: bold;
	padding-top: 10px;
	padding-bottom: 5px;
	color: #248;
}

li {
	margin-left: 50px;
	margin-bottom: 5px;
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

.result {
  font-size: 15pt;
  text-transform: uppercase;
  font-weight: bold;
}

.resultsmall {
  font-size: 12pt;
  text-transform: uppercase;
  font-weight: bold;
}

span.passed {
  color: darkgreen;
}

span.failed {
  color: red;
}

td.passed {
  background-color: #DFD;
}

td.failed {
  background-color: #FDD;
}

.log {
  font-family: monospace, sans-serif;
  color: #444;
  font-size: 9pt;
}

</style>
</head>
<body>

<h1>EasySOA - Scheduled validation report</h1>

<h2>General information</h2>

<ul>
  <li><strong>Result</strong>: <span class="result ${validationSuccess}">${validationSuccess}</span></li>
  <li><strong>Date</strong>: ${date?datetime}</li>
  <li><strong>Target run</strong>: ${runName}</li>
  <li><strong>Target environment</strong>: ${environmentName}</li>
</ul>

<h2>Per-service results</h2>

<table>
<tr>
  <th>Service name</th>
  <th style="width: 50px">Result</th>
  <#list validatorsNames as validatorsName>
  <th>${validatorsName}</th>
  </#list>
</tr>
<#list results as result>
<tr>
  <td>Service name</td>
  <td class="${result.validationSuccess}"><span class="resultsmall ${result.validationSuccess}">${result.validationSuccess}</span></td>
  <#list result.validatorsResults as validatorsResult>
  <td>
    <span class="resultsmall ${validatorsResult.validationSuccess}">
    ${validatorsResult.validationSuccess} 
    <#if validatorsResult.validationSuccess == "failed">
      <br /><span class="log">(${validatorsResult.validationLog})</span>
    </#if>
    </span>
  </td>
  </#list>
</tr>
</#list>
</table>

</body>
</html>