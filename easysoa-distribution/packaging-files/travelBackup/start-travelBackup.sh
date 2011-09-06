#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Travel services backup"
echo "(Deployed on http://localhost:9020)"
echo $LINE

java -jar easysoa-samples-smarttravel-services-backup-1.0-SNAPSHOT.jar http://localhost:9020/
