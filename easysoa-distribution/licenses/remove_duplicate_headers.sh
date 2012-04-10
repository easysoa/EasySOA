#!/bin/bash

# Configuration

TARGET_PATH=$1
FILES_TO_TEST=$2
LINES_TO_REMOVE=$((`wc -l $3 | awk '{print $1}'` + 1))

# Script

FILES_TO_PATCH=.tmp_filelist
PATCHED=.tmp_patchedfile

find $TARGET_PATH -iname $FILES_TO_TEST | xargs pcregrep -Ml "googlegroups[^2]*2011" > $FILES_TO_PATCH

echo "=============> About to remove the first $LINES_TO_REMOVE first lines to:"
cat $FILES_TO_PATCH
echo
echo "=============> Patched example below - Does this looks like the top of the license header?"
cat $FILES_TO_PATCH | head -1 | xargs tail -n +$LINES_TO_REMOVE | head -n +5
echo "......"
echo "(If not OK, hit CTRL-C)"
read OK

for i in `cat $FILES_TO_PATCH`; do
   tail -n +$LINES_TO_REMOVE $i > $PATCHED
   rm $i
   mv $PATCHED $i
   echo "OK: $i"
done

rm -f $FILES_TO_PATCH
