package org.easysoa;

import java.util.List;
import java.util.Map;

import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.content.template.factories.BaseContentFactory;
import org.nuxeo.ecm.platform.content.template.service.ACEDescriptor;
import org.nuxeo.ecm.platform.content.template.service.TemplateItemDescriptor;
import org.nuxeo.runtime.api.Framework;

public class EasySOAContentFactory extends BaseContentFactory {

    protected boolean isTargetEmpty(DocumentModel eventDoc) throws ClientException  {
        // If we already have children : exit !!!
        return session.getChildren(eventDoc.getRef()).isEmpty();
    }


    @Override
    public void createContentStructure(DocumentModel eventDoc)
            throws ClientException {
        initSession(eventDoc);

        if (eventDoc.isVersion() || !isTargetEmpty(eventDoc)) {
            return;
        }

        DocumentService docService = Framework.getLocalService(DocumentService.class);
        // Touch default application
        docService.getDefaultAppliImpl(session);

    }

    @Override
    public boolean initFactory(Map<String, String> opionst,
            List<ACEDescriptor> rootAcl, List<TemplateItemDescriptor> template) {

        return true;
    }

}
