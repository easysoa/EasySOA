## Introduction

A static XSL transformation to generate an HTML form from a WSDL.

This example can work with the PureAirFlowers REST/SOAP proxy and with the PureAirFlowers CXF server.

## Execution

To run this example, process the transformation and open the generated HTML file. 
A click on the submit button send a rest request to the proxy server. The response is processed with an Ajax script.

The proxy server must be started on the port 7001 for PureAirFlowers.

### Warning

Currently, you have to modify the XSL file depending on the file you want to transform:

     <!-- Base server URL -->
     <xsl:variable name="baseServerUrl">http://localhost:7001/?</xsl:variable>
     <!-- For Galaxy demo travel -->	
     <!--<xsl:variable name="baseServerUrl">http://localhost:7002/?</xsl:variable>-->

## For developers

To generate the HTML file, you can use the following Eclipse plugin "Eclipse XSL Developer Tools".
