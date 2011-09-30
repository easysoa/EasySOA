@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo EasySOA Light service proxy for the Pure Air Flowers demo
echo (Deployed on http://localhost:7001)
echo DEPENDENCY: Running PAF demo
echo %LINE%

set CUSTOM_JAVA_OPTS=-Dcxf.config.file=cxfEsperProxy.xml
"./bin/frascati-easysoa" run RestSoapProxy.composite -libpath ./sca-apps/easysoa-samples-paf-restsoapproxy-1.0-SNAPSHOT.jar
