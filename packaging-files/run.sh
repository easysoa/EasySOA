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
  ./start-web.sh > ../log/web.log 2>&1
}

webproxy()
{
  touch log/webproxy.log
  cd webproxy
  ./start-proxy.sh > ../log/webproxy.log 2>&1
}

webservices()
{
  touch log/webservices.log
  java -jar `find ./webservices -name *.jar` > log/webservices.log 2>&1
}

webservicesproxy()
{
  touch log/webservicesproxy.log
  cd ./webproxy
  ./mvn -Prun > ../log/webservicesproxy.log 2>&1
}

# Start processes
echo "Starting EasySOA Demo."
serviceregistry &
#serviceregistrypid=$!
web &
webproxy &
webservices &
webservicesproxy &

echo "Press any key to stop."

firefox "http://localhost:8083/easysoa"

# Prepare to interrupt all running processes
shutdown()
{
  echo "Stopping all servers."
  ps | awk 'NR>1 { print $1 }' | xargs kill > /dev/null 2>&1
}

trap shutdown SIGINT SIGTERM

# Wait for a key to be pressed
read -n 1 -s
shutdown

