@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo PureAirFlowers services
echo (Deployed on http://localhost:9010)
echo %LINE%

java -jar easysoa-samples-paf-server-1.0-SNAPSHOT-jar-with-dependencies.jar
