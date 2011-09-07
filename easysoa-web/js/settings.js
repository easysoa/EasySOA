var settings = {
    
    // The port of the HTTP web & proxy servers
    webPort: "8083",
    proxyPort: "8081",
    
    // Web root folder
    webRoot : "../www",
    
    // Scrapers to be used to find WSDLs
    scrapers: [
      "http://127.0.0.1:8080/nuxeo/site/easysoa/wsdlscraper/?" // Nuxeo
    ],
    
    // Paths to ignore during scraping
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
    ],
    
    // Address of the REST services on which to send notifications
    nuxeoNotification: "http://127.0.0.1:8080/nuxeo/site/easysoa/notification/"

};
