#!/bin/bash

NUXEO_LIB_DIR=/home/mdutoo/dev/easysoa/nuxeo-dm-5.4.1-tomcat/nxserver/lib
FRASCATI_LIB_DIR=/home/mdutoo/dev/easysoa/easysoa-demo-1.0/webservices/frascati-proxy/lib

ls $NUXEO_LIB_DIR > nuxeo_libs.txt
sed -e 's/-[^-]*\.jar$//' nuxeo_libs.txt > nuxeo_libs_nov.txt
ls $FRASCATI_LIB_DIR > frascati_libs.txt
sed -e 's/-[^-]*\.jar$//' frascati_libs.txt > frascati_libs_nov.txt

echo "" > n_in_f.txt
for i in `cat nuxeo_libs_nov.txt`
do
	grep $i frascati_libs_nov.txt >> n_in_f.txt
done
echo "There are `wc -l n_in_f.txt` nuxeo libs among frascati's"

echo "" > f_in_n.txt
for i in `cat frascati_libs_nov.txt`
do
        grep $i nuxeo_libs_nov.txt >> f_in_n.txt
done
echo "There are `wc -l f_in_n.txt` frascati libs among nuxeo's"

rm nuxeo_libs.txt nuxeo_libs_nov.txt frascati_libs.txt frascati_libs_nov.txt
