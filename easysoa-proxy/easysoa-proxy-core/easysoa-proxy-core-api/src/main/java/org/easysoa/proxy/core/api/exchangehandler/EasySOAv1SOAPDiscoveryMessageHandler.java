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
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.proxy.core.api.properties.ProxyPropertyManager;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ResourceDownloadInfo;
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
	public static final String DISCOVERY_PROBE_TYPE = "HTTPProxy";
	
    // Logger
    private static Logger logger = Logger.getLogger(EasySOAv1SOAPDiscoveryMessageHandler.class);
    private boolean enabled = true;
    private ClientBuilder clientBuilder;
    
    // Proxy configuration params :
    // NB. defaults are in setHandlerConfiguration()
    // User
    private String user = null;
    // Project ID
    private String projectId = null; // default i.e. MyProject/Realisation
    // Environment
    private String environment = null; // TODO default environment in conf
    // ComponentIDs
    private String componentIds = null;
    /** For all other optional parameters  */
    private ProxyConfiguration configuration = null; // TODO in MessageHandlerBase & other handlers

    /**
     * Constructor
     *
     */
    // TODO : Move Jersey client configuration in another class
    public EasySOAv1SOAPDiscoveryMessageHandler() throws Exception {
    	PropertyManager propertyManager = ProxyPropertyManager.getPropertyManager(); // AND NOT PropertyManager because null TODO better
        clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoSitesUrl(propertyManager.getProperty("nuxeo.rest.service", "http://localhost:8080/nuxeo/site"));
        clientBuilder.setCredentials(propertyManager.getProperty("nuxeo.auth.login", "Administrator"),
        		propertyManager.getProperty("nuxeo.auth.password", "Administrator"));
    }
    
    private String checkRequiredParameter(ProxyConfiguration proxyConfiguration, String parameterName) {
        String value = proxyConfiguration.getParameter(parameterName);
        if (isEmpty(value)) {
            throw new RuntimeException("Missing required parameter"); // TODO better
        }
        return value;
    }
    
    private boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}

	public ProxyConfiguration getConfiguration() {
    	return this.configuration;
    }

    @Override
    public void setHandlerConfiguration(ProxyConfiguration configuration) {
        // Getting and checking required parameters
    	// NB. null is allowed when doing reset(), but required parameters are checked in handleMessage()
    	// & therefore there must be at least one required parameter, to know when it has been reset
        ///if (configuration.getParameter(ProxyConfiguration.PROJECTID_PARAM_NAME) != null) {
            projectId = configuration.getParameter(ProxyConfiguration.PROJECTID_PARAM_NAME);
        ///}
        ///if (configuration.getParameter(ProxyConfiguration.ENVIRONMENT_PARAM_NAME) != null) {
            environment = configuration.getParameter(ProxyConfiguration.ENVIRONMENT_PARAM_NAME);
        ///}
        ///projectId = checkRequiredParameter(configuration, ProxyConfiguration.PROJECTID_PARAM_NAME);	
        ///environment = checkRequiredParameter(configuration, ProxyConfiguration.ENVIRONMENT_PARAM_NAME);
            
        // Getting optional parameters
        if (configuration.getParameter(ProxyConfiguration.USER_PARAM_NAME) != null) {
            user = configuration.getParameter(ProxyConfiguration.USER_PARAM_NAME);
        } else {
        	user = "Administrator"; // default
        }
        ///if (configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME) != null) {
            // TODO as list : for now comma-separated, LATER custom service model ?
            componentIds = configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME);
        ///}
        
        // If all OK, can update the local configuration cache, else TODO reapply the old one
    	this.configuration = configuration;
    }

    @Override
    // TODO :Return an OperationResult object instead of void !
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception {
        if (enabled
        		// checking required parameters :
        		&& !isEmpty(projectId) && !isEmpty(environment)) {

            // check if the received message is a SOAP message
        	boolean isSoapMessage = checkForSoapMessage(inMessage);
        	boolean isGetWsdlMessage = !isSoapMessage || checkForWsdlMessage(inMessage);
            if(isSoapMessage || isGetWsdlMessage){

                // Create endpoint
                Map<String, Serializable> properties = new HashMap<String, Serializable>();
                List<SoaNodeId> parents = new ArrayList<SoaNodeId>();

            	String messageUrl = inMessage.buildCompleteUrl(); // TODO normalizeUrl() to avoid ex. http://vmpivotal:7080/WS//ContactSvc.asmx
                String endpointUrl = isSoapMessage ? messageUrl : messageUrl.substring(0, messageUrl.length() - 5); // removing ?wsdl 
                String wsdlUrl = isGetWsdlMessage ? messageUrl : messageUrl + "?wsdl";
                
                // properties
                // GLOBAL
                properties.put(Endpoint.XPATH_URL, endpointUrl);
                properties.put(Endpoint.XPATH_TITLE, environment + " - " + endpointUrl);
                properties.put(Endpoint.XPATH_ENDP_ENVIRONMENT, environment);
                properties.put(Subproject.XPATH_SUBPROJECT, projectId);
                //properties.put("*participants*", user); // TODO LATER participants meta
                //properties.put("serviceimpl:component?", component); // TODO LATER if any ; get from conf service, default none

                // If the message is a SOAP Post request, also discovery & send an EndpointConsumption
                if(isSoapMessage){
                    // TODO
                }

                // PROBE
                properties.put(ResourceDownloadInfo.XPATH_URL, wsdlUrl);
                properties.put(ResourceDownloadInfo.XPATH_PROBE_TYPE, DISCOVERY_PROBE_TYPE);
                properties.put(ResourceDownloadInfo.XPATH_PROBE_INSTANCEID, this.configuration.getId());
                //static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                //properties.put("rdi:timestamp", dateFormater.format(new GregorianCalendar().getTime())); // if the probe downloads the wsdl itself

                // TODO LATER : STRUCTURE
                //properties.put("iserv:operationIn", exchangeRecord.getInMessage().getMessageContent().getContentType().toString());
                //properties.put("iserv:outContentType", exchangeRecord.getOutMessage().getMessageContent().getContentType().toString());

                // parents :
                //parents.add(new SoaNodeId(InformationService.DOCTYPE, "PrecomptePartenaireService")); // specified service
                //parents.add(new SoaNodeId("Component", componentIds)); // specified component
                //parents.add(new SoaNodeId("Component", "FraSCAti Studio for AXXX DPS DCV Integration")); // technical component
                SoaNodeId soaNodeId = new SoaNodeId(projectId, Endpoint.DOCTYPE, environment + ":" + endpointUrl);
                SoaNodeInformation soaNodeInfo = new SoaNodeInformation(soaNodeId, properties, parents);

                // Run request
                RegistryApi registryApi = clientBuilder.constructRegistryApi();
                OperationResult result = registryApi.post(soaNodeInfo);
                logger.debug("Registry API request response status : " + result.isSuccessful());
                logger.debug("Registry API request response message : " + result.getMessage());

                // Create some document
                if (result.isSuccessful()) {
                    logger.info("Service registered successfully");
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
