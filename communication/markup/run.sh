#!/bin/bash

# Converts Markdown markup to PDF files
# @author Marwane Kalam-Alami

mkdir "in" -p
mkdir "out" -p

function mk2html2pdf {
  echo "=======" $1 ">" $2
  pandoc -s $1 -o out/tmp.html --toc --template config/template.html
  wkhtmltopdf out/tmp.html $2
}

if [[ $1 && $2 ]];
then
  mk2html2pdf $1 $2
else
  echo "Looking for Markdown files in the 'in' folder..."
  echo
  pattern="in"
  for input in in/*
  do
    mk2html2pdf $input out${input#$pattern}.pdf
  done
fi

rm -f out/tmp.html
