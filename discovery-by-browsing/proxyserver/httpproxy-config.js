var config = {

    // The port of the HTTP proxy server
    proxy_port: "8081",
    
    // Scrapers to be used to find WSDLs
    scrapers: [
      "http://127.0.0.1:8080/nuxeo/restAPI/wsdlscraper/?", // Nuxeo
      //"http://localhost:8082/url=?" // myscraper.js
    ],
    
    // Paths to ignore
    ignore: [
      ".css",
      ".jpg",
      ".gif",
      ".png",
      ".js",
      ".ico",
      ":8083/client", // Discovery by browsing client
      ":8080/nuxeo", // Nuxeo
      ":8081", // The proxy itself
    ]
    
};
