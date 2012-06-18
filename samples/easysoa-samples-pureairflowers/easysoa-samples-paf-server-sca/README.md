A simple SOAP WS CXF server designed to work with the pureAirFlowers demo as a FraSCAti app embedded in Nuxeo.

This SOAP server is based on Apache CXF.

One operation is available :

- getOrdersNumber : accepts one parameter (client id) and returns the number of orders for this client.

No database is required. The number of orders returned by the service is hard coded. You can fill the client id parameter with any value.

The server listens to: http://localhost:9010/PureAirFlowers
The WSDL is available at http://localhost:9010/PureAirFlowers?wsdl

-----------------

## FOR DEVELOPERS

To build the project, you need :

A working Maven 3 installed on your computer.

To build the project, use the following command : `mvn clean install`.

