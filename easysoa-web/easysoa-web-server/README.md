# About this web server

This web server contains various web components used by EasySOA. They are managed by [Antinode][1], a static file webserver built on node.js. EasySOA contents are under the `www` folder:

* `intranet` = A fake website where the PureAirFlowers services can be found "by browsing".
* `easysoa/core` = The discovery by browsing client, intended to be used with the web proxy launched with `/discovery-by-browsing/start-proxy.sh`.
* `easysoa/light` = Some pre-generated forms to use EasySOA services through the Light proxies.

[1]: https://github.com/mhansen/antinode
