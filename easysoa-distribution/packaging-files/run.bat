@echo off

IF NOT EXIST log mkdir log
PATH=%PATH%;%CD%\node

rem Start processes
echo Starting EasySOA Demo. A browser page will be opened in a few seconds.
echo Note that the service registry will take between 30s and 2mn to launch.

rem serviceRegistry
cd serviceRegistry\bin
start "EasySOA Demo - Service Registry" "Start Nuxeo.bat" 2>&1 ^| tee.exe ..\..\log\serviceRegistry.log
cd ..\..

rem esper
cd frascati
start "EasySOA Demo - Esper Proxy" start-esperProxy.bat 2>&1 ^| "../tee.exe" ..\log\esperProxy.log
cd ..

rem pafServices
cd pafServices
start "EasySOA Demo - Web Services" start-pafServices.bat 2>&1 ^| "../tee.exe" ..\log\pafServices.log
cd ..

rem travelBackup
cd travelBackup
start "EasySOA Demo - Travel Services Backup" start-travelBackup.bat 2>&1 ^| "../tee.exe" ..\log\travelBackup.log
cd ..

rem dbbProxy
cd dbbProxy
start "EasySOA Demo - Web Proxy" start-dbbProxy.bat 2>&1 ^| "../tee.exe" ..\log\dbbProxy.log
cd ..

rem sleep 3 (let the servers start, see http://stackoverflow.com/questions/1672338/how-to-sleep-for-5-seconds-in-windowss-command-prompt-or-dos)
ping -n 4 127.0.0.1 > nul

rem travel
cd frascati
start "EasySOA Demo - Travel" start-travelDemo.bat 2>&1 ^| "../tee.exe" ..\log\travelDemo.log
cd ..

rem sleep 7 (let the demo start)
ping -n 8 127.0.0.1 > nul

rem web
cd web
start "EasySOA Demo - Web Server" start-web.bat 2>&1 ^| "../tee.exe" ..\log\web.log
cd ..

rem lightProxyPaf
cd frascati
start "EasySOA Demo - EasySOA Light Proxy (PAF)" start-lightProxyPaf.bat 2>&1 ^| "../tee.exe" ..\log\lightProxyPaf.log
cd ..

rem lightProxyTravel
cd frascati
start "EasySOA Demo - EasySOA Light Proxy (Smart Travel)" start-lightProxyTravel.bat 2>&1 ^| "../tee.exe" ..\log\lightProxyTravel.log
cd ..

rem sleep 2
ping -n 3 127.0.0.1 > nul

call explorer "http://127.0.0.1:8083/easysoa"
