package org.easysoa.registry.indicators.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class ServiceStateProvider implements IndicatorProvider {

    private static final String SERVICE_DOCTYPE_INDICATOR = "Service count";

    @Override
    public List<String> getRequiredIndicators() {
        return Arrays.asList(SERVICE_DOCTYPE_INDICATOR);
    }
    
    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        DocumentModelList serviceList = session.query(NXQL_SELECT_FROM + "Service" + NXQL_WHERE_NO_PROXY);
        
        // Count indicators - Service-specific
        int serviceWhithoutImplementationNb = 0;
        int servicesCount = computedIndicators.get(SERVICE_DOCTYPE_INDICATOR).getCount();
        HashSet<String> serviceWhithoutImplementationIdSet = new HashSet<String>(servicesCount);
        int serviceWithImplementationWhithoutEndpointNb = 0;
        for (DocumentModel service : serviceList) {
            // finding (all) child implems and then their endpoints
            DocumentModelList serviceImpls = documentService.getChildren(session, service.getRef(), ServiceImplementation.DOCTYPE);
            if (serviceImpls.isEmpty()) {
                serviceWhithoutImplementationNb++;
                serviceWhithoutImplementationIdSet.add(service.getId());
            } else {
                boolean isAServiceWithImplementationWhithoutEndpoint = true;
                for (DocumentModel serviceImplModel : serviceImpls) {
                    DocumentModelList endpoints = documentService.getChildren(session, serviceImplModel.getRef(), Endpoint.DOCTYPE);
                    ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
                    if (!serviceImpl.isMock() && endpoints.isEmpty()) {
                        isAServiceWithImplementationWhithoutEndpoint = false;
                        break;
                    }
                }
                if (isAServiceWithImplementationWhithoutEndpoint) {
                    serviceWithImplementationWhithoutEndpointNb++;
                }
            }
        }
        
        // Indicators results registration
        
        // TODO "main" vs "test" implementation
        indicators.put("serviceWhithoutImplementation",
                new IndicatorValue(serviceWhithoutImplementationNb,
                        (servicesCount > 0) ? (100 * serviceWhithoutImplementationNb / servicesCount) : -1)); 
        
        // TODO "test", "integration", "staging" ("design", "dev")
        indicators.put("serviceWithImplementationWhithoutEndpoint",
                new IndicatorValue(serviceWithImplementationWhithoutEndpointNb,
                        (servicesCount - serviceWhithoutImplementationNb > 0) ? (100 * serviceWithImplementationWhithoutEndpointNb / (servicesCount - serviceWhithoutImplementationNb)) : -1));
        
        indicators.put("serviceWhithoutEndpoint",
                new IndicatorValue(serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb, 
                        (servicesCount > 0) ? (100 * (serviceWhithoutImplementationNb + serviceWithImplementationWhithoutEndpointNb) / servicesCount) : -1));

        return indicators;
    }
    
}
