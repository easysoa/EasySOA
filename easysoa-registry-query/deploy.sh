NUXEO_INSTANCE_HOME="$HOME/.local/dev/real/nuxeo-5.5-HF09-easysoa/"

NUXEO_INSTANCE_PLUGINS_DIR="$NUXEO_INSTANCE_HOME/nxserver/plugins"

#UI="easysoa-registry-query-nuxeo-webengine"
UI="easysoa-registry-query-nuxeo-contentviews"
TESTMODEL="easysoa-registry-query-nuxeo-test-model"

show_status () {
	echo 1>&2 "Deployment on existing Nuxeo instance
  * Path:	$NUXEO_INSTANCE_HOME
  * Result:	$1"
}

DEPLOY_MSG=
if cp -t "$NUXEO_INSTANCE_PLUGINS_DIR" \
	"$UI/target"/*.jar "$TESTMODEL/target/"*.jar
then
	show_status "OK"
	exit 0
else
	show_status "FAILED"
fi

