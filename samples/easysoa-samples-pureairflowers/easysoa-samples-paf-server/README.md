A simple SOAP WS CXF server designed to work with the pureAirFlowers demo.

This SOAP server is based on Apache CXF.

One operation is available : 

- getOrdersNumber : accepts one parameter (client id) and returns the number of orders for this client.

No database is required. The number of orders returned by the service is hard coded. You can fill the client id parameter with any value. 

To run the server : "java -jar bin\easysoa-samples-paf-server-1.0-SNAPSHOT.jar". The server listen on the port 9001.

-----------------

## FOR DEVELOPERS

To build the project, you need :

A working Maven 3 installed on your computer.

To build the project, use the following command : `mvn clean install`.
To package the project, use the following command : mvn assembly:assembly. A zip archive is generated in the target directory. It contains all required jar libraries in the lib folder, the main jar in the bin directory and a script (sh or bat) to laucnh the server.

