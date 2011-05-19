@echo off

set NODE=node

set LINE="----------------------------------------------------"

echo %LINE%
echo "HTTP Proxy Server"
echo "(Default configuration: Host=127.0.0.1, Port=8081)"
echo %LINE%

rem Default port: 8081
%NODE% ./proxyserver/httpproxy.js
