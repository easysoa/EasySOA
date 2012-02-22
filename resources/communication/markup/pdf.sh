#!/bin/bash

# Converts Markdown markup to PDF files
# @author Marwane Kalam-Alami

# Use 1: ./pdf.sh MARKDOWN_INPUT
# Converts the file from given path to a PDF of the same name at the same folder. 

# Use 2: ./pdf.sh MARKDOWN_INPUT PDF_OUTPUT
# Converts the file from given path to a specific PDF path.

# Use 3: ./pdf.sh
# Converts all files from the in/ folder to PDFs of the same name in the out/ folder.


mkdir "in" -p
mkdir "out" -p

function mk2html2pdf {
  echo "=======" $1 ">" $2
  pandoc -s $1 -o .tmp.html --toc --template resources/template.html
  wkhtmltopdf .tmp.html $2
}

if [[ $1 ]];
then
  if [[ $2 ]];
  then
    mk2html2pdf $1 $2
  else
    mk2html2pdf $1 $1.pdf
  fi
else
  echo "Looking for Markdown files in the 'in' folder..."
  pattern="in"
  for input in in/*
  do
    if [[ !($input == in/*~) ]]
    then
      mk2html2pdf $input out${input#$pattern}.pdf
    fi
  done
fi

rm -f .tmp.html
