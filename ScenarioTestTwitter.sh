#!/bin/bash
# 
# This script launch curl with twitter REST API URL. To test the proxy.

curl -x localhost:8082 http://api.twitter.com/1/users/show/FR3Aquitaine.xml
curl -x localhost:8082 http://api.twitter.com/1/users/show/EasySoaTest.xml
curl -x localhost:8082 http://api.twitter.com/1/users/show/FR3Aquitaine.json
curl -x localhost:8082 http://api.twitter.com/1/users/show/FR3Bourgogne.xml
curl -x localhost:8082 http://api.twitter.com/1/users/show/FR3Bourgogne.json
curl -x localhost:8082 http://api.twitter.com/1/users/show/truckblogfr.xml
curl -x localhost:8082 http://api.twitter.com/1/users/show/OliverTweett.xml
curl -x localhost:8082 http://api.twitter.com/1/users/show/Developpez.xml
curl -x localhost:8082 http://api.twitter.com/1/users/show/europe_camions.xml
curl -x localhost:8082 http://api.twitter.com/1/users/show/OliverTweett.json
curl -x localhost:8082 http://api.twitter.com/1/users/show/FR3Aquitaine.xml

curl -x localhost:8082 http://api.twitter.com/1/statuses/friends/oliverTweett.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/friends/FR3Aquitaine.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/friends/europe_camions.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/friends/Developpez.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/friends/FR3Bourgogne.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/friends/EasySoaTest.xml?cursor=-1

curl -x localhost:8082 http://api.twitter.com/1/statuses/followers/europe_camions.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/followers/Developpez.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/followers/FR3Aquitaine.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/followers/oliverTweett.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/followers/Developpez.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/followers/FR3Bourgogne.xml?cursor=-1
curl -x localhost:8082 http://api.twitter.com/1/statuses/followers/EasySoaTest.xml?cursor=-1
