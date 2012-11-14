/**
 * 
 */
package org.easysoa.proxy.core.api.exchangehandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Endpoint;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * Discovery message handler, works with easysoa model V1
 * 
 * @author jguillemotte
 *
 */
public class EasySOAv1SOAPDiscoveryMessageHandler implements MessageHandler {

    //public static final String HANDLER_ID = "EasySOAv1SOAPDiscoveryMessageHandler";
    
    public static final int PORT = 8082;
    public static final String PATH = "easysoa/registry";
    public static final String NUXEO_URL = "http://localhost:" + PORT + "/" + PATH;    
    
    private final ClientConfig clientConfig;

    // Logger
    private static Logger logger = Logger.getLogger(EasySOAv1SOAPDiscoveryMessageHandler.class);    
    
    // User
    private String user;
    
    // Project ID
    private String projectId;
    
    // Environment
    private String environment;
    
    // ComponentIDs
    private String componentIds;
    
    /**
     * Constructor
     *
     */
    // TODO : Move Jersey client configuration in another class
    public EasySOAv1SOAPDiscoveryMessageHandler() {
        this.clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(new JsonMessageReader());
        clientConfig.getSingletons().add(new JsonMessageWriter());    
    }
    
    @Override
    public void setHandlerConfiguration(ProxyConfiguration configuration) {
        // TODO : copy only required parameters or the entire map ???
        if(configuration.getParameter(ProxyConfiguration.USER_PARAM_NAME) != null){
            user = configuration.getParameter(ProxyConfiguration.USER_PARAM_NAME);
        }
        if(configuration.getParameter(ProxyConfiguration.PROJECTID_PARAM_NAME) != null){
            projectId = configuration.getParameter(ProxyConfiguration.PROJECTID_PARAM_NAME);
        }
        if(configuration.getParameter(ProxyConfiguration.ENVIRONMENT_PARAM_NAME) != null){
            environment = configuration.getParameter(ProxyConfiguration.ENVIRONMENT_PARAM_NAME);
        }
        if(configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME) != null){
            // TODO as list : for now comma-separated, LATER custom service model ?
            componentIds = configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME);
        }
    }    

    @Override
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception {
        
        // Create endpoint
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        List<SoaNodeId> parents = new ArrayList<SoaNodeId>();
        
        // runtime props :
        properties.put("env:environment", environment);
        properties.put("endp:url", inMessage.buildCompleteUrl());
        
        //String endpUrl = "http://localhost:8076/services/PrecomptePartenaireService";
        
        // TODO : ask for user, projectID, componentsIDs param nuxeo name
        //properties.put("endp:url", parameters.get(ENVIRONMENT_PARAM_NAME));
        
        
        // parents :
        //parents.add(new SoaNodeId(InformationService.DOCTYPE, "PrecomptePartenaireService")); // specified service
        parents.add(new SoaNodeId("Component", componentIds)); // specified component
        //parents.add(new SoaNodeId("Component", "FraSCAti Studio for AXXX DPS DCV Integration")); // technical component        
        SoaNodeInformation soaNodeInfo = new SoaNodeInformation(new SoaNodeId(Endpoint.DOCTYPE, environment + ":" + inMessage.buildCompleteUrl()), properties, parents );        
        
        // Run request
        Client client = createAuthenticatedHTTPClient("Administrator", "Administrator");
        Builder discoveryRequest = client.resource(NUXEO_URL).type(MediaType.APPLICATION_JSON);
        OperationResult result = discoveryRequest.post(OperationResult.class, soaNodeInfo);
        if(result.isSuccessful()){
            logger.info("Service regsitered successfully");
        } else {
            logger.error("An error occurs during the service registration : " + result.getMessage());
        }
    }

    /**
     * Creates an authenticated client
     * @param username Username
     * @param password Password
     * @return 
     */
    private Client createAuthenticatedHTTPClient(String username, String password) {
        Client client = Client.create(this.clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(username, password));
        return client;
    }
      
}
