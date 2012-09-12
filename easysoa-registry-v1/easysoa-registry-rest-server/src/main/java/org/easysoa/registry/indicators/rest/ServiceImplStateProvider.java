package org.easysoa.registry.indicators.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class ServiceImplStateProvider implements IndicatorProvider {

    private static final String SERVICEIMPL_DOCTYPE_INDICATOR = DoctypeCountIndicator.getName(ServiceImplementation.DOCTYPE);
    
    @Override
    public List<String> getRequiredIndicators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception {
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        DocumentService documentService = Framework.getService(DocumentService.class);

        // Count indicators - ServiceImplementation-specific
        final int IDEAL_DOCUMENTATION_LINES = 40;
        int undocumentedServiceImpls = 0, documentationLines = 0;
        int serviceImplCount = computedIndicators.get(SERVICEIMPL_DOCTYPE_INDICATOR).getCount();
        int maxServiceImplsDocQuality = serviceImplCount * IDEAL_DOCUMENTATION_LINES;
        int serviceImplsDocQuality = maxServiceImplsDocQuality;
        Map<Serializable, Boolean> hasMock = new HashMap<Serializable, Boolean>();
        int mockedImplsCount = 0, testedImplsCount = 0, nonMockImplsCount = 0;
        DocumentModelList serviceImplModels = session.query(NXQL_SELECT_FROM
                + ServiceImplementation.DOCTYPE + NXQL_WHERE_NO_PROXY);
        
        for (DocumentModel serviceImplModel : serviceImplModels) {
            ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
            
            if ("(Placeholder)".equals(serviceImpl.getTitle())) { // TODO Specific field?
                continue;
            }
            
            String documentation = (String) serviceImpl.getProperty(ServiceImplementation.XPATH_DOCUMENTATION);
            if (documentation != null && !documentation.isEmpty()) {
                int serviceDocumentationLines = documentation.split("\n").length;
                documentationLines += serviceDocumentationLines;
                serviceImplsDocQuality -= Math.max(0, Math.abs(IDEAL_DOCUMENTATION_LINES - serviceDocumentationLines));
            }
            else {
                undocumentedServiceImpls++;
                serviceImplsDocQuality -= IDEAL_DOCUMENTATION_LINES;
            }
            
            String parentServiceId = null;
            DocumentModelList implParents = documentService.findAllParents(session, serviceImplModel);
            for (DocumentModel implParent : implParents) {
                if (Service.DOCTYPE.equals(implParent.getType())) {
                    parentServiceId = (String) implParent.getPropertyValue(Service.XPATH_SOANAME);
                    break;
                }
            }
            if (parentServiceId != null) {
                if (serviceImpl.isMock()) {
                    hasMock.put(parentServiceId, true);
                }
                else if (!hasMock.containsKey(parentServiceId)) {
                    hasMock.put(parentServiceId, false);
                }
            }
            
            if (!serviceImpl.isMock()) {
                nonMockImplsCount++;
                if (!serviceImpl.getTests().isEmpty()) {
                    testedImplsCount++;
                }
            }
        }
        for (Boolean isMock : hasMock.values()) {
            if (isMock) {
                mockedImplsCount++;
            }
        }
        
        // Indicators results registration
        
        indicators.put("Undocumented service implementation", 
                new IndicatorValue(undocumentedServiceImpls, -1));
        indicators.put("Lines of documentation per service impl. (average)",
                new IndicatorValue((serviceImplCount > 0) ? (documentationLines / serviceImplCount) : -1, -1));
        indicators.put("Service impls without mock", 
                new IndicatorValue(nonMockImplsCount - mockedImplsCount,
                        (nonMockImplsCount > 0) ? (100 * (nonMockImplsCount - mockedImplsCount) / nonMockImplsCount) : -1));
        indicators.put("Service impls without test", 
                new IndicatorValue(nonMockImplsCount - testedImplsCount,
                        (nonMockImplsCount > 0) ? (100 * (nonMockImplsCount - testedImplsCount) / nonMockImplsCount) : -1));
        indicators.put("Service implementations documentation quality", 
                new IndicatorValue(-1, (maxServiceImplsDocQuality == 0) ? -1 : (100 * serviceImplsDocQuality / maxServiceImplsDocQuality)));

        // TODO model consistency ex. impl without service
        // TODO for one ex. impl of ONE service => prop to query
        
        return indicators;
    }
    
}
