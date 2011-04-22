#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Exemple JS Scraping Server"
echo "(Configured in proxyserver/httpproxy-config.js, http://localhost:8082 by default)"
echo $LINE

node ./proxyserver/scraper-exemple.js
