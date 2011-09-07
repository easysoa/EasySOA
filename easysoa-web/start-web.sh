#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "HTTP Web Server"
echo "(Default configuration: Host=127.0.0.1, Port=8083)"
echo $LINE

cd js
node web.js
