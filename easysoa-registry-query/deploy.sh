NUXEO_INSTANCE_HOME="$HOME/.local/dev/real/nuxeo-5.5-HF09-easysoa/"

NUXEO_INSTANCE_PLUGINS_DIR="$NUXEO_INSTANCE_HOME/nxserver/plugins"

#UI="easysoa-registry-query-nuxeo-webengine"
UI="easysoa-registry-query-nuxeo-contentviews"
TESTMODEL="easysoa-registry-query-nuxeo-test-model"

if cp -t "$NUXEO_INSTANCE_PLUGINS_DIR" \
	"$UI/target"/*.jar "$TESTMODEL/target/"*.jar
then
	echo "OK"
	exit 0
else
	echo "Failure"
	exit 1
fi

