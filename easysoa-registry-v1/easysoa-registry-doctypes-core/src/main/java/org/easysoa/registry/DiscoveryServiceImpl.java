package org.easysoa.registry;

import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class DiscoveryServiceImpl implements DiscoveryService {

    public DocumentModel importDiscovery(CoreSession documentManager, SoaNodeId document,
            Map<String, String> properties, List<SoaNodeId> correlatedDocuments) {
        
        return null;
    }

}
