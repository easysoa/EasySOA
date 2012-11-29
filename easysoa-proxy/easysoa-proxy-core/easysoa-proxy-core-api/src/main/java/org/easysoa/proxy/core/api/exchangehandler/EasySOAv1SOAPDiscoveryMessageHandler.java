/**
 * 
 */
package org.easysoa.proxy.core.api.exchangehandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Endpoint;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Discovery message handler, works with easysoa model V1
 * 
 * @author jguillemotte
 *
 */
public class EasySOAv1SOAPDiscoveryMessageHandler implements MessageHandler {
    
    public final static String HANDLER_ID = "discoveryMessageHandler";
    
    // TODO : remove hard coded client configuration
    public static final int PORT = 8080;
    public static final String PATH = "nuxeo/site";
    public static final String NUXEO_URL = "http://localhost:" + PORT + "/" + PATH;

    // Logger
    private static Logger logger = Logger.getLogger(EasySOAv1SOAPDiscoveryMessageHandler.class);    

    private boolean enabled = true;
    
    private final ClientConfig clientConfig;
    
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
        if(enabled){
            // Create endpoint
            Map<String, Serializable> properties = new HashMap<String, Serializable>();
            List<SoaNodeId> parents = new ArrayList<SoaNodeId>();
            
            // runtime props :
            properties.put("env:environment", environment);
            properties.put("endp:url", inMessage.buildCompleteUrl());
            // TODO : add project ID property
            // properties.put("projectId", projectId); // Not implemented yet in V1 model
            // properties.put("user", user);
            
            // parents :
            //parents.add(new SoaNodeId(InformationService.DOCTYPE, "PrecomptePartenaireService")); // specified service
            //parents.add(new SoaNodeId("Component", componentIds)); // specified component
            //parents.add(new SoaNodeId("Component", "FraSCAti Studio for AXXX DPS DCV Integration")); // technical component        
            SoaNodeInformation soaNodeInfo = new SoaNodeInformation(new SoaNodeId(Endpoint.DOCTYPE, environment + ":" + inMessage.buildCompleteUrl()), properties, parents );        
            // Run request
            ClientBuilder clientBuilder = new ClientBuilder();
            clientBuilder.setNuxeoSitesUrl(NUXEO_URL);
            RegistryApi registryApi = clientBuilder.constructRegistryApi();
            OperationResult result = registryApi.post(soaNodeInfo);
            
            // Create some document
            if(result.isSuccessful()){
                logger.info("Service regsitered successfully");
            } else {
                logger.error("An error occurs during the service registration : " + result.getMessage());
            }
        } else {
            logger.info("Discovery message handler is disabled");
        }
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public String getID() {
        return HANDLER_ID;
    }
      
}
