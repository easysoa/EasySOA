@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo EasySOA Light service proxy
echo (Deployed on http://localhost:7001)
echo DEPENDENCY: Running PAF services
echo %LINE%

set CUSTOM_JAVA_OPTS=-Dcxf.config.file=cxfEsperProxy.xml
"./bin/frascati-easysoa" run RestSoapProxy_withIntents.composite -libpath sca-apps/proxy-1.0-SNAPSHOT.jar
