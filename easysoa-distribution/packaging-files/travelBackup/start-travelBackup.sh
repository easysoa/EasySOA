#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Travel services backup"
echo "(Deployed on http://localhost:9020)"
echo $LINE

cd bin
java -jar easysoa-samples-smarttravel-services-backup-0.5-SNAPSHOT.jar http://localhost:9020/
