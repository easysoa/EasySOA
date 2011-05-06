Project for EasySOA contributors, used to tie all packages together.

# Prerequisites

* [Buildr][1] for building.
* Installed [Nuxeo DM][4] for EasySOA Core packing.

# Set up

You will need to clone all needed projects:

* [mkalam-alami/easysoa-model-demo][2]
* [JGuillemotte/easysoa-demo-pureAirFlowers-proxy][3]

Once this is done, clone this repository in your workspace, on the same level than the other projects.

# Build

Modify the `buildfile` file constants according to your config, then run the following command, from the `easysoa-demo-dist` folder:

    buildr buildall tgz

# Task list

* `buildr buildall` builds all needed projects
* `buildr tgz` creates a tar.gz containing the discovery-by-browsing tool + the Nuxeo installation

* `buildr nx_mvn` builds all Nuxeo bundles
* `buildr nx_dist` deploys Nuxeo bundles to the Nuxeo plugins folder
* `buildr nx_git` deploys Nuxeo bundles to the git build folder
* `buildr nx_clean` cleans all Nuxeo bundles
* `buildr paf_mvn` builds PAF CXF server and service proxy

[1]: http://buildr.apache.org/
[2]: https://github.com/mkalam-alami/easysoa-model-demo
[3]: https://github.com/JGuillemotte/easysoa-demo-pureAirFlowers-proxy
[4]: http://www.nuxeo.com/en/products/document-management
