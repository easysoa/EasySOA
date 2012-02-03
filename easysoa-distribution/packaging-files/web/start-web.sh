#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "EasySOA Web Servers"
echo "(Default static server configuration: Host=127.0.0.1, Port=8083"
echo " Default proxy configuration: Host=127.0.0.1, Port=8081)"
echo "DEPENDENCIES: Service registry (to log in)"
echo $LINE

cd js
../../node/node easysoa.js
