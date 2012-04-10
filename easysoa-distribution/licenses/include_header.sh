#! /bin/bash

ROOT_PATH=$1
FILE_PATTERN=$2
HEADER_TEMPLATE=$3
PROJECT_NAME=$4
FIND_OPTIONS=$5

HEADER_FILE=header_file.txt
sed -e s/PROJECT_NAME/"$PROJECT_NAME"/ $HEADER_TEMPLATE > $HEADER_FILE

find $ROOT_PATH -name "$FILE_PATTERN" $FIND_OPTIONS -print | xargs grep --files-without-match "googlegroups" > files

echo About to apply header to `wc -l files` like \"$FILE_PATTERN\" below $ROOT_PATH :
cat $HEADER_FILE
cat files
echo If not OK, hit CTRL-C

read OK
#exit

echo Applying...

for i in `cat files`; do
   echo $i
   mv $i "$i.tmp"
   cp $HEADER_FILE $i
   cat "$i.tmp" >> $i
   rm -f "$i.tmp"
done

rm -f files $HEADER_FILE

