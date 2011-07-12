#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Trip demo SOA"
echo "(Deployed on http://localhost:9000)"
echo $LINE

./bin/frascati run smart-travel-mock-services.composite -libpath ./sca-apps/trip-1.0-SNAPSHOT.jar
