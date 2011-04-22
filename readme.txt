A few node.js scripts for easysoa (see https://github.com/easysoa) developers : tools, mocks, samples.


General information :
Still heavily beta.
Configuration is done by properties at the start of the script, so no command line parameters.


tools :
proxyDump.js - Tool for gathering data about HTTP exchanges, in order to help designing the easysoa model.

mocks :
mocks/proxyFuse.js - Mock of a "fuse" behaviour in the easysoa service proxy layer.
mocks/discoverByBrowsing.js - Non working mock of the "pattern" strategy of the "service discovery by browsing" easysoa scenario.
mocks/discoverByListening.js - yet to be done.

samples - nuxeo clients :
samples/nuxeo/nuxeoAutomationApi.js - Returns as JSON the Content Automation API of a Nuxeo server.
samples/nuxeo/nuxeoAutomationQuery[Fetch[Children]][_proxied].js - A few sample Content Automation operations on a Nuxeo server.
samples/nuxeo/nuxeoQuery.js - Does a Nuxeo query using the Site Admin API.


Installing node.js :
on Windows, see https://github.com/ry/node/wiki/Building-node.js-on-Cygwin-%28Windows%29
on Linux, see https://github.com/joyent/node/wiki/Installation


Licensing :
MIT license
(c) 2011 Open Wide SAS - Marc Dutoo
