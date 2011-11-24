Object.extend(global, require('prototype'));
Object.extend(module.exports, require('./Configuration'));
Object.extend(module.exports, require('./Consts'));
Object.extend(module.exports, require('./Endpoints'));
Object.extend(module.exports, require('./Environments'));
Object.extend(module.exports, require('./Impls'));
Object.extend(module.exports, require('./Projects'));
Object.extend(module.exports, require('./Proxies'));
Object.extend(module.exports, require('./Queries'));
Object.extend(module.exports, require('./Tests'));

// ===================== UI =====================

module.exports.selectServiceEndpointInUI = function(envFilter) {
    var selectedEndpoint = new module.exports.ServiceEndpoint(
            new module.exports.ExternalImpl("PureAirFlowers"),
            "http://localhost:9010/PureAirFlowers",
            new module.exports.StagingEnvironment(envFilter[0]));
    selectedEndpoint.started = true;
    return selectedEndpoint;
};

module.exports.selectServiceImplFromEnvInUI = function(env) {
    return env.endpoints[0].impl;
};
