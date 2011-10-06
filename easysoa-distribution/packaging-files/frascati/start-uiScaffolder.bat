@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo UI Scaffolding Proxy
echo (Deployed on http://localhost:8090)
echo DEPENDENCY: Running Travel demo
echo %LINE%

"./bin/frascati" run scaffoldingProxy.composite -libpath ./sca-apps/easysoa-proxy-core-scaffolderproxy-0.3-SNAPSHOT.jar
