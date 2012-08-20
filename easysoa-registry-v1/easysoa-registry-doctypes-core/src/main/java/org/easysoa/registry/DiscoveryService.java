package org.easysoa.registry;

import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 * 
 */
public interface DiscoveryService {

    DocumentModel runDiscovery(CoreSession documentManager, SoaNodeId identifier,
            Map<String, String> properties, List<SoaNodeId> correlatedDocuments) throws Exception;

}
