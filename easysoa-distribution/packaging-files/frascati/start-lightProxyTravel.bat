@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo EasySOA Light service proxy for the Travel Demo
echo (Deployed on http://localhost:7002)
echo DEPENDENCY: Running Travel demo
echo %LINE%

set CUSTOM_JAVA_OPTS=-Dcxf.config.file=cxfEsperProxy.xml
"./bin/frascati-easysoa" run RestSoapProxy.composite -libpath sca-apps/easysoa-samples-smarttravel-scaffolder-proxy-0.3-SNAPSHOT.jar
