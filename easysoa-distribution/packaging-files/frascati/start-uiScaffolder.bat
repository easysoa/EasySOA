@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo UI Scaffolding Proxy
echo (Deployed on http://localhost:8090)
echo DEPENDENCY: Running Travel demo
echo %LINE%

"./bin/frascati-easysoa" run scaffoldingProxy.composite -libpath ./sca-apps/easysoa-proxy-core-scaffolderproxy-1.0-SNAPSHOT.jar
