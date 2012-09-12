package org.easysoa.registry.indicators.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceConsumption;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class ServiceConsumptionIndicatorProvider implements IndicatorProvider {

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        List<SoaNodeId> servicesIds = documentService.createSoaNodeIds(
                    session.query(NXQL_SELECT_FROM + Service.DOCTYPE + NXQL_WHERE_NO_PROXY)
                    .toArray(new DocumentModel[]{}));
        List<SoaNodeId> unconsumedServiceIds = new ArrayList<SoaNodeId>(servicesIds);
        DocumentModelList serviceConsumptionModels = session.query(NXQL_SELECT_FROM + ServiceConsumption.DOCTYPE + NXQL_WHERE_NO_PROXY);
        for (DocumentModel serviceConsumptionModel : serviceConsumptionModels) {
            ServiceConsumption serviceConsumption = serviceConsumptionModel.getAdapter(ServiceConsumption.class);
            unconsumedServiceIds.removeAll(serviceConsumption.getConsumableServiceImpls());
        }
        
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        indicators.put("Never consumed services",
                new IndicatorValue(unconsumedServiceIds.size(), 100 * unconsumedServiceIds.size() / servicesIds.size()));
        
        return indicators;
    }

}
