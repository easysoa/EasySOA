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
if [ $1 != 'local' ]; then
	echo "Downloading archive..."
	scp $EASYSOAUSER@$EASYSOAIP:$EASYSOAFILE $EASYSOADEST
else
	echo "Skipping archive download"
fi

# Extract
echo "Extracting files..."
cd ../easysoa
tar xf ../.tmp/$EASYSOADEST
echo "Done. Please use ~/Desktop/easysoa/run.sh to run the demo."
