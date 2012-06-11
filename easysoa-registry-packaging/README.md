# EasySOA Registry packaging project

This POM project gathers all of the resources needed to turn any Nuxeo DM setup into the core of EasySOA, i.e. the service registry.

## Contents

This packaging embeds:

* Additions to Nuxeo DM (= bundles) to implement the EasySOA model, make the UI fit our needs, but also:
  * Provide a custom REST API
  * Enable some features such as service validation, deployables management, etc.
* Add support for an embedded FraSCAti instance within Nuxeo
* Add some EasySOA applications to be started on FraSCAti:
  * [HTTP Discovery proxy](https://github.com/easysoa/EasySOA/tree/master/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy)
  * [Client scaffolder](https://github.com/easysoa/EasySOA/tree/master/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-scaffolderproxy)
  * [Smart Travel application](https://github.com/easysoa/EasySOA/tree/master/samples/easysoa-samples-smarttravel/easysoa-samples-smarttravel-trip)
  
## Usage

* `mvn clean install`
* Copy the `./target` contents to your Nuxeo DM 5.5 setup
* Launch Nuxeo!

## POM behavior

* Plugin `maven-dependency-plugin` > ``<id>copy-bundle-dependencies</id>`: packages the Nuxeo bundles, by gathering them at `target/nxserver/plugins` (they must be listed one by one)
* Plugin `maven-dependency-plugin` > ``<id>copy-lib-dependencies</id>`: packages the additional Nuxeo libraries, by gathering them at `target/lib` (they must be listed one by one)
* Plugin `maven-resources-plugin` > `<id>copy-frascati-resources</id>`: packages the FraSCAti engine at `target/nxserver/frascati`
* Plugin `maven-dependency-plugin` > `<id>copy-registry-frascati</id>`: more `target/lib` libraries
* Plugin `maven-antrun-plugin`: packages the applications to be started by FraSCAti, by unzipping them at `target/nxserver/frascati/apps`
