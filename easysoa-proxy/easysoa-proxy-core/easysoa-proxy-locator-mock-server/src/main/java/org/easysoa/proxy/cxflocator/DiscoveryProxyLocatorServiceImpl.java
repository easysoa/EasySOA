package org.easysoa.proxy.cxflocator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.easysoa.proxy.cxflocator.interceptor.CxfLocatorInInterceptor;
import org.easysoa.registry.types.ids.SoaNodeId;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.InformationService;
import org.easysoa.registry.types.Endpoint;
import org.talend.schemas.esb.locator._2011._11.BindingType;
import org.talend.schemas.esb.locator._2011._11.LookupEndpointResponse;
import org.talend.schemas.esb.locator._2011._11.LookupEndpointsResponse;
import org.talend.schemas.esb.locator._2011._11.LookupRequestType;
import org.talend.schemas.esb.locator._2011._11.SLPropertiesType;
import org.talend.schemas.esb.locator._2011._11.TransportType;
import org.talend.services.esb.locator.v1.InterruptedExceptionFault;
import org.talend.services.esb.locator.v1.LocatorService;
import org.talend.services.esb.locator.v1.ServiceLocatorFault;


/**
 * Proxies (tunnel) Talend's LocatorService to allow discovery of endpoints at registration time.
 * Also works without Talend's (Zookeeper-based) LocatorService, in which case it returns empty results.
 * 
 * @author jguillemotte
 *
 */
public class DiscoveryProxyLocatorServiceImpl implements LocatorService {
    
    private LocatorService talendLocatorService = null;

    private Logger logger = Logger.getLogger(DiscoveryProxyLocatorServiceImpl.class.getName());
    
    public void setTalendLocatorService(LocatorService talendLocatorService) {
        this.talendLocatorService = talendLocatorService;
    }

    public void unregisterEndpoint(QName serviceName, String endpointURL) throws ServiceLocatorFault, InterruptedExceptionFault {
        if (talendLocatorService != null) {
            talendLocatorService.unregisterEndpoint(serviceName, endpointURL);
        }
    }

    public LookupEndpointResponse lookupEndpoint(LookupRequestType parameters) throws ServiceLocatorFault, InterruptedExceptionFault {
        if (talendLocatorService != null) {
            return talendLocatorService.lookupEndpoint(parameters);
        } else {
            return new LookupEndpointResponse(); // empty // TODO legit enough ?
        }
    }

    public LookupEndpointsResponse lookupEndpoints(LookupRequestType parameters) throws ServiceLocatorFault, InterruptedExceptionFault {
        if (talendLocatorService != null) {
            return talendLocatorService.lookupEndpoints(parameters);
        } else {
            return new LookupEndpointsResponse(); // empty // TODO legit enough ?
        }
    }

    public void registerEndpoint(QName serviceName, String endpointURL, BindingType binding, TransportType transport, SLPropertiesType properties) throws ServiceLocatorFault,
            InterruptedExceptionFault {
        // getting low-level protocol info from our interceptor's ThreadLocal :
        String endpoint = CxfLocatorInInterceptor.endpointSession.get();

        // calling EasySOA to notify discovery of the endpoint :
        // Init Rest registry client
        if(endpoint != null){
            try {
                ClientBuilder clientBuilder = new ClientBuilder();
                RegistryApi registryApi = clientBuilder.constructRegistryApi();

                Map<String, Serializable> nxProperties;
                OperationResult result;
                
                SoaNodeId myServiceId = new SoaNodeId(InformationService.DOCTYPE, serviceName.getLocalPart());
                nxProperties = new HashMap<String, Serializable>();
                nxProperties.put("title", serviceName.getLocalPart());
                // Call Rest Nuxeo service for registering Service
                result = registryApi.post(new SoaNodeInformation(myServiceId, nxProperties, null));                
                
                SoaNodeId myEndpointId = new SoaNodeId(Endpoint.DOCTYPE, endpoint);
                nxProperties = new HashMap<String, Serializable>();
                nxProperties.put("endp:url", endpoint);                
                
                List<SoaNodeId> parentDocuments = new ArrayList<SoaNodeId>();
                parentDocuments.add(myServiceId);
                // Call Rest Nuxeo service for registering endpoint
                result = registryApi.post(new SoaNodeInformation(myEndpointId, nxProperties, parentDocuments));
                if(!result.isSuccessful()){
                    logger.error("An error occurs during the endpoint recording. " + result.getMessage());
                } 
                else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Endpoint recorded successfully.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{ 
                CxfLocatorInInterceptor.endpointSession.set(null);
            }
        }
        
        if (talendLocatorService != null) {
            talendLocatorService.registerEndpoint(serviceName, endpointURL, binding, transport, properties);
        }
    }

}
