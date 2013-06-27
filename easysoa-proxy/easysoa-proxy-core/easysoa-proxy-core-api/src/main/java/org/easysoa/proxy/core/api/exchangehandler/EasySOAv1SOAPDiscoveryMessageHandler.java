/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.core.api.exchangehandler;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
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
public class EasySOAv1SOAPDiscoveryMessageHandler extends MessageHandlerBase {

    // Probe type and id
    public final static String HANDLER_ID = "discoveryMessageHandler";
	public static final String DISCOVERY_PROBE_TYPE = "HTTPProxy";

    // Default values
    public static final String PROJECT_ID_DEFAULT = "MyProject/Realisation";
    public static final String ENVIRONMENT_DEFAULT = "test";

    // Logger
    private static Logger logger = Logger.getLogger(EasySOAv1SOAPDiscoveryMessageHandler.class);
    private boolean enabled = true;
    private ClientBuilder registryClient = null;

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

    /*private String checkRequiredParameter(ProxyConfiguration proxyConfiguration, String parameterName) {
        String value = proxyConfiguration.getParameter(parameterName);
        if (isEmpty(value)) {
            throw new RuntimeException("Missing required parameter"); // TODO better
        }
        return value;
    }*/

    private boolean isEmpty(String value) {
		return value == null || value.length() == 0;
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
            // TODO LATER as list : for now comma-separated, LATER custom service model ?
            componentIds = configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME);
        ///}

        // If all OK, can update the local configuration cache, else TODO reapply the old one
    	setConfiguration(configuration);
    }

    @Override
    // TODO :Return an OperationResult object instead of void !
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception {
        // checking required parameters
        if (enabled && hasValidConfiguration()) {

            // check if the received message is a SOAP message
        	boolean isSoapMessage = checkForSoapMessage(inMessage);
        	boolean isGetWsdlMessage = !isSoapMessage || checkForWsdlMessage(inMessage);
            if(isSoapMessage || isGetWsdlMessage){

                // Get the registry client, if not initialized => init
                if(registryClient == null){
                    RegistryJerseyClientConfiguration jerseyClient = new RegistryJerseyClientConfiguration();
                    registryClient = jerseyClient.getClient();
                }

                // Create endpoint
                RegistryApi registryApi = registryClient.constructRegistryApi();
                Map<String, Serializable> properties = new HashMap<String, Serializable>();
                List<SoaNodeId> parents = new ArrayList<SoaNodeId>();

                // Normalize the URL
                URI uriToNormalize = new URI(inMessage.buildCompleteUrl());
            	String messageUrl = uriToNormalize.normalize().toString();
                String endpointUrl = isSoapMessage ? messageUrl : messageUrl.substring(0, messageUrl.length() - 5); // removing ?wsdl
                String wsdlUrl = isGetWsdlMessage ? messageUrl : messageUrl + "?wsdl";

                // properties
                // GLOBAL
                properties.put(Subproject.XPATH_SUBPROJECT, projectId);
                properties.put(Endpoint.XPATH_TITLE, environment + " - " + endpointUrl);

                // ENDPOINT
                properties.put(Endpoint.XPATH_URL, endpointUrl);
                properties.put(Endpoint.XPATH_ENDP_ENVIRONMENT, environment);
                properties.put(Endpoint.XPATH_ENDP_HOST, inMessage.getRemoteHost());
                //properties.put(Endpoint.XPATH_ENDP_IP, inMessage.getServer()); // TODO add meta
                //properties.put("*participants*", user); // TODO LATER participants meta
                //properties.put("serviceimpl:component?", component); // TODO LATER if any ; get from conf service, default none
                // TODO if SOAPAction header Endpoint.XPATH_ENDP_SERVICE_PROTOCOL = SOAP, else REST
                ///properties.put(Endpoint.XPATH_ENDP_SERVICE_PROTOCOL, agent); // SOAP, REST...
                // TODO see in headers if anything (ex. "agent") to guess :
                ///properties.put(Endpoint.XPATH_ENDP_SERVICE_RUNTIME, agent); // CXF (, Axis2...)
                ///properties.put(Endpoint.XPATH_ENDP_APP_SERVER_RUNTIME, agent); // ApacheTomcat, Jetty...
                // TODO LATER security props

                String endpointSoaName = environment + ':' + endpointUrl;

                // If the message is a SOAP Post request, also discovery & send an EndpointConsumption
                if(isSoapMessage){

                    // Filling base info :
                    Map<String, Serializable> ecProperties = new HashMap<String, Serializable>();
                    List<SoaNodeId> ecParents = new ArrayList<SoaNodeId>();
                    // TODO LATER create it in consumer parent i.e. (placeholder) DeployedDeliverable (which has
                    // Environment meta) with ip & host classification meta (like Application on Deliverable) & try to match
                    String consumerHost = environment + ':' + inMessage.getRemoteHost();

                    // Filling consumer info :
                    ecProperties.put(EndpointConsumption.XPATH_CONSUMER_HOST, inMessage.getRemoteHost()); // ex. "vmapv", TODO LATER alt in case of proxy see through it using request.getHeader( "X_FORWARDED_FOR" ); see nuxeo VirtualHostHelper & http://stackoverflow.com/questions/1767080/httpservletrequest-getremoteaddr-returning-wrong-address
                    ecProperties.put(EndpointConsumption.XPATH_CONSUMER_IP, inMessage.getRemoteAddress()); // Address
                    // NB. port is useless because changes all the times on client side
                    // consumerIp=ip ex. "127.0.0.1"
                    // try referrer request header (at least println) ex. "" : There is no referrer header in the request
                    // using HTTP request referrer header or ServletRequest getRemoteAddr()
                    // or getRemotePort() (see what's int it, already in inMessage ??)
                    // to put in endpoint-consumption.xsd, to add to nuxeo types & layout
                    /*for(Header header : inMessage.getHeaders().getHeaderList()){
                        System.out.println("HEADERName : " + header.getName() + " - " + header.getValue());
                    }*/
                    // TODO LATER take protocol props (see above for Endpoint) out of Endpoint
                    // in a facet to be also put on EndpointConsumption and fill it

                    // Filling consumed endpoint info :
                    // ec:consumedUrl & ec:consumedEnvironment
                    // Since is ResourceDownloadInfo and WsdlInfo, resource fw will extract wsdlinfo
                    // of consumed endpoint wsdl on EndpointConsumption (just as it's done for Endpoint)
                    ecProperties.put(EndpointConsumption.XPATH_CONSUMED_URL, endpointUrl);
                    ecProperties.put(EndpointConsumption.XPATH_CONSUMED_ENVIRONMENT, environment); // TODO LATER rather merely "env:environment", shared with Endpoint

                    // Schedule download & parsing of WSDL using Resource framework :
                    ecProperties.put(ResourceDownloadInfo.XPATH_URL, wsdlUrl);

                    // Create Endpoint Consumption SOA node ID : [consumerId]>[consumedEndpointId]
                    String ecSoaName = consumerHost + '>' + endpointSoaName;
                    SoaNodeId ecSoaNodeId = new SoaNodeId(projectId, EndpointConsumption.DOCTYPE, ecSoaName);
                    SoaNodeInformation ecSoaNodeInfo = new SoaNodeInformation(ecSoaNodeId, ecProperties, ecParents);
                    OperationResult ecResult = registryApi.post(ecSoaNodeInfo);
                    if(!ecResult.isSuccessful()){
                        logger.warn("Unable to registry the endpoint consumption : " + ecResult.getMessage());
                        throw new Exception("An error occurs during the endpoint consumption registration : " + ecResult.getMessage());
                    } else {
                        // make endpoint child of endpoint consumption (which is Folderish), by providing endpoint consumption as parent of endpoint
                        parents.add(ecSoaNodeId);
                    }
                }

                // get request to get the wsdl
                // PROBE
                properties.put(ResourceDownloadInfo.XPATH_URL, wsdlUrl);
                properties.put(ResourceDownloadInfo.XPATH_PROBE_TYPE, DISCOVERY_PROBE_TYPE);
                properties.put(ResourceDownloadInfo.XPATH_PROBE_INSTANCEID, getConfiguration().getId());
                //static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                //properties.put("rdi:timestamp", dateFormater.format(new GregorianCalendar().getTime())); // if the probe downloads the wsdl itself

                // TODO LATER : STRUCTURE
                //properties.put("iserv:operationIn", exchangeRecord.getInMessage().getMessageContent().getContentType().toString()); // RATHER IN WSDL PARSING ??
                //properties.put("iserv:outContentType", exchangeRecord.getOutMessage().getMessageContent().getContentType().toString()); // RATHER IN WSDL PARSING ??
                // parents :
                //parents.add(new SoaNodeId(InformationService.DOCTYPE, "PrecomptePartenaireService")); // specified service NO, IN MATCHING
                //parents.add(new SoaNodeId("Component", componentIds)); // specified component
                //parents.add(new SoaNodeId("Component", "FraSCAti Studio for AXXX DPS DCV Integration")); // technical component
                SoaNodeId soaNodeId = new SoaNodeId(projectId, Endpoint.DOCTYPE, endpointSoaName);
                SoaNodeInformation soaNodeInfo = new SoaNodeInformation(soaNodeId, properties, parents);

                // Run request
                OperationResult result = registryApi.post(soaNodeInfo);
                logger.debug("Registry API request response status : " + result.isSuccessful());
                logger.debug("Registry API request response message : " + result.getMessage());

                // Create some document
                if (result.isSuccessful()) {
                    logger.info("Endpoint registered successfully");
                } else {
                    logger.error("An error occurs during the endpoint registration : " + result.getMessage());
                    throw new Exception("An error occurs during the endpoint registration : " + result.getMessage());
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
        //TODO LATER : Refine the way that a WSDl message is discovered
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
        if (inMessage.getMethod().equalsIgnoreCase("get") && inMessage.getPath().toLowerCase().matches(pattern)) {
            return true;
        }
        return returnValue;
    }

    /**
     * Check if the proxyConfiguration is valid to be used with the registry
     * Required parameters are :
     * - ProjectID not empty (and not null)
     * - Environment nor empty (and not null)
     * @return true if the configuration is valid, false otherwise
     */
    private boolean hasValidConfiguration(){
        return !isEmpty(projectId) && !isEmpty(environment);
    }

}
