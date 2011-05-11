#!/bin/bash

# $1 = Github user
# $2 = Repository name
function pullRepo {
  puts "Pulling $2..."
  mkdir -p $2
  cd $2
  git init
  git pull "https://github.com/$1/$2"
  cd ..
}

mkdir -p workspace
cd workspace

pullRepo "JGuillemotte" "easysoa-demo-pureAirFlowers-proxy"
pullRepo "mkalam-alami" "easysoa-model-demo"
pullRepo "mkalam-alami" "easysoa-demo-dist"

puts "Sources downloaded. Please open easysoa-demo-dist/buildfile to configure it (especially your Nuxeo path),"
puts "then go to easysoa-demo-dist and run 'buildr buildall packageall'"
