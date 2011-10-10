@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo UI Scaffolding Proxy
echo (Deployed on http://localhost:8090 (scaffolder) and http://localhost:7001 (REST to SOAP proxy))
echo DEPENDENCY: Running Travel demo
echo %LINE%

"./bin/frascati" run scaffoldingProxy_monitored.composite -libpath ./sca-apps/easysoa-proxy-core-scaffolderproxy-0.3-SNAPSHOT.jar
