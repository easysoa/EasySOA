@echo off


IF NOT EXIST log mkdir log


rem Start processes
echo Starting EasySOA Demo. A browser page will be opened in a few seconds.
echo Note that the service registry will take between 30s and 2mn to launch.


rem serviceregistry
chdir serviceregistry\bin
start "EasySOA Demo - Service Registry" "Start Nuxeo.bat" > ..\..\log\serviceregistry.log 2>&1
chdir ..\..

rem web
cd web
start "EasySOA Demo - Web" start-web.bat > ..\log\web.log 2>&1
cd ..

rem webproxy
cd webproxy
start "EasySOA Demo - Web Proxy" start-proxy.bat > ..\log\webproxy.log 2>&1
cd ..

rem webservices
cd webservices
start "EasySOA Demo - Web Services" start_cxf_server.bat > ..\log\webservices.log 2>&1
cd ..

rem webservicesproxy
cd webservices
start "EasySOA Demo - Service Proxy" start_frascati_proxy.bat > ..\log\webservicesproxy.log 2>&1
cd ..

call explorer "http://localhost:8083/easysoa"

rem travel
cd travel/trip
start "EasySOA Demo - Travel" mvn -Prun > ..\log\travel.log 2>&1
cd ..
