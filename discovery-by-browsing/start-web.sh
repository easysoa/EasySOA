#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Service Finder Client + Intranet Web Server"
echo "(Access from http://localhost:8083 by default)"
echo $LINE

node ./webserver/server.js
