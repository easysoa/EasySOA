
This bundle contains the CXF server and the frascati proxy for the Pure Air Flowers demo.

The CXF server must be launched before the Frascati proxy.

To launch the cxf server, simply execute the cxf server script with this command : "./start_cxf_server.sh". The server expose a soap web service available at "http://localhost:9010/PureAirFlowers?wsdl".

To launch the Frascati server, simply execute the frascati proxy script : "./start_frascati_proxy.sh". The proxy talks with the cxf server and expose a rest service at "http://localhost:7001/".
