#!/bin/bash

clear

mkdir -p log

# Start functions

serviceregistry()
{
  touch log/serviceregistry.log
  ./serviceregistry/bin/nuxeoctl console > log/serviceregistry.log 2>&1
}

web()
{
  touch log/web.log
  cd web
  chmod +x ./start-web.sh
  ./start-web.sh > ../log/web.log 2>&1
}

webproxy()
{
  touch log/webproxy.log
  cd webproxy
  chmod +x ./start-proxy.sh
  ./start-proxy.sh > ../log/webproxy.log 2>&1
}

webservices()
{
  touch log/webservices.log
  cd webservices
  ./start_cxf_server.sh > ../log/webservices.log 2>&1
}

webservicesproxy()
{
  touch log/webservicesproxy.log
  cd webservices
  ./start_frascati_proxy.sh > ../log/webservicesproxy.log 2>&1
}

travel()
{
  touch log/travel.log
  cd travel/trip
  mvn -Prun > ../../log/travel.log 2>&1
}

# Start processes
echo "Starting EasySOA Demo. A browser page will be opened in a few seconds."
echo "Note that the service registry will take between 30s and 2mn to launch."
serviceregistry &
webproxy &
sleep 5 # Give time to read the msg & let the webproxy launch
web &
webservices &
webservicesproxy &
#travel & # Automatically shuts down
firefox "http://localhost:8083/easysoa" &

echo "Press any key to stop."

# Prepare to interrupt all running processes
shutdown()
{
  echo "Stopping all servers."
  ps | awk 'NR>2 { print $1 }' | xargs kill -9 > /dev/null 2>&1
}

trap shutdown SIGINT SIGTERM

# Wait for a key to be pressed
read -n 1 -s
shutdown

