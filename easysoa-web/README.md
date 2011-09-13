# About this web server

This web server contains various web components used by EasySOA. The server is run using [node.js][2] and the [Connect][2] framework. EasySOA contents are under the `www` folder:

* `intranet` = A fake website where the PureAirFlowers services can be found "by browsing".
* `easysoa/core` = The discovery by browsing client, intended to be used with the web proxy launched with `/discovery-by-browsing/start-proxy.sh`.
* `easysoa/light` = Some pre-generated forms to use EasySOA services through the Light proxies.

[1]: http://nodejs.org/
[2]: http://senchalabs.github.com/connect/
