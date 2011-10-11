#! /bin/bash

pushd ../..
./include_header.sh ../../easysoa-common "*.java" lgpl3_java_header.txt "EasySOA Common"
./include_header.sh ../../easysoa-registry "*.java" lgpl3_java_header.txt "EasySOA Registry"
./include_header.sh ../../easysoa-proxy "*.java" lgpl3_java_header.txt "EasySOA Proxy"
./include_header.sh ../../easysoa-web "*.java" mit_js_header.txt "EasySOA Web"
./include_header.sh ../../samples/easysoa-samples-pureairflowers "*.java" lgpl3_java_header.txt "EasySOA Samples - PureAirFlowers"
./include_header.sh ../../samples/easysoa-samples-smarttravel "*.java" lgpl3_java_header.txt "EasySOA Samples - Smart Travel"
#./include_header.sh ../../samples/Talend-Airport-Service "*.TODO" TODO_header.txt "EasySOA Samples - Talend Airport"
popd
