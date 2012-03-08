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
Object.extend(module.exports, require('./Validation'));

// ===================== UI =====================

module.exports.selectServiceEndpointInUI = function(envFilter) {
    // Run query on services
    var query = "SELECT * FROM ServiceEndpoint WHERE ";
    envFilter.each(function (environmentName) {
        query += "endp:environment = '"+environmentName+"' OR ";
    });
    new module.exports.Query().run(query.substring(0, query.length - 4));
    
    // Return the one chosen by the user
    var selectedEndpoint = new module.exports.ServiceEndpoint(
            new module.exports.ExternalImpl("PureAirFlowers"),
            "http://localhost:9010/PureAirFlowers",
            new module.exports.StagingEnvironment(envFilter[0]));
    selectedEndpoint.started = true;
    return selectedEndpoint;
    
};

module.exports.selectServiceImplFromEnvInUI = function(env) {
    // Run query on services
    new module.exports.Query().run("SELECT * FROM ServiceEndpoint WHERE endp:environment = '" + env.name + "'");
    
    // Return the one chosen by the user
    return env.endpoints[0].impl;
};

module.exports.getUnexpectedNotification = function() {
    return {
        url : "http://unexpected.notification.com"
    };
}
