#!/bin/bash

# $1 = Github user
# $2 = Repository name
function pullRepo {
  puts "Cloning $2..."
  git clone "https://github.com/$1/$2"
}

mkdir -p easysoa-workspace
cd easysoa-workspace

pullRepo "easysoa" "easysoa-demo-dist"
pullRepo "easysoa" "easysoa-demo-pureAirFlowers-proxy"
pullRepo "easysoa" "easysoa-model-demo"
pullRepo "easysoa" "esper-frascati-poc"
pullRepo "easysoa" "EasySOADemoTravel"

puts "Sources downloaded. Please open easysoa-demo-dist/build.yaml to configure it (especially your Nuxeo path),"
puts "then move to the easysoa-demo-dist folder and run 'buildr buildall packageall'."
