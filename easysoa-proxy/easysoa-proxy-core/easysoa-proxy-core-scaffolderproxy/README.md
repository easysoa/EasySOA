#Light proxy Architecture :

The ligth proxy works with two components : the HTML form generator and the REST/SOAP proxy.

##HTML Form generator :

Its role is to generate an HTML form from a WSDL document. The generation is made with a XSLT transformation. Javascipt code is embeded directly in the form. With this solution, the form can send a request, receive and display a response directly to the REST/SOAP proxy.

##REST / SOAP proxy :

The REST/SOAP proxy receive a REST request containing the parameters to map in the SOAP request and indications about the WSDL, port and operation tuo use. It builds with these indications the SOAP request and maps the received paramaters in the request. Then the SOAP request is send to the server specified in the WSDL. The server response is send back to the HTML form.

#Architectural choices :

The two components of the light proxy works with FraSCAti.

##For HTML Form generator :

    - Use of XSLT : At the moment the HTML form is generated with an XSLT transformation. It is working for simple WSDL definition with messages containing only primitive types. For more complex data structures contained int the WSDL, a best solution will be to use an other technology as templating engine (Velocity).
    - Complex data structures and HTML Form : Not working at the moment. The form can only contains simple primitives types for input and outpu fields. We have to find a way to display complex dtat structures.
    - Embeded javascript : Javascript code is embeded directly in the form for sending request and receiving and displaying the response.
    - Velocity is used to make the link between the REST request to get the form and the XSLT transformation method.

##For Rest/SOAP proxy :

    - Use of SOAPUI : SOAPI=UI is used in the proxy to easily generate the SOAP request and to establish the dialog with the targeted web service. unfortunately, there is no mecanism in SOAPUI to map easilly form parameters in the SOAP request. We have to use standard java string manipulation methods.

#Tests :

A test class is avalaible : FormGeneratoTest.java. This test is fully automated, starts both components and send request automatically. The proxy has been successfully tested with pure air flowers and smart travel samples.

#How to use :

    - Start the light in FraSCAti with the composite : scaffoldingProxy.composite

To generate a WSDL form :

    - Go to 'http://localhost:8090/scaffoldingProxy/?wsdlUrl=wsdlFile' where wsdlFile is the address of the WSDL to transform in a HTML Form. For instance : "http://localhost:8090/scaffoldingProxy/?wsdlUrl=http://localhost:8086/soapServiceMock?wsdl".
    - When the HTML Form is displayed, fill the input fields with some values and click on the "send" button. The SOAP service is requested and the response is displayed in the output field.

