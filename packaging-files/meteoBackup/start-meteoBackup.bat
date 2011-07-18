@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo Meteo service backup
echo (Deployed on http://localhost:9020)
echo %LINE%

cd bin
java -jar easysoa-meteo-sca-backup-1.0-SNAPSHOT.jar http://localhost:9020/WeatherService