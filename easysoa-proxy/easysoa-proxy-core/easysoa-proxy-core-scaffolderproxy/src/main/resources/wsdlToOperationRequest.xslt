<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
	<xsl:template match="/">
		<soapenv:Envelope>
		  <soapenv:Body>
		    <!-- Operation -->
		      <cli:getOrdersNumber xmlns:cli="http://clients.PureAirFlowers/">
		         <!--Optional:-->
		         <cli:ClientId><xsl:value-of select="//path/component[2]"/></cli:ClientId>
		      </cli:getOrdersNumber>	    	
		  </soapenv:Body>
		</soapenv:Envelope>	
	</xsl:template>
</xsl:stylesheet>
