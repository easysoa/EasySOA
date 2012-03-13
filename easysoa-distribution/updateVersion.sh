#!/bin/bash

# Updates all Maven projects' versions

# HOW TO
# - Pom update: ./updateVersion.sh 0.4-SNAPSHOT
# - Backups cleanup: ./updateVersion.sh clean 
#   (removes all created .versionsBackup files)

# WARNINGS
# - The script must be launched from the EasySOA/easysoa-distribution folder
# - Shell/Batch files still have to be manually updated. They can be found in:
#    /easysoa-distribution/packaging-files/
#    /samples/easysoa-samples-pureairflowers/easysoa-samples-paf-server/etc/

LINE="----------------------------------------------------"

# Catch interrupt signal
shutdown() {
	echo ""; echo "Aborting."
	ABORT=1
}
trap shutdown SIGINT SIGTERM

# Backups cleanup
if [ "$1" == "clean" ]; then

	# Remove all .versionsBackup files
	cd ..
	echo $LINE; echo "Removing all pom backups"; echo $LINE
	find ./ -type f -name \*.versionsBackup -print -delete
	echo "Done."
	cd "easysoa-distribution"
	
# Pom update
else

	# Clean repository
	rm -r easysoa/
	cd ..
	mvn clean
	
	# Init
	NEW_VERSION=$1
	ROOT_DIR="$( cd "$( dirname "$0" )" && pwd )"

	# Update pom function
	# (takes a pom path as parameter)
	update_pom() {
		pom=$1
		cd ${pom%pom.xml}
		mvn versions:set -DnewVersion=$NEW_VERSION
		cd $ROOT_DIR
	}

	# Pom search and update
	progress=1
	total=`find -name pom.xml | wc -l`
	for pom in `find -name pom.xml`; do
		echo ""; echo "Updating project $progress / $total..."; echo ""
		update_pom $pom
		progress=$(expr $progress + 1)
		if [ $ABORT == 1 ]; then
			exit 0
		fi
	done
	
	# End
	cd "$ROOT_DIR/easysoa-distribution"

fi
