/**
 *
 */
package org.easysoa.proxy.core.api.exchangehandler;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.message.QueryParam;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.marshalling.JsonMessageReader;
import org.easysoa.registry.rest.marshalling.JsonMessageWriter;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.Subproject;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.osoa.sca.annotations.Scope;

/**
 * Discovery message handler, works with easysoa model V1
 *
 * @author jguillemotte
 *
 */
@Scope("composite")
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
    private static String user;
    // Project ID
    private static String projectId;
    // Environment
    private static String environment;
    // ComponentIDs
    private static String componentIds;

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
        if (configuration.getParameter(ProxyConfiguration.USER_PARAM_NAME) != null) {
            user = configuration.getParameter(ProxyConfiguration.USER_PARAM_NAME);
        }
        if (configuration.getParameter(ProxyConfiguration.PROJECTID_PARAM_NAME) != null) {
            projectId = configuration.getParameter(ProxyConfiguration.PROJECTID_PARAM_NAME);
        }
        if (configuration.getParameter(ProxyConfiguration.ENVIRONMENT_PARAM_NAME) != null) {
            environment = configuration.getParameter(ProxyConfiguration.ENVIRONMENT_PARAM_NAME);
        }
        if (configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME) != null) {
            // TODO as list : for now comma-separated, LATER custom service model ?
            componentIds = configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME);
        }
    }

    @Override
    // TODO :Return an OperationResult object instead of void !
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception {
        if (enabled) {

            // check if the received message is a SOAP message
            if(checkForSoapMessage(inMessage) || checkForWsdlMessage(inMessage)){

                // Create endpoint
                Map<String, Serializable> properties = new HashMap<String, Serializable>();
                List<SoaNodeId> parents = new ArrayList<SoaNodeId>();

                // properties
                // GLOBAL
                String endpointUrl = inMessage.buildCompleteUrl();
                properties.put(Endpoint.XPATH_URL, endpointUrl);
                properties.put(Endpoint.XPATH_TITLE, endpointUrl);
                properties.put(Endpoint.XPATH_ENDP_ENVIRONMENT, environment);
                properties.put(Subproject.XPATH_SUBPROJECT, projectId);

                // If the message is a SOAP Post request
                // Add the rdi:url property
                if(checkForSoapMessage(inMessage)){
                    properties.put("rdi:url", endpointUrl + "?wsdl");
                }

                // PROBE
                properties.put("spnode:subproject", projectId); // TODO get from conf service, default empty i.e. default project & Phase
                //properties.put("*participants*", user); // TODO LATER get from conf service, default "Administrator"
                //properties.put("serviceimpl:component?", component); // TODO LATER if any ; get from conf service, default none
                properties.put(Endpoint.XPATH_ENDP_ENVIRONMENT, environment);
                //properties.put("rdi:timestamp", exchangeRecord.getOutMessage().getMessageContent().getContentType().toString()); // if it downloads the wsdl
                properties.put("rdi:probeType", "HTTPProxy");
                //properties.put("rdi:probeInstance", "default");

                // TODO LATER : STRUCTURE
                //properties.put("iserv:operationIn", exchangeRecord.getInMessage().getMessageContent().getContentType().toString());
                //properties.put("iserv:outContentType", exchangeRecord.getOutMessage().getMessageContent().getContentType().toString());

                // parents :
                //parents.add(new SoaNodeId(InformationService.DOCTYPE, "PrecomptePartenaireService")); // specified service
                //parents.add(new SoaNodeId("Component", componentIds)); // specified component
                //parents.add(new SoaNodeId("Component", "FraSCAti Studio for AXXX DPS DCV Integration")); // technical component
                SoaNodeInformation soaNodeInfo = new SoaNodeInformation(new SoaNodeId(projectId, Endpoint.DOCTYPE, environment + ":" + inMessage.buildCompleteUrl()), properties, parents);

                // Run request
                ClientBuilder clientBuilder = new ClientBuilder();
                clientBuilder.setNuxeoSitesUrl(NUXEO_URL);
                RegistryApi registryApi = clientBuilder.constructRegistryApi();
                OperationResult result = registryApi.post(soaNodeInfo);
                logger.debug("Registry API request response status : " + result.isSuccessful());
                logger.debug("Registry API request response message : " + result.getMessage());

                // Create some document
                if (result.isSuccessful()) {
                    logger.info("Service regsitered successfully");
                } else {
                    logger.error("An error occurs during the service registration : " + result.getMessage());
                    throw new Exception("An error occurs during the service registration : " + result.getMessage());
                }
            }
            else {
                logger.info("The received message is not a SOAP message or a WSDL get request ! It will be not registered !");
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

    /**
     * Check for a post message containing expression matching a SOAP exchange like "soap:envelope"
     * @param inMessage
     * @return true if the message is a SOAP exchange
     */
    private boolean checkForSoapMessage(InMessage inMessage) {
        //TODO : Refine the way that a WSDl message is discovered
        if(inMessage.getMethod().equalsIgnoreCase("post") && (inMessage.getMessageContent().getRawContent().toLowerCase().contains("<soap:envelope")
                || inMessage.getMessageContent().getRawContent().toLowerCase().contains("http://schemas.xmlsoap.org/soap/envelope/"))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check for a get message requesting a WSDL file
     * @param inMessage
     * @return true if the message is a request for a WSDL file
     */
    private boolean checkForWsdlMessage(InMessage inMessage) {
        boolean returnValue = false;
        String pattern;
        try {
            pattern = PropertyManager.getPropertyManager().getProperty("proxy.wsdl.request.detect", ".*?wsdl");
        } catch (Exception ex) {
            logger.warn("An error occurs when getting the 'proxy.wsdl.request.detect' value, using default value => .*?wsdl", ex);
            pattern = ".*?wsdl";
        }
        //for (QueryParam queryParam : inMessage.getQueryString().getQueryParams()) {
            //if (inMessage.getMethod().equalsIgnoreCase("get") && queryParam.getName().toLowerCase().matches(pattern)) {
            if (inMessage.getMethod().equalsIgnoreCase("get") && inMessage.getPath().toLowerCase().matches(pattern)) {
                return true;
            }
        //}
        return returnValue;
    }
}
