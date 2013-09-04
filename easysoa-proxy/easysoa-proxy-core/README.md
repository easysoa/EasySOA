## EasySOA Proxy

### About :

EasySOA proxy is a component of the [EasySOA project](http://www.easysoa.org) and
developed by its partners :
* [INRIA labs](http://www.inria.fr)
* [EasiFab](http://easifab.net)
* [Talend](http://www.talend.com)
* [Nuxeo](http://www.nuxeo.org)
* [Bull](http://www.bull.com)
* [Open Wide](http://www.openwide.fr)


### Features :

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

	# Deploy to a remote computer :
	scp easysoa-proxy-war/target/*.war [USER]:[REMOTE_HOST]:/home/[USER]/install/apache-tomcat7-proxy/webapps/
	scp -r easysoa-proxy-web/target/easysoa-proxy-web/* [USER]:[REMOTE_HOST]:/home/[USER]/install/apache-tomcat7-proxy/webapps/ROOT/

	# On local computer, use these commands instead :
	cp -rf easysoa-proxy-war/target/*.war [TOMCAT_HOME]/webapps/
	cp -rf easysoa-proxy-web/target/easysoa-proxy-web [TOMCAT_HOME]/webapps/ROOT

	# If you're adding a new version, remove the older one first
	# (else FraSCAti startup errors "component XXX is already defined") :
	rm -rf [TOMCAT_HOME]/webapps/easysoa-proxy*
	rm -rf [TOMCAT_HOME]/webapps/ROOT/*

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


## FAQ

### OutOfMemoryError - PermGen space error
May happen with other webapps in the same tomcat. To solve it, add to catalina.sh :
	
	JAVA_OPTS=-XX:MaxPermSize=128m

### FraSCAti startup error - "component XXX is already defined"
In log file (apache-tomcat7-proxy/logs/catalina.out etc.), a lot of errors such as :

	sept. 04, 2013 5:20:38 PM org.ow2.frascati.parser.core.ParsingContextImpl error
	SEVERE: jar:file:/home/mdutoo/dev/easysoa/apache-tomcat7-proxy/webapps/ROOT/WEB-INF/lib/frascati-assembly-factory-1.6-20130820.132037-101.jar!/org/ow2/frascati/assembly/factory/AssemblyFactory.composite: <sca:composite name="org.ow2.frascati.assembly.factory.AssemblyFactory"> - <component name='sca-component-property'> is already defined

=> because TWO VERSIONs of FraSCAti jars with different build / release number were added
=> rm -rf webapps/ROOT before adding them from easysoa-proxy(-web) war build

going after :

	sept. 04, 2013 5:23:47 PM org.ow2.frascati.util.AbstractLoggeable warning
	WARNING: 172 errors detected during the checking phase of composite 'org/ow2/frascati/FraSCAti'
	sept. 04, 2013 5:23:47 PM org.ow2.frascati.util.AbstractLoggeable severe
	SEVERE: Cannot load the OW2 FraSCAti composite
	org.ow2.frascati.util.FrascatiException: Cannot load the OW2 FraSCAti composite
		at org.ow2.frascati.FraSCAti.initFrascatiComposite(FraSCAti.java:191)
		at org.ow2.frascati.FraSCAti.newFraSCAti(FraSCAti.java:246)
		at org.ow2.frascati.FraSCAti.newFraSCAti(FraSCAti.java:222)
		at org.ow2.frascati.servlet.FraSCAtiServlet.init(FraSCAtiServlet.java:150)

Solution :

there are two different versions of FraSCAti in the same webapp. This probably
happened because you tried to update webapps to newer versions. In this case, before
updating it remove the older ones :

	rm -rf [TOMCAT_HOME]/webapps/easysoa-proxy*
	rm -rf [TOMCAT_HOME]/webapps/ROOT/*
