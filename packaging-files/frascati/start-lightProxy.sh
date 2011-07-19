#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "EasySOA Light service proxy"
echo "(Deployed on http://localhost:7001)"
echo "DEPENDENCY: Running PAF services"
echo $LINE

./bin/frascati-easysoa run RestSoapProxy.composite -libpath sca-apps/proxy-1.0-SNAPSHOT.jar
