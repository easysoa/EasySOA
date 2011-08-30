#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Trip demo SOA"
echo "(Deployed on http://localhost:9000)"
echo "DEPENDENCY: Running services backup"
echo $LINE

export CUSTOM_JAVA_OPTS=-Dcxf.config.file=cxfEsperProxy.xml
./bin/frascati-easysoa run smart-travel-mock-services.composite -libpath ./sca-apps/trip-1.0-SNAPSHOT.jar ./sca-apps/summary-model-1.0-SNAPSHOT.jar

#PS: The summary-model library has been moved out from the lib/ folder due to a classloader conflict with the EasySOA Light trip proxy.
