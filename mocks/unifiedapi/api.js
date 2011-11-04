/*
object kinds :
endpoint
envDev
impl
scaffolderClientImpl
ui

/envDev/scaffolderClientImpl/references/calledService
*/

var easysoaServerUrl = "http://localhost";
var login = "Sophie";

exports.selectServiceEndpointInUI = function(envFilter) {
  var theUrl = easysoaServerUrl + ":9010/PureAirFlowers";
  return { url: theUrl,
    checkStarted: function() { 
      console.log("Checked "+theUrl);
      return true; } };
}

exports.createEnvDev = function(devEnvKind) {
  if (devEnvKind == "Light") {
    implServer = easysoaServerUrl;
  }
  var id = 0;
  return { id:id,
      name:login + "_" + id + "_" + "PureAirFlowers",
      implServer:implServer,
      serviceImpls: new Array(),
      externalServices: new Array()
    };
}

exports.createScaffolderClient = function(serviceEndpointToScaffold) {
  var theScaffolderUi = "http://localhost:8090/";
  return {
      scaffolderUi: theScaffolderUi,
      scaffoldedServiceUrl:serviceEndpointToScaffold.url,
      start: function() { console.log("Starting "+theScaffolderUi); return true; },
      stop: function() { console.log("Stopping "+theScaffolderUi); }
    };
}

exports.addExternalServiceEndpoint = function(env, serviceEndpointToScaffold) {
  env.externalServices.push(serviceEndpointToScaffold);
}

exports.addImpl = function(env, impl) {
  env.serviceImpls.push(impl);
}

exports.display = function(ui) {
  console.log("Displaying TODO");
}

exports.start = function(env) {
  for (i in env.externalServices) {
    if (!env.externalServices[i].checkStarted()) {
      return false;
    }
  }
  for (i in env.serviceImpls) {
    if (!env.serviceImpls[i].start()) {
      return false;
    }
  }
  return true;
}

exports.stop = function(env) {
  for (serviceImpl in env.serviceImpls) {
    serviceImpl.stop();
  }
}

