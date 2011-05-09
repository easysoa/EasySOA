#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "HTTP Proxy Server"
echo "(Default configuration: Host=127.0.0.1, Port=8081)"
echo $LINE

# Default port: 8081
node ./proxyserver/httpproxy.js
