#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "HTTP Proxy Server"
echo "(Default configuration: Host=127.0.0.1, Port=8081)"
echo "When specifying the proxy in your browser, make sure to remove localhost/127.0.0.1 from the proxy exceptions."
echo $LINE

node ./easysoa-web-proxy/httpproxy.js
