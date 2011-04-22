// Non working mock of the "pattern" strategy of the "service discovery by browsing" easysoa scenario.

// sample URLs to inspect ; later gotten from UI
var urls = [ "http://localhost:8080" ];

// patterns to handler mapa ; later use a isHandlerFor() + handle() interface
// TODO possible to share it on the web
// sample ones for platforms, components, solutions :
var patternToHandlers = {
  "/WS/": function(url) {
    // on .NET platform by default, this URL returns HTML linking to files
    // TODO get the *.asmx ones, get the wsdl url (add ?wsdl to the url)
    // TODO build the minimal service model and report it to the core
    var asmxUrl = url + "/WS/" + "InterfaceSvc.asmx"; // TODO
    var service = {wsdl: asmxUrl + "?wsdl"};
    reportService(service);
  },
  "/cxf": function(url) {
    // on Java CXF component by default, this URL returns HTML linking to WSDLs
    // TODO build the minimal service model and report it to the core
    var wsdlUrl = url +"/cxf" + "/DefinitionService?wsdl";
    var service = {wsdl: wsdlUrl};
    reportService(service);
  },
  "/service/index/all": function(url) {
    // on Alfresco solution by default, links to REST service with parameter templated urls
    // along with the method (just before) and other info (auth, tx, content type)
    // that could be parsed with a custom mapper
    // TODO possible to parse it with a custom mapper, and share it on the web
    // TODO build the minimal service model and report it to the core
    var service = {url: "/service/api/people", method: "GET", parameters: [ "userName" ], description: "etc"};
    reportService(service);
  }
};

// inspecting by trying the known patterns
// TODO could even try classical web crawling, especially for REST services
function inspect(url) {
  console.log("Inspecting url " + url);
  for (var pattern in patternToHandlers) {
    patternToHandlers[pattern](url);
  }
}

function reportService(service) {
  console.log("reportService " + JSON.stringify(service));
  // TODO post it to the core
}

for (var i in urls) {
  inspect(urls[i]);
}
