// scenario : create Service Scaffolder Client for a given existing service endpoint
// context : Light

var api = require('./api.js');

var envFilter = ["sandbox", "dev"]; // "sandbox" is a sandboxed version of "staging" i.e. actual, existing services
var serviceEndpointToScaffold = api.selectServiceEndpointInUI(envFilter); // user also navigates or filters

var testEnv = api.createEnvDev("Light"); // on default business architecture
api.addExternalServiceEndpoint(testEnv, serviceEndpointToScaffold);
var scaffolderClientImpl = api.createScaffolderClient(serviceEndpointToScaffold);
api.addImpl(testEnv, scaffolderClientImpl);

if (api.start(testEnv)) { // starts scaffolder
  api.display(scaffolderClientImpl.clientUi);
  console.log("Done.");
}
else {
  console.error("Fail.");
}


// scenario : after the previous one, mock the serviceEndpoint it calls using a javascriptImpl


