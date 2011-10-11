#! /bin/bash

FILE_PATTERN=$1
HEADER_FILE=$2

find . -name \"$FILE_PATTERN\" -print > files
echo find a -name \"$FILE_PATTERN\" -print

echo About to apply header $HEADER_FILE to `wc -l files` like \"$FILE_PATTERN\" :
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

rm -f files

