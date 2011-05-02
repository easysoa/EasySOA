<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="1.0">
	<!-- Base server URL -->
	<xsl:variable name="baseServerUrl">http://localhost:7001/?</xsl:variable>
	
	<!-- Root of document -->
	<xsl:template match="wsdl:definitions">	
		<html>
			<head>
				<title>Pure Air Flowers ~ WS Form</title>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/debug.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/cjw_newsletter.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/ezlabel.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/ezflow.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/ezfind.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/ezmultiupload.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/cjw_newsletter.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/pagelayout.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/websitetoolbar.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/common_clean.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/site.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/site_ow.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/common_menu.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/menu.css"/>
			    <link rel="stylesheet" type="text/css" href="index_fichiers/custom.css"/>
  				<style media="screen" type="text/css">#random_image_flash {visibility:hidden}</style>			    
			</head>
	  		<body class="main">
			    <div id="menu_header">
      			<a id="logo" href="http://www.openwide.fr/" title="Pure Air Flowers" name="logo"><img alt="Pure Air Flowers" src="index_fichiers/logo_openwide.png"/></a>
      			<form action="/content/search" method="post" id="fSearch" name="fSearch">
        			<input name="SearchText" id="searchtext" value="Recherche" onclick="this.value='';" type="text"/> <input name="btnOk" id="btnOk" src="index_fichiers/btnOk.gif" type="image"/>
      			</form>
     			</div>
    <div id="random_image_container" style="height: 180px;position:relative;top:-10px;left:0px">
      <img src="img/bann.jpg" />
    </div>
    <div class="clearfloat"></div>
    <div id="container">
      <!-- COLUMNS 1 : START -->
      <div id="col1">
        <!-- ZONE CONTENT: START -->
        <img src="index_fichiers/left_top.gif" alt="menu" class="fltlft" height="3" width="135"/>
        <div class="submenu fltlft">
          <div class="clearfloat"></div><span class="title clearfloat">Pure Air Flowers</span> <a class="sub_level2 selected" href="http://www.openwide.fr/Open-Wide/Open-Wide/Qui-sommes-nous">Présentation</a>
          <div class="block_level3">
            <a class="sub_level3" href="http://www.openwide.fr/Open-Wide/Open-Wide/Qui-sommes-nous/Histoire-et-vocation">- Histoire et vocation</a> <a class="sub_level3" href="http://www.openwide.fr/Open-Wide/Open-Wide/Qui-sommes-nous/Management">- Management</a> <a class="sub_level3" href="http://www.openwide.fr/Open-Wide/Open-Wide/Qui-sommes-nous/Chiffres-cles">- Chiffres clés</a>
          </div><a class="sub_level2" href="http://www.openwide.fr/Open-Wide/Open-Wide/Notre-vision">Notre vision</a> <a class="sub_level2" href="http://www.openwide.fr/Open-Wide/Open-Wide/Nos-metiers">Nos métiers</a> <a class="sub_level2" href="http://www.openwide.fr/Open-Wide/Open-Wide/Communiques">Communiqués</a> <a class="sub_level2" href="http://www.openwide.fr/Open-Wide/Open-Wide/Contactez-nous">Contactez-nous</a>
        </div><img src="index_fichiers/left_bottom.gif" alt="menu" class="fltlft"/><br/>
        <div class="clearfloat"></div>
        <div class="clearfloat"></div><!-- ZONE CONTENT: END -->
      </div><!-- COLUMNS 1 : END -->
      <!-- Main area: START -->
      <!-- Main area content: START -->
      <!-- COLUMNS MIDDLE_CONTAINER : START -->
      <div id="middle_container">
        <div class="dotted_thick"></div>
        <div id="">
          <div id="article">
	  			<!-- Web service name -->
	  			<h2><xsl:value-of select="@name"/></h2>
	  			<h3><xsl:call-template name="service-address" mode="header"/></h3>
				<!-- Web service endpoint-->
				<xsl:key name="baseElements" match="xsd:element" use="@type"/>
				<xsl:key name="complexTypes" match="xsd:complexType" use="@name"/>
				<xsl:apply-templates/>
				</div>
				</div>
				</div>
				</div>
		  	</body>
	  	</html>
  	</xsl:template>

  	<xsl:template name="service-address" mode="header">
  		location : <xsl:value-of select="wsdl:service/wsdl:port/soap:address/@location"/>
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
		<table border="1">
		<tr>
			<td>Field type</td>
			<td>Name</td>
			<td>Value</td>
		</tr>
		<xsl:for-each select="key('complexTypes', $elementName)/xsd:sequence/xsd:element">
		   	<tr>
				<td><xsl:value-of select="@type"/></td>
				<td><xsl:value-of select="@name"/></td>
      			<xsl:choose>
        			<xsl:when test="$readOnly = false">
						<td><input type='text' size="50" name='{@name}'/></td>		        
			        </xsl:when>
        			<xsl:otherwise>
						<td><input type='text' size="50" name='{@name}' disabled=""/></td>
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
  		<h3>Operation <xsl:value-of select="$operationName"/></h3>
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
			Input fields : <br/>
			<xsl:apply-templates select="wsdl:input"/>
			<xsl:variable name="inMessName"><xsl:apply-templates select="wsdl:input/@message"/></xsl:variable>
			<!--input message name : <xsl:value-of select="$inMessName"/><br/>--> 
			<xsl:call-template name="formFields">
				<xsl:with-param name="messageName" select="$inMessName"/>
				<xsl:with-param name="readOnly" select="false()"/>
			</xsl:call-template>
			<!-- output fields -->
			<br/>
			Output fields : <br/>
			<xsl:apply-templates select="wsdl:output"/>
			<xsl:variable name="outMessName"><xsl:apply-templates select="wsdl:output/@message"/></xsl:variable>			
			<!--output message name : <xsl:value-of select="$outMessName"/><br/>-->
			<xsl:call-template name="formFields">
				<xsl:with-param name="messageName" select="$outMessName"/>
				<xsl:with-param name="readOnly" select="true()"/>
			</xsl:call-template>
			<br/>
  		</form>
		<input type="button" value="Submit" OnClick="submit{$operationName}Form('{$operationName}');"/>
		<br/>  		
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