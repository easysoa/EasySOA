@echo off

set NODE=node

set LINE=----------------------------------------------------

echo %LINE%
echo HTTP Web Server
echo (Default configuration: Host=127.0.0.1, Port=8083)
echo %LINE%

rem Default port: 8083
cd js
%NODE% web.js
