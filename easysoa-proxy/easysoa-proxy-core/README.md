## EasySOA Proxy

### About
Once configured as HTTP proxy (available on port 8082) of your middleware service
components, EasySOA Proxy listens to HTTP service calls and provides :
* a simple UI to configure the context of what it's listening to (which project...)
available at http://localhost:9080/easysoa-proxy-web
* runtime discovery of SOAP service endpoints and consumptions, which are
registered in the (by default local) EasySOA Registry v1 (see
http://github.com/easysoa/EasySOA-Incubation/easysoa-registry-v1 )
* record and replay of service calls, using the embedded EasySOA Proxy Driver UI
available at http://localhost:8084
* replay of a templatized and parametrized version (by correlating request and
response) of such recorded service calls, available using the EasySOA Service
Scaffolder UI
* simulations of services (following the same correlation principle), based on
such recorded service calls

Some other features are not yet available along the EasySOA Registry v1 releases,
but may still be found in the EasySOA 0.4 release (see http://www.easysoa.org ) :
* EasySOA Service Scaffolder and its UI, available at
http://localhost:8083/scaffoldingProxy/?wsdlUrl=your_wsdl_url
* runtime discovery of REST services endpoints, which are registered in the EasySOA
Registry 0.4, along with some realtime Esper-aggregated information such as call
count
* Service Event Subscription proxy handler (Its RoR-based UI must be installed
separately)
* packaging within Nuxeo, using "FraSCAti in Nuxeo" (see
https://github.com/easysoa/EasySOA/wiki/Frascati-in-nuxeo-architecture )

See documentation on wiki at https://github.com/easysoa/EasySOA/wiki .

Releases are available at http://www.easysoa.org .

### How to develop :

Prerequisites : Java 6+, Maven 3

Compiling :

	mvn clean install -DskipTests


### How to deploy :

The compilation step above produces .war files in easysoa-proxy-war/target and
easysoa-proxy-web/target that you can deploy in your favorite application server.

*WARNING* for now, easysoa-proxy-web only supports to be deployed at application
server root url.

Here is how to deploy them on Apache Tomcat 7 :

In your system's hosts file, define the "vmregistry" host as its real IP (rather than
the loopback address 127.0.0.1, else the HTTP Proxy won't be visible from remote
computers).

Download Tomcat 7 from http://tomcat.apache.org/download-70.cgi , unzip it, rename its
directory to "apache-tomcat7-proxy" and change all 80xx ports to 90xx in conf/server.xml
(allows to have a running EasySOA Registry on the 8080 port).

Copy easysoa-proxy-war/target/*war in its webapps directory and
easysoa-proxy-web/target/easysoa-proxy-web/* in its webapps/ROOT directory :

   # To a remote computer
   scp easysoa-proxy-war/target/*.war [USER]:[REMOTE_HOST]:/home/[USER]/install/apache-tomcat7-proxy/webapps/
   scp -r easysoa-proxy-web/target/easysoa-proxy-web/* [USER]:[REMOTE_HOST]:/home/[USER]/install/apache-tomcat7-proxy/webapps/ROOT/

   # On local computer, use these commands instead :
   rm -rf [TOMCAT_HOME]/webapps/*
   cp -rf easysoa-proxy-war/target/*.war [TOMCAT_HOME]/webapps/
   cp -rf easysoa-proxy-web/target/easysoa-proxy-web [TOMCAT_HOME]/webapps/ROOT

Then go in bin/ directory and start it :

    cd [TOMCAT_HOME]/bin/
	./catalina.sh run
	
EasySOA Proxy Web UI will be available at http://localhost:9080/easysoa-proxy-web ,
EasySOA Proxy Driver at http://localhost:8084 and EasySOA HTTP Proxy on port 8082.

If the following error message is displayed :

	The BASEDIR environment variable is not defined correctly
	This environment variable is needed to run this program

Then before starting it, just go in bin folder and execute the following command : 

	chmod +x *.sh
