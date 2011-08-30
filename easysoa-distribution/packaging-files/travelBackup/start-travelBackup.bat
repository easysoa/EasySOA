@echo off
set LINE=----------------------------------------------------

echo %LINE%
echo Travel services backup
echo (Deployed on http://localhost:9020)
echo %LINE%

cd bin
java -jar travel-services-backup-1.0-SNAPSHOT.jar http://localhost:9020/
