#!/bin/bash

echo "Extracting files..."
rm -rf easysoa
mkdir easysoa
cd easysoa
find .. -wholename easysoa*.tar.gz | xargs tar xf 
echo "Done. Please use ./run.sh from the easysoa folder to run the demo."
