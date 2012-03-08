A few node.js scripts for EasySOA (see https://github.com/easysoa) developers : tools, mocks, samples.

## General information

Still heavily beta.
Configuration is done by properties at the start of the script, so no command line parameters.

### Installing node.js

- On Linux : Install out of the box by building the sources
- On Windows : Pre-built binaries available

See https://github.com/joyent/node/wiki/Installation

### Licensing

MIT license
(c) 2011 Open Wide SAS - Marc Dutoo, Marwane Kalam-Alami

## Tools
* proxyDump.js - Tool for gathering data about HTTP exchanges, in order to help designing the easysoa model.

## Mocks
* mocks/proxyFuse.js - Mock of a "fuse" behaviour in the easysoa service proxy layer.
* mocks/discoverByBrowsing.js - Non working mock of the "pattern" strategy of the "service discovery by browsing" easysoa scenario.
* mocks/discoverByListening.js - yet to be done.
* mocks/unifiedapi - Iterative modeling of an unified EasySOA API through various scenarii.

## Samples - Nuxeo clients
* samples/nuxeo/nuxeoAutomationApi.js - Returns as JSON the Content Automation API of a Nuxeo server.
* samples/nuxeo/nuxeoAutomationQuery[Fetch[Children]][_proxied].js - A few sample Content Automation operations on a Nuxeo server.
* samples/nuxeo/nuxeoQuery.js - Does a Nuxeo query using the Site Admin API.
