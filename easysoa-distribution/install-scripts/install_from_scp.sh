#!/bin/bash

# Configuration
EASYSOAUSER=mkalam-alami
EASYSOAIP=192.168.2.217
EASYSOAFILE=/home/mkalam-alami/workspace/easysoa-demo-dist/easysoa-demo-1.0-SNAPSHOT.tar.gz

# Set up environment
EASYSOADEST=easysoa-demo.tar.gz
rm -rf easysoa
mkdir -p easysoa
mkdir -p .tmp

# Download archive
cd .tmp
echo "Downloading archive..."
scp $EASYSOAUSER@$EASYSOAIP:$EASYSOAFILE $EASYSOADEST
	
# Extract
echo "Extracting files..."
cd ../easysoa
tar xf ../.tmp/$EASYSOADEST
echo "Done. Please use ./run.sh from the easysoa folder to run the demo."
