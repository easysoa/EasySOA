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
start "EasySOA Demo - Web Proxy" start-web-proxy.bat > ..\log\dbbProxy.log 2>&1
cd ..

rem esper
cd frascati
start "EasySOA Demo - Esper Proxy" start-esperProxy.bat > ..\log\esperProxy.log 2>&1
cd ..

rem pafServices
cd pafServices
start "EasySOA Demo - Web Services" start-pafServices.bat > ..\log\pafServices.log 2>&1
cd ..

rem travelBackup
cd travelBackup
start "EasySOA Demo - Travel Services Backup" start-travelBackup.bat > ..\log\travelBackup.log 2>&1
cd ..

rem sleep 3 (let the servers start, see http://stackoverflow.com/questions/1672338/how-to-sleep-for-5-seconds-in-windowss-command-prompt-or-dos)
ping -n 4 127.0.0.1 > nul

rem travel
cd frascati
start "EasySOA Demo - Travel" start-travelDemo.bat > ..\log\travelDemo.log 2>&1
cd ..

rem sleep 7 (let the demo start)
ping -n 8 127.0.0.1 > nul

rem web
cd web
start "EasySOA Demo - Web" start-web-server.bat > ..\log\web.log 2>&1
cd ..

rem lightProxyPaf
cd frascati
start "EasySOA Demo - EasySOA Light Proxy (PAF)" start-lightProxyPaf.bat > ..\log\lightProxyPaf.log 2>&1
cd ..

rem lightProxyTravel
cd frascati
start "EasySOA Demo - EasySOA Light Proxy (Travel)" start-lightProxyTravel.bat > ..\log\lightProxyTravel.log 2>&1
cd ..

rem sleep 2
ping -n 3 127.0.0.1 > nul

call explorer "http://127.0.0.1:8083/easysoa"
