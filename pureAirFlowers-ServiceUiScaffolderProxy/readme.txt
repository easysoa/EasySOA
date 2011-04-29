A simple REST/SOAP static proxy.

The goal of this proxy is to establish a communication between a REST Request and a SOAP webservice.
It is build to run with Frascati (1.4 or more).

The SCA component name to launch is "RestSoapProxy".
This proxy is designed to run with the PureAirFlowers ServiceUIScaffolder and the CXF server examples.

To run this proxy :

- Build the project with the following maven command : "mvn clean install".
- Run the project with the command : "mvn -Prun".