<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="1.0">
	<!-- Base server URL -->
	<xsl:variable name="baseServerUrl">http://localhost:7001/?</xsl:variable>
	
	<!-- Root of document -->
	<xsl:template match="wsdl:definitions">	
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>EasySOA Core - <xsl:value-of select="@name"/> Service</title>
        <link rel="stylesheet" href="style.css"/>    
			</head>
			<body>

	  	<!-- Web service name -->
      <div id="header"><b><xsl:value-of select="@name"/></b> Service</div>
      
      <div id="container">

        <h1>Service location</h1>
        <p><xsl:call-template name="service-address" mode="header"/></p>

				<!-- Web service endpoint-->
				<xsl:key name="baseElements" match="xsd:element" use="@type"/>
				<xsl:key name="complexTypes" match="xsd:complexType" use="@name"/>
				<xsl:apply-templates/>
				
      </div>
      </body>
	  	</html>
  	</xsl:template>

  	<xsl:template name="service-address" mode="header">
  		<xsl:value-of select="wsdl:service/wsdl:port/soap:address/@location"/>
  	</xsl:template>
  	
  	<xsl:template name="formFields">
		<xsl:param name="messageName"/>
		<xsl:param name="readOnly"/>
		<!--parametre : <xsl:value-of select="$messageName"/><br/>-->
		<!--element name : <xsl:value-of select="key('baseElements', $messageName)/@name"/>, type : <xsl:value-of select="key('baseElements', $messageName)/@type"/> <br/>-->
	  	<xsl:call-template name="fields">
	  		<xsl:with-param name="elementName"><xsl:value-of select="key('baseElements', $messageName)/@name"/></xsl:with-param>
	  		<xsl:with-param name="readOnly" select="$readOnly"/>
	  	</xsl:call-template>  		
  	</xsl:template>

  	<xsl:template name="fields">
  		<xsl:param name="elementName"/>
  		<xsl:param name="readOnly"/>
  	    <!--param : <xsl:value-of select="$elementName"/><br/>-->
	  	<!--complextype name : <xsl:value-of select="key('complexTypes', $elementName)/@name"/>-->
		<table>
		<tr>
	    <th style="width: 20%">Field type</th>
	    <th style="width: 20%">Name</th>
	    <th>Value</th>
		</tr>
		<xsl:for-each select="key('complexTypes', $elementName)/xsd:sequence/xsd:element">
		   	<tr>
				<td><xsl:value-of select="@type"/></td>
				<td><xsl:value-of select="@name"/></td>
      			<xsl:choose>
        			<xsl:when test="$readOnly = false">
						<td><input type='text' name='{@name}'/></td>		        
			        </xsl:when>
        			<xsl:otherwise>
						<td><input type='text' name='{@name}' disabled=""/></td>
        			</xsl:otherwise>
      			</xsl:choose>
		   	</tr>
		</xsl:for-each>
		</table>
  	</xsl:template>

	<!-- Operation template -->
	<!-- for each operation, create an HTML form -->  	
  	<xsl:template match="wsdl:portType/wsdl:operation">
  		<xsl:variable name="operationName"><xsl:value-of select="@name"/></xsl:variable>
        <h1>Operation <b><xsl:value-of select="$operationName"/></b></h1>
				<!-- ATTENTION : Nom du(des) champ(s) de retour en dur dans le code javascript !!! -->
				<!-- A modifier pour rendre la génération du formulaire dynamique -->
				<script type="text/javascript">
					function submit<xsl:value-of select="$operationName"/>Form(form){ 
    					var xhr;
    					var op = "<xsl:value-of select="$operationName"/>"; 
    					document.<xsl:value-of select="$operationName"/>.ordersNumber.value = "Sending request ...";
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
						document.<xsl:value-of select="$operationName"/>.ordersNumber.value = "Getting response ...";
 					    xhr.onreadystatechange  = function(){ 
         					//alert("xhr.readyState : " + xhr.readyState);
         					if(xhr.readyState  == 4){
         						//alert("xhr.status : " + xhr.status + " ; xhr.responseText : " + xhr.responseText);
              					if(xhr.status == 200){ 
                 					document.<xsl:value-of select="$operationName"/>.ordersNumber.value = xhr.responseText; 
              					} else { 
                 					document.<xsl:value-of select="$operationName"/>.ordersNumber.value = "Error code " + xhr.status;
         						}
         					}
    					};
    					// Hard coded URL for demo purpose
    					//var url = "http://localhost:7001/?";
    					var url = "<xsl:value-of select="$baseServerUrl"/>"
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
						params = params + "&operation=" + op;						
						]]>
						//alert("url = " + url + params);
					   	xhr.open("GET", url + params, true); 
   						xhr.send(null); 
					}
				<!-- <![CDATA[...]]> -->
				</script>
				
  		<form name='{$operationName}' method="get" action="" enctype='text/plain'>
    		
			  <!-- parameters input fields -->
			  <h2>Input fields</h2>
			  <xsl:apply-templates select="wsdl:input"/>
			  <xsl:variable name="inMessName"><xsl:apply-templates select="wsdl:input/@message"/></xsl:variable>
			  <!--input message name : <xsl:value-of select="$inMessName"/><br/>--> 
			  <xsl:call-template name="formFields">
				  <xsl:with-param name="messageName" select="$inMessName"/>
				  <xsl:with-param name="readOnly" select="false()"/>
			  </xsl:call-template>
			
			  <!-- output fields -->
			  <h2>Output fields</h2>
			  <xsl:apply-templates select="wsdl:output"/>
			  <xsl:variable name="outMessName"><xsl:apply-templates select="wsdl:output/@message"/></xsl:variable>			
			  <!--output message name : <xsl:value-of select="$outMessName"/><br/>-->
			  <xsl:call-template name="formFields">
				  <xsl:with-param name="messageName" select="$outMessName"/>
				  <xsl:with-param name="readOnly" select="true()"/>
			  </xsl:call-template>
			  
  		</form>
		<input type="button" value="Submit" OnClick="submit{$operationName}Form('{$operationName}');"/>		
  	</xsl:template>

  	<!-- Service, port & address template -->
  	<!--<xsl:template match="wsdl:service" name="service">
		Service name : <xsl:value-of select="@name"/>
		<xsl:apply-templates></xsl:apply-templates>  	
  	</xsl:template>-->
  	
  	<!--<xsl:template match="wsdl:port">
  		Port binding : <xsl:value-of select="@binding"/>
		<xsl:apply-templates></xsl:apply-templates>  	
  	</xsl:template>-->
  		
</xsl:stylesheet> 
