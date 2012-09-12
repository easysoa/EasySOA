package org.easysoa.registry.indicators.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.runtime.api.Framework;

public class SoftwareComponentIndicatorProvider implements IndicatorProvider {

    @Override
    public List<String> getRequiredIndicators() {
        return Arrays.asList(
                DoctypeCountIndicator.getName(Deliverable.DOCTYPE),
                DoctypeCountIndicator.getName(ServiceImplementation.DOCTYPE),
                DoctypeCountIndicator.getName(Service.DOCTYPE)
                );
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception {
        Map<String, DocumentModelList> listMap = new HashMap<String, DocumentModelList>();
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        
        // not in any software component :
        DocumentModelList deliverableProxyInASoftwareComponent = session.query(NXQL_SELECT_FROM + "Deliverable"
                + " WHERE " + "ecm:currentLifeCycleState <> 'deleted' " + "AND ecm:isProxy = 1"
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/SoftwareComponent" + "'");
        listMap.put("deliverable in no software component", session.query(NXQL_SELECT_FROM + "Deliverable" + NXQL_WHERE_NO_PROXY
                + " AND ecm:uuid NOT IN " + getProxiedIdLiteralList(session, deliverableProxyInASoftwareComponent)));
        int deliverableInNoSoftwareComponentCount = listMap.get("deliverable in no software component").size();
        int deliverableCount = computedIndicators.get(DoctypeCountIndicator.getName(Deliverable.DOCTYPE)).getCount();
        indicators.put("deliverable in no software component", 
                new IndicatorValue(deliverableInNoSoftwareComponentCount,
                100 * deliverableInNoSoftwareComponentCount / deliverableCount));
        
        listMap.put("deliverable in no software component' implementations", session.query(NXQL_SELECT_FROM + "ServiceImplementation"
                + " WHERE " + "ecm:currentLifeCycleState <> 'deleted' " + "AND ecm:isProxy = 1"
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/Deliverable" + "'"
                + " AND ecm:parentId NOT IN " + getProxiedIdLiteralList(session, deliverableProxyInASoftwareComponent)));
        indicators.put("deliverable in no software component' implementations", 
                new IndicatorValue(listMap.get("deliverable in no software component' implementations").size(),
                        -1));
        
        DocumentModelList implementationProxyInADeliverable = session.query(NXQL_SELECT_FROM + "ServiceImplementation"
                + " WHERE " + "ecm:currentLifeCycleState <> 'deleted' " + "AND ecm:isProxy = 1"
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/Deliverable" + "'");
        listMap.put("implementation in no deliverable", session.query(NXQL_SELECT_FROM + "ServiceImplementation" + NXQL_WHERE_NO_PROXY
                + " AND ecm:uuid NOT IN " + getProxiedIdLiteralList(session, implementationProxyInADeliverable)));
        int serviceImplCount = computedIndicators.get(DoctypeCountIndicator.getName(ServiceImplementation.DOCTYPE)).getCount();
        indicators.put("implementation in no deliverable", 
                new IndicatorValue(listMap.get("implementation in no deliverable").size(),
                        100 * listMap.get("implementation in no deliverable").size() / serviceImplCount));
        
        ArrayList<DocumentModel> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent = new ArrayList<DocumentModel>(listMap.get("implementation in no deliverable"));
        implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.addAll(listMap.get("deliverable in no software component' implementations"));
        indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent",
                new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.size(),
                        100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent.size() / serviceImplCount));
        
        DocumentModelList implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceProxy = session.query(NXQL_SELECT_FROM + "ServiceImplementation"
                + " WHERE " + "ecm:currentLifeCycleState <> 'deleted' " + "AND ecm:isProxy = 1"
                + " AND ecm:path STARTSWITH '" + "/default-domain/repository/Service" + "'"
                // + " AND ecm:proxyTargetId IN " + getProxiedIdLiteralList(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent)); // TODO soaid for proxies
                + " AND ecm:uuid IN " + getIdLiteralList(getProxyIds(session, implementationInNoDeliverableOrWhoseIsInNoSoftwareComponent, null))); // TODO soaid for proxies
        int serviceCount = computedIndicators.get(DoctypeCountIndicator.getName(ServiceImplementation.DOCTYPE)).getCount();
        HashSet<String> implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet = new HashSet<String>(getParentIds(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceProxy));
        indicators.put("implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentService",
                new IndicatorValue(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet.size(),
                        100 * implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet.size() / serviceCount));
       
        HashSet<String> serviceWhithoutImplementationIdSet = new HashSet<String>(serviceCount);
        DocumentModelList serviceList = session.query(NXQL_SELECT_FROM + Service.DOCTYPE + NXQL_WHERE_NO_PROXY);
        DocumentService documentService = Framework.getService(DocumentService.class);
        for (DocumentModel service : serviceList) {
            DocumentModelList serviceImpls = documentService.getChildren(session, service.getRef(), ServiceImplementation.DOCTYPE);
            if (serviceImpls.isEmpty()) {
                serviceWhithoutImplementationIdSet.add(service.getId());
            }
        }
        HashSet<String> serviceInNoSoftwareComponentIdSet = new HashSet<String>(implementationInNoDeliverableOrWhoseIsInNoSoftwareComponentServiceIdSet);
        serviceInNoSoftwareComponentIdSet.addAll(serviceWhithoutImplementationIdSet);
        indicators.put("serviceInNoSoftwareComponent", 
                new IndicatorValue(serviceInNoSoftwareComponentIdSet.size(),
                100 * serviceInNoSoftwareComponentIdSet.size() / serviceCount));
        
        return indicators;
    }
    
    private String getIdLiteralList(List<String> ids) throws ClientException {
        return "('" + ids.toString().replaceAll("[\\[\\]]", "").replaceAll(", ", "', '") + "')";
    }

    private String getProxiedIdLiteralList(CoreSession session, List<DocumentModel> proxies) throws ClientException {
        return getIdLiteralList(getProxiedIds(session, proxies));
    }

    private List<String> getProxiedIds(CoreSession session, List<DocumentModel> proxies) throws ClientException {
        ArrayList<String> proxiedIds = new ArrayList<String>();
        for (DocumentModel proxy : proxies) {
            proxiedIds.add(session.getWorkingCopy(proxy.getRef()).getId());
        }
        return proxiedIds;
    }

    private List<String> getProxyIds(CoreSession session, List<DocumentModel> docs, DocumentRef root) throws ClientException {
        ArrayList<String> proxyIds = new ArrayList<String>();
        for (DocumentModel doc : docs) {
            for (DocumentModel proxy : session.getProxies(doc.getRef(), root)) {
                proxyIds.add(proxy.getId());
            }
        }
        return proxyIds;
    }
    
    private List<String> getParentIds(List<DocumentModel> docs) throws ClientException {
        ArrayList<String> parentIds = new ArrayList<String>();
        for (DocumentModel doc : docs) {
            parentIds.add(doc.getParentRef().reference().toString());
        }
        return parentIds;
    }
}
