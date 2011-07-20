Contains the "Smart Travel" demo, a fake SOA built using SCA.

This demo was originally developed by the Galaxy team. Some modifications were made to use it with EasySOA.

## Presentation

“Smart Travel” demo is a fake SOA application built using SCA. The goal of this application is to helps people making a trip to a city.

This fake application works with 4 soap web-services :
	- A meteo web service : http://www.webservicex.net/globalweather.asmx?WSDL
	- A currency service : http://www.currencyserver.de/webservice/currencyserverwebservice.asmx?WSDL
	- A translation service : http://api.microsofttranslator.com/V1/Soap.svc?WSDL
	- A summary service : http://localhost:9080/CreateSummary?wsdl (works on the local machine)

An orchestration workflow is in charge to send request to the meteo, then to the currency and to finish to the translate services. The summary service is called at the end to aggregate the information.

The demo expose a 'GalaxyTrip" SOAP service on the local machine : http://localhost:9000/GalaxyTrip?wsdl. This service is the demo entry point.

This demo can works either with the real web services (An Internet connection is required), or with mocked web services (Currently, only the meteo and the currency web services are mocked).


## Architecture

The demo works with FraSCAti 1.4 and with CXF. The local services are built with composite file. 

The main project for the demo is the "trip" project. 
There is 2 composite file in this project :
	- smart-travel.composite : To use the real web services (Internet connection required)
	- smart-travel-mock-services.composite : To use the mocked web services.

By default, the following ports are used by the local services :
	- 9000 : For the galaxy travel web service, the entry point of the demo.
	- 9080 : For the summary service
	- 9020 : For the mocked services (optional)

To use a proxy, you have to configure CXF with a Spring configuration file.

## Sending a SOAP request

To send a SOAP request to trigger the demo, you can used the SOAP Client plugin for Firefox : https://addons.mozilla.org/en-US/firefox/addon/soa-client/

Here is a sample request :
	- URL : http://localhost:9000/GalaxyTrip
	- BODY : 
		<?xml version="1.0" encoding="utf-8"?>
		<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
			<soap:Body>
				<process xmlns="http://scenario1.j1.galaxy.inria.fr/">
					<arg0>ART</arg0>
					<arg1>I would like a bier !</arg1>
					<arg2>0.5</arg2>
				</process>
			</soap:Body>
		</soap:Envelope>


## Build and run

### Parameters

### To use the real web services or the mocked services, you need to change the composite file used by FraSCAti.

In the 'trip' project, you have to open the pom.xml and search in the 'run' profile the composite tag. Set the value 'smart-travel' if you want to use the original web services or the value 'smart-travel-mock-services.composite' to use the mocked web services (Only the meteo and currency web services for now)  

### To configure a Spring file to use a proxy.

In the 'trip' project, you have to open the pom.xml and search the 'run' profile. The configuration file is set up by a system property.
For instance :  <argument>-Dcxf.config.file.url=src/main/resources/configurationCXF.xml</argument>
See http://cxf.apache.org/docs/configuration.html for more information about how to configure CXF.

### To launch the demo (recommended) :

Simply run the following command in the 'trip' project : mvn -Prun

### To build the entire project :

Simply use `buildAndRunDemoTravel.sh` (or `.bat`).
