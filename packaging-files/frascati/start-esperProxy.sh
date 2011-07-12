#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Esper HTTP monitoring proxy"
echo "(Deployed on http://localhost:8082, can be controlled on http://localhost:8083)"
echo $LINE

./bin/frascati run httpProxy.composite -libpath sca-apps/esperfrascatipoc-1.0-SNAPSHOT.jar
