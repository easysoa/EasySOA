# Pandoc

## Installing

On Ubuntu:

    sudo apt-get install pandoc texlive-latex-extra

Otherwise, see [Installing Pandoc](http://johnmacfarlane.net/pandoc/installing.html)


## Getting started

The `pdf.sh` script converts any Markdown document to a PDF, using a custom template and custom stylesheets. Try:

`./pdf.sh README.md` or `./pdf.sh README.md README.pdf`

If you put files in the `./in` folder, running `./pdf.sh` (without any parameters) will convert each file it contains. The `./html.sh` script works exactly the same way.

* The first title level (#) should only be used for the document title (just like in this Readme).
* If your text links to images, their path must be relative to the `markup/` folder.

## How it works: Basic Pandoc use

Converting a Markdown file to HTML (works with other formats too):

    pandoc -s MARKDOWN_SOURCE -o HTML_OUTPUT

Converting an HTML file to PDF:

    wkhtmltopdf HTML_SOURCE PDF_OUTPUT

For more documentation, see the `pdf.sh` sources and the [Pandoc User’s Guide](http://johnmacfarlane.net/pandoc/README.html)
