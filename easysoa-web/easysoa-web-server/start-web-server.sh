#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Service Finder Client + Intranet Web Server"
echo "(Access from http://127.0.0.1:8083 by default)"
echo $LINE

node ./server.js
