#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Esper HTTP monitoring proxy"
echo "(Deployed on http://localhost:8082, can be controlled on http://localhost:8084)"
echo $LINE

./bin/frascati run httpDiscoveryProxy.composite -libpath sca-apps/easysoa-proxy-core-httpdiscoveryproxy-0.4-SNAPSHOT.jar
