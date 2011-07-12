#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "EasySOA Light service proxy"
echo "(Deployed on hhttp://localhost:7001)"
echo $LINE

./bin/frascati run RestSoapProxy.composite -libpath sca-apps/proxy-1.0-SNAPSHOT.jar
