#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Fake Meteo service "
echo "(Deployed on http://localhost:9020/WeatherService)"
echo $LINE

./bin/frascati run meteo.composite -libpath ./sca-apps/easysoa-meteo-sca-backup-1.0-SNAPSHOT.jar
