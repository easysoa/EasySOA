Project to test together Nuxeo model, http Proxy and Galaxy Demo.

There is 3 way to launch the tests :

1) Nuxeo model, http proxy and Galaxy demo are launched separately.

Execute the following actions in the order :
- On the class NuxeoTestStarter, click "RunAs JUnit test".
- On the class HttpProxyTestStarter, click "RunAs JUnit test".
- On the class GalaxyDemoTestStarter, click "RunAs JUnit test".

2) Nuxeo model is launched separately, Http Proxy and Galaxy Demo are launched in the same FraSCAti engine.

Execute the following actions in the order :
- On the class NuxeoTestStarter, click "RunAs JUnit test".
- On the class FraSCAtiCompositeDemoTestStarter, click "RunAs JUnit test".

3) Nuxeo model, http proxy and Galaxy demo are launched in the same OSGI engine

Execute :
- On the class GalaxyDemoOSGITest, click "RunAs JUnit test".