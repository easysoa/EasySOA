#!/bin/bash

clear

mkdir -p log
export PATH="$PATH;$PWD/node"

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
  chmod +x ./start-proxy.sh
  ./start-proxy.sh > ../log/dbbProxy.log 2>&1
}

esperproxy()
{
  touch log/esperProxy.log
  cd frascati
  ./start-esperProxy.sh > ../log/esperProxy.log 2>&1
}

lightproxy()
{
  touch log/lightProxy.log
  cd frascati
  ./start-lightProxy.sh > ../log/lightProxy.log 2>&1
}

web()
{
  touch log/web.log
  cd web
  chmod +x ./start-web.sh
  ./start-web.sh > ../log/web.log 2>&1
}

pafservices()
{
  touch log/pafServices.log
  cd pafServices
  ./start-pafServices.sh > ../log/pafServices.log 2>&1
}

meteobackup()
{
  touch log/meteoBackup.log
  cd meteoBackup
  ./start-meteoBackup.sh > ../log/meteoBackup.log 2>&1
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
sleep 5 # Let the servers start
web &
lightproxy &
meteobackup &
sleep 2 # Let the web server launch
traveldemo &
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

