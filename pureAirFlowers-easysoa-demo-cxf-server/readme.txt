A simple SOAP Hello world server.

This SOAP server is based on Apache CXF. 2 operations are available : 

- sayHi : accepts one parameter and returns a string.
- repeatAfterMe : accepts 2 parameters (text to repeat and iterations) and returns a string.

This example works with the proxy and the ServiceUiScaffolder examples.

To run the server : java -jar target/server-0.0.1-SNAPSHOT-with-dep.jar
