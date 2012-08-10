package org.easysoa.registry.beans;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

@Name("documentModelHelperBean")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class DocumentModelHelperBean {

    @In
    CoreSession documentManager;

    @In(create = true)
    DocumentService documentService;
    
    public String getDocumentTypeLabel(DocumentModel model) throws Exception {
        return DocumentModelHelper.getDocumentTypeLabel(model.getType());
    }
    
    public DocumentModelList findAllParents(DocumentModel documentModel) throws Exception {
        return documentService.findAllParents(documentManager, documentModel);
    }
    
}
