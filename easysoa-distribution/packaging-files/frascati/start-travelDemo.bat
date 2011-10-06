@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo Trip demo SOA
echo (Deployed on http://localhost:9000)
echo DEPENDENCY: Running services backup
echo %LINE%

set CUSTOM_JAVA_OPTS=-Dcxf.config.file=cxfEsperProxy.xml
"./bin/frascati" run smart-travel-mock-services.composite -libpath ./sca-apps/easysoa-samples-smarttravel-trip-1.0-SNAPSHOT.jar ./sca-apps/easysoa-samples-smarttravel-summary-model-1.0-SNAPSHOT.jar

#PS: The summary-model library has been moved out from the lib/ folder due to a classloader conflict with the EasySOA Light trip proxy.
