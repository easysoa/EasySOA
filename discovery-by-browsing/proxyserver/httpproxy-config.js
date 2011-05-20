var config = {

    // The port of the HTTP proxy server
    proxy_port: "8081",
    
    // Scrapers to be used to find WSDLs
    scrapers: [
      "http://127.0.0.1:8080/nuxeo/site/easysoa/wsdlscraper/?", // Nuxeo
    ],
    
    // Paths to ignore
    ignore: [
      ".css",
      ".jpg",
      ".gif",
      ".png",
      ".js",
      ".ico",
      ":8083/easysoa", // EasySOA website
      ":8080/nuxeo", // Nuxeo (part of EasySOA Core)
      ":7001", // FraSCAti (part of EasySOA Light)
      ":8081", // The web proxy itself
    ]
    
};
