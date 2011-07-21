@echo off

IF NOT EXIST log mkdir log
PATH=%PATH%;%CD%\node

rem Start processes
echo Starting EasySOA Demo. A browser page will be opened in a few seconds.
echo Note that the service registry will take between 30s and 2mn to launch.

rem serviceRegistry
cd serviceRegistry\bin
start "EasySOA Demo - Service Registry" "Start Nuxeo.bat" > ..\..\log\serviceRegistry.log 2>&1
cd ..\..

rem dbbPproxy
cd dbbProxy
start "EasySOA Demo - Web Proxy" start-dbbProxy.bat > ..\log\dbbProxy.log 2>&1
cd ..

rem esper
cd frascati
start "EasySOA Demo - Esper Proxy" start-esperProxy.bat > ..\log\esperProxy.log 2>&1
cd ..

rem pafServices
cd pafServices
start "EasySOA Demo - Web Services" start-pafServices.bat > ..\log\pafServices.log 2>&1
cd ..

sleep 5

rem web
cd web
start "EasySOA Demo - Web" start-web.bat > ..\log\web.log 2>&1
cd ..

rem lightProxy
cd frascati
start "EasySOA Demo - EasySOA Light Proxy" start-lightProxy.bat > ..\log\lightProxy.log 2>&1
cd ..

rem meteoBackup
cd meteoBackup
start "EasySOA Demo - Meteo Service Backup" start-meteoBackup.bat > ..\log\meteoBackup.log 2>&1
cd ..

sleep 2

rem travel
cd frascati
start "EasySOA Demo - Travel" start-travelDemo.bat > ..\log\travelDemo.log 2>&1
cd ..

call explorer "http://127.0.0.1:8083/easysoa"
