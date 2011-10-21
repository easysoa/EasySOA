#!/bin/bash

# Converts an OGV file to an AVI for easier editing.
	
if [[ -z "$1" || "$1" == "--help" ]]; then
	echo "Use: ./ogvToAvi.sh [PATH_TO_OGV]"
else

	IN_PATH=${1%/*}
	IN_FILE=${1##*/}
	IN_BASE=${IN_FILE%%.*}
	ffmpeg -an -b 3000kb -i $1 $IN_PATH/$IN_BASE.avi

fi
