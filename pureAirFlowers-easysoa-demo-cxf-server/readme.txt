A simple SOAP WS CXF server designed to work with the pureAirFlowers demo.

This SOAP server is based on Apache CXF.

One operation is available : 

- getOrdersNumber : accepts one parameter (client id) and returns the number of orders for this client.

No database is required. The number of orders returned by the service is hard coded. You can fill the client id parameter with any value. 

To run the server : "java -jar target/server-0.0.1-SNAPSHOT-with-dep.jar". The server listen on the port 9001.

***************************

FOR DEVELOPERS

To build the project, you need :

A working Maven 2 installed on your computer.

Use the following command to build the project : "mvn clean install". An executable jar is generated in the project target folder.

