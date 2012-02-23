#!/bin/bash

# Converts Markdown markup to HTML files
# @author Marwane Kalam-Alami

# Use 1: ./html.sh MARKDOWN_INPUT
# Converts the file from given path to a HTML of the same name at the same folder. 

# Use 2: ./html.sh MARKDOWN_INPUT HTML_OUTPUT
# Converts the file from given path to a specific HTML path.

# Use 3: ./html.sh
# Converts all files from the in/ folder to PDFs of the same name in the out/ folder.


mkdir "in" -p
mkdir "out" -p

function mk2html {
  echo "=======" $1 ">" $2
  pandoc -s $1 -o $2 --toc --template resources/template.html
}

if [[ $1 ]];
then
  if [[ $2 ]];
  then
    mk2html $1 $2
  else
    mk2html $1 $1.html
  fi
else
  echo "Looking for Markdown files in the 'in' folder..."
  pattern="in"
  for input in in/*
  do
    if [[ !($input == in/*~) ]]
    then
      mk2html $input out${input#$pattern}.html
    fi
  done
fi

cp -r resources out
rm out/resources/template.html
