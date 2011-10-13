
#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "UI Scaffolding Proxy"
echo "(Deployed on http://localhost:8090 (scaffolder) and http://localhost:7001 (REST to SOAP proxy))"
echo $LINE

./bin/frascati run scaffoldingProxy_monitored.composite -libpath ./sca-apps/easysoa-proxy-core-scaffolderproxy-0.4-SNAPSHOT.jar
