# EasySOA Demo Distribution (not updated for the latest version yet)

Project for EasySOA contributors, used to tie all packages together.

# Prerequisites

Instructions to setup these applications (including Git) are detailed on [this page](https://github.com/easysoa/easysoa-model-demo/wiki/Development-Environment).

## For building

* Sun's 1.6 Java JDK
* Maven 2
* [Buildr][1]
* [Nuxeo DM][4] (with Derby as a database)

## For running

* Sun's 1.6 Java JDK
* [Node.js][5]

# Set up

You will need to clone all needed projects:

* [easysoa-model-demo][2]
* [easysoa-demo-pureAirFlowers-proxy][3]

Once this is done, clone this repository in your workspace, on the same level than the other projects.

# Build and distribute

Modify the `build.yaml` file constants according to your config (especially the Nuxeo path, since Nuxeo is not included in our repositories). Then, run the following command, from the `easysoa-demo-dist` folder:

    buildr buildall packageall tgz
    
# Build and run

Modify the `build.yaml` file constants according to your config, then run the following command, from the `easysoa-demo-dist` folder:

    buildr buildall packageall
    
To launch EasySOA, type these commands:

    cd easysoa
    ./run.sh

# Task list

* `buildr buildall` builds all needed projects
* `buildr packageall` creates an easysoa folder including all components
* `buildr tgz` creates a tar.gz from the easysoa package

* `buildr nx_mvn` builds all Nuxeo bundles
* `buildr nx_dist` deploys Nuxeo bundles to the Nuxeo plugins folder
* `buildr nx_git` deploys Nuxeo bundles to the git build folder
* `buildr nx_clean` cleans all Nuxeo bundles
* `buildr paf_mvn` builds PAF CXF server and service proxy

[1]: http://buildr.apache.org/
[2]: https://github.com/easysoa/easysoa-model-demo
[3]: https://github.com/easysoa/easysoa-demo-pureAirFlowers-proxy
[4]: http://www.nuxeo.com/en/products/document-management
[5]: http://nodejs.org/
