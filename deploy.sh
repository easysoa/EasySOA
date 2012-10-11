#!/bin/sh

# CHANGE THE FOLLOWING
export NUXEO_HOME=/cygdrive/c/dev/easysoa/nuxeo-cap-5.5-tomcat
export M2_REPO=/cygdrive/c/Documents\ and\ Settings/mdutoo/.m2/repository

# deploying built & dep jars
cp easysoa-registry-v1/*/target/*jar $NUXEO_HOME/nxserver/plugins/
cp "$M2_REPO/net/sf/jung/jung-api/2.0.1/jung-api-2.0.1.jar" "$NUXEO_HOME/lib"
cp "$M2_REPO/net/sf/jung/jung-graph-impl/2.0.1/jung-graph-impl-2.0.1.jar" "$NUXEO_HOME/lib"
cp "$M2_REPO/net/sf/jung/jung-algorithms/2.0.1/jung-algorithms-2.0.1.jar" $NUXEO_HOME/lib/
cp "$M2_REPO/net/sourceforge/collections/collections-generic/4.01/collections-generic-4.01.jar" "$NUXEO_HOME/lib"
