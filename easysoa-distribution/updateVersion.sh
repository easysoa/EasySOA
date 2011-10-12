#!/bin/bash

# Updates all Maven projects' versions
# WARNING: Must be launched from the EasySOA/easysoa-distribution folder

# Example: ./updateVersion.sh 0.3

NEW_VERSION=$1

rm -rf easysoa/
cd ..
ROOT_DIR="$( cd "$( dirname "$0" )" && pwd )"

update_pom() {
	pom=$1
	cd ${pom%pom.xml}
	mvn versions:set -DnewVersion=$NEW_VERSION
	cd $ROOT_DIR
}

for pom in `find -name pom.xml`; do
	update_pom $pom
done
cd "$ROOT_DIR/easysoa-distribution"
