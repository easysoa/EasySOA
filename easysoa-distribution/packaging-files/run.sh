#!/bin/bash

clear

mkdir -p log
export PATH="$PATH:$PWD/node"

# Start functions

serviceregistry()
{
  touch log/serviceRegistry.log
  ./serviceRegistry/bin/nuxeoctl console > log/serviceRegistry.log 2>&1
}

dbbProxy()
{
  touch log/dbbProxy.log
  cd dbbProxy
  chmod +x ./start-web-proxy.sh
  ./start-web-proxy.sh > ../log/dbbProxy.log 2>&1
}

esperproxy()
{
  touch log/esperProxy.log
  cd frascati
  ./start-esperProxy.sh > ../log/esperProxy.log 2>&1
}

lightproxypaf()
{
  touch log/lightProxyPaf.log
  cd frascati
  ./start-lightProxyPaf.sh > ../log/lightProxyPaf.log 2>&1
}

lightproxytravel()
{
  touch log/lightProxyTravel.log
  cd frascati
  ./start-lightProxyTravel.sh > ../log/lightProxyTravel.log 2>&1
}


web()
{
  touch log/web.log
  cd web
  chmod +x ./start-web-server.sh
  ./start-web-server.sh > ../log/web.log 2>&1
}

pafservices()
{
  touch log/pafServices.log
  cd pafServices
  ./start-pafServices.sh > ../log/pafServices.log 2>&1
}

travelbackup()
{
  touch log/travelBackup.log
  cd travelBackup
  ./start-travelBackup.sh > ../log/travelBackup.log 2>&1
}

traveldemo()
{
  touch log/travelDemo.log
  cd frascati
  ./start-travelDemo.sh > ../log/travelDemo.log 2>&1
}

# Start processes
echo "Starting EasySOA Demo. A browser page will be opened in a few seconds."
echo "Note that the service registry will take between 30s and 2mn to launch."

# FIXME The script uses delays to solve dependencies issues,
# it might not be enough on lower-end computers

serviceregistry &
dbbProxy &
esperproxy &
pafservices &
travelbackup &
sleep 3 # Let the servers start
traveldemo &
sleep 7 # Let the demo start
web &
lightproxypaf &
lightproxytravel &
sleep 2
firefox "http://127.0.0.1:8083/easysoa" &

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

