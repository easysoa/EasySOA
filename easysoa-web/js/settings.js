var settings = {
    
    // Port of the HTTP web & proxy servers
    webPort: "8083",
    proxyPort: "8081",
    
    // Port of the EasySOA Light UI scaffolding server
    scaffoldingServer: "http://127.0.0.1:8090",
    
    // Web root folder
    webRoot : "../www",
    
    // Scrapers to be used to find WSDLs
    scrapers: [
      "http://127.0.0.1:8080/nuxeo/site/easysoa/servicefinder/?" // Nuxeo
    ],
    
    // Paths to ignore during scraping
    ignore: [
      ".css",
      ".jpg",
      ".gif",
      ".png",
      ".js",
      ".ico",
      ":8080/nuxeo", // Nuxeo (part of EasySOA Core)
      ":7001", // FraSCAti (part of EasySOA Light)
    ],

    // Address of the EasySOA rest services
    nuxeoEasySOARest: "http://127.0.0.1:8080/nuxeo/site/easysoa",
        
    // Address of the REST services on which to send notifications
    nuxeoDiscovery: "http://127.0.0.1:8080/nuxeo/site/easysoa/discovery/",
    
    // Address of the automation services, to test Nuxeo status and the user login
    nuxeoAutomation: "http://127.0.0.1:8080/nuxeo/site/automation"

};
