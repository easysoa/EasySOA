<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:s="http://www.w3.org/2001/XMLSchema" version="1.0">
	<!-- URL of WSDL of service to call -->
	<xsl:param name="wsdlUrl"/>
	<!-- Base server URL -->
	<xsl:variable name="baseServerUrl">http://localhost:7001/</xsl:variable>
	<!-- For Galaxy demo travel -->
	<!--<xsl:variable name="baseServerUrl">http://localhost:7002/?</xsl:variable> -->

	<!-- Web service endpoint -->
	<xsl:key name="baseElements" match="xsd:element | s:element" use="@type" />
	<xsl:key name="complexTypes" match="xsd:complexType | s:complexType" use="@name" />
	<xsl:key name="bindings" match="wsdl:binding" use="@type" />

	<!-- Header -->
	<xsl:template name="serviceAddress">
		<xsl:param name="address" />
		<a href="{$address}">
			<xsl:value-of select="$address" />
		</a>
	</xsl:template>

	<!-- Root of document -->
	<xsl:template match="wsdl:definitions">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<title>
					EasySOA Core -
					<xsl:value-of select="@name" />
					Service
				</title>
				<link rel="stylesheet" href="../style.css"></link>
			</head>
			<body>
				<!-- Common javascript stuff -->
				<script type="text/javascript">
					function getElementsByClass(searchClass,node,tag) {
						var classElements = new Array();
						if ( node == null )
							node = document;
						if ( tag == null )
							tag = '*';
						var els = node.getElementsByTagName(tag);
						var elsLen = els.length;
						var pattern = new RegExp("(^|\\s)"+searchClass+"(\\s|$)");
						<![CDATA[
						for (i = 0, j = 0; i < elsLen; i++) {
							if ( pattern.test(els[i].className) ) {
								classElements[j] = els[i];
								j++;
							}
						}
						]]>
						return classElements;
					}
				</script>			
				<!-- Web service name -->
				<div id="headerLight">
					<div id="headerLightContents">
						<div id="headerBreadcrumbs">
							<a href="../index.html">EasySOA</a>
							>
							<a href="index.html">Light</a>
							>
							<b>
								<xsl:value-of select="@name" />
							</b>
						</div>
						<b>
							<xsl:value-of select="@name" />
						</b>
						Service
					</div>
				</div>
				<div id="container">
					<h1>Service location</h1>
					<p>
						<xsl:call-template name="serviceAddress">
							<xsl:with-param name="address"
								select="wsdl:service/wsdl:port/soap:address/@location" />
						</xsl:call-template>
					</p>
					<!-- Call the binding template -->
					<xsl:call-template name="porttypes">
						<!-- xsl:with-param name="wsdlUrl" select="wsdl:service/wsdl:port/soap:address/@location" / -->
						<xsl:with-param name="wsdlUrl" select="$wsdlUrl" />
					</xsl:call-template>					
				</div>
			</body>
		</html>
	</xsl:template>

	<!-- Form fields -->
	<xsl:template name="formFields">
		<xsl:param name="messageName" />
		<xsl:param name="readOnly" />
		<xsl:call-template name="fields">
			<xsl:with-param name="elementName">
				<xsl:value-of select="key('baseElements', $messageName)/@name" />
			</xsl:with-param>
			<xsl:with-param name="readOnly" select="$readOnly" />
		</xsl:call-template>
	</xsl:template>

	<!-- Input / Output Fields -->
	<xsl:template name="fields">
		<xsl:param name="elementName" />
		<xsl:param name="readOnly" />
		<table>
			<tr>
				<th style="width: 20%">Field type</th>
				<th style="width: 20%">Name</th>
				<th>Value</th>
			</tr>
			<!--<xsl:choose>-->
				<!-- Structure with sequence/elements -->			
  				<!--<xsl:when test="expression">-->
					<xsl:for-each
						select="key('complexTypes', $elementName)/xsd:sequence/xsd:element | key('complexTypes', $elementName)/s:sequence/s:element" >
						<tr>
							<td>
								<xsl:value-of select="@type" />
							</td>
							<td>
								<xsl:value-of select="@name" />
							</td>
							<xsl:choose>
								<xsl:when test="$readOnly = false">
									<td>
										<input type='text' name='{@name}' class='inputField' />
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td>
										<input type='text' name='{@name}' disabled=""
											id='return_{@name}' class='outputField' />
									</td>
								</xsl:otherwise>
							</xsl:choose>
						</tr>
					</xsl:for-each>
  				<!--</xsl:when>-->
  				<!-- Only elements -->
  				<!--<xsl:otherwise>
    				... some output ....
  				</xsl:otherwise>
			</xsl:choose>-->

		</table>
	</xsl:template>

	<!-- Porttype template -->
	<xsl:template name="porttypes">
		<xsl:param name="wsdlUrl" />
		<xsl:for-each select="wsdl:portType">
			<xsl:variable name="portTypeName">
				<xsl:value-of select="@name" />		
			</xsl:variable>
			<!-- Get the binding name -->				
			<xsl:variable name="bindingName">
				<xsl:value-of select="key('bindings', concat('tns:', $portTypeName))/@name" />		
			</xsl:variable>	
			<h2>
				Binding
				<b>
					<xsl:value-of select="$bindingName" />
				</b>
			</h2>
			<xsl:call-template name="operation">
				<xsl:with-param name="bindingName" select="$bindingName" />
				<xsl:with-param name="wsdlUrl" select="$wsdlUrl" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<!-- Operation template -->
	<!-- for each operation, create an HTML form -->
	<xsl:template name="operation">	
		<!-- Add the binding name as parameter -->	
		<xsl:param name="bindingName" />
		<xsl:param name="wsdlUrl" />
		<xsl:for-each select="wsdl:operation">			
			<xsl:variable name="operationName">
				<xsl:value-of select="@name" />
			</xsl:variable>
			<h3>
				Operation
				<b>
					<xsl:value-of select="$operationName" />
				</b>
			</h3>
			<!-- Javascript code engine for the form -->
			<script type="text/javascript">
				function submit_<xsl:value-of select="$operationName" />_<xsl:value-of select="$bindingName" />_form(form){
					var xhr;
					var operation = "<xsl:value-of select="$operationName" />";
					var binding = "<xsl:value-of select="$bindingName" />";
					var responseMessage = "<xsl:value-of select="wsdl:output/@name"/>";
					getElementsByClass('outputField', document.<xsl:value-of select="$operationName" />_<xsl:value-of select="$bindingName" />, '*')[0].value = "Sending request ...";
					if(window.XMLHttpRequest) // Firefox and others
						xhr = new XMLHttpRequest();
					else if(window.ActiveXObject){ // Internet Explorer
						try {
							xhr = new ActiveXObject("Msxml2.XMLHTTP");
						} catch (e) {
							xhr = new ActiveXObject("Microsoft.XMLHTTP");
						}
					}
					else { // XMLHttpRequest not supported by browser
						alert("Your browser does not support XMLHTTPRequest objects ...");
						xhr = false;
					}
					getElementsByClass('outputField', document.<xsl:value-of select="$operationName" />_<xsl:value-of select="$bindingName" />, '*')[0].value = "Getting response ...";
					xhr.onreadystatechange = function(){
						//alert("xhr.readyState : " + xhr.readyState);
						if(xhr.readyState == 4){
							//alert("xhr.status : " + xhr.status + " ; xhr.responseText : " + xhr.responseText);
							if(xhr.status == 200){
								// receive now json structure
								//getElementsByClass('outputField', document.<xsl:value-of select="$operationName" />_<xsl:value-of select="$bindingName" />, '*')[0].value = xhr.responseText;
								var responseData = JSON.parse(xhr.responseText);
								//for each elements 'outputField'
								var outputFields = getElementsByClass('outputField', document.<xsl:value-of select="$operationName" />_<xsl:value-of select="$bindingName" />, '*');
								<![CDATA[
								for(j=0; j<outputFields.length; j++){
									var fieldName = outputFields[j].name;
									outputFields[j].value = eval("responseData.Body." + responseMessage + "." + fieldName);
								}
								]]>
							} else {
								getElementsByClass('outputField', document.<xsl:value-of select="$operationName" />_<xsl:value-of select="$bindingName" />, '*')[0].value = "Error code " + xhr.status;
							}
						}
					};
					
					// Hard coded URL for demo purpose
					var url = "<xsl:value-of select="$baseServerUrl" />";
					var wsdlUrl = "<xsl:value-of select="$wsdlUrl" />";				
					var params = "";
					// For each form field : add it in the url
	    			<![CDATA[
	    			for(i=0; i<document.forms[form].elements.length; i++){
						// on ne prend que les champs enabled ou en lecture / ecriture
						if(document.forms[form].elements[i].disabled == false){
							//alert("The field name is: " + document.forms[form].elements[i].name + " and it’s value is: " + document.forms[form].elements[i].value + ".<br />");							
							if(params == ""){
								params = params + document.forms[form].elements[i].name + "=" + document.forms[form].elements[i].value;
							} else {
								params = params + "&" + document.forms[form].elements[i].name + "=" + document.forms[form].elements[i].value;							
							}
						}
					}
					params = params + "&wsdlUrl=" + wsdlUrl; 
					url = url + "callService/" + binding + "/" + operation + "/";
					]]>
					//alert("url = " + url + "?" + params);
					xhr.open("GET", url + "?" + params, true);
					xhr.send(null);
				}
				<!-- <![CDATA[...]]> -->
			</script>
	
			<form name='{$operationName}_{$bindingName}' method="get" action="" enctype='text/plain'>
	
				<!-- parameters input fields -->
				<h4>Input fields</h4>
				<xsl:apply-templates select="wsdl:input" />
				<xsl:variable name="inMessName">
					<xsl:apply-templates select="wsdl:input/@message" />
				</xsl:variable>
				<xsl:call-template name="formFields">
					<xsl:with-param name="messageName" select="$inMessName" />
					<xsl:with-param name="readOnly" select="false()" />
				</xsl:call-template>
	
				<!-- output fields -->
				<h4>Output fields</h4>
				<xsl:apply-templates select="wsdl:output" />
				<xsl:variable name="outMessName">
					<xsl:apply-templates select="wsdl:output/@message" />
				</xsl:variable>
				<xsl:call-template name="formFields">
					<xsl:with-param name="messageName" select="$outMessName" />
					<xsl:with-param name="readOnly" select="true()" />
				</xsl:call-template>
	
			</form>
			<br />
			<input type="button" value="Submit"
				OnClick="submit_{$operationName}_{$bindingName}_form('{$operationName}_{$bindingName}');" />
		</xsl:for-each>
	</xsl:template>

	<!-- Service, port & address template -->
	<!--<xsl:template match="wsdl:service" name="service"> Service name : <xsl:value-of 
		select="@name"/> <xsl:apply-templates></xsl:apply-templates> </xsl:template> -->

	<!--<xsl:template match="wsdl:port"> Port binding : <xsl:value-of select="@binding"/> 
		<xsl:apply-templates></xsl:apply-templates> </xsl:template> -->

</xsl:stylesheet> 
