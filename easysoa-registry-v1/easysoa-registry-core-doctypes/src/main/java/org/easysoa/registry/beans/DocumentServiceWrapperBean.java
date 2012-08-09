package org.easysoa.registry.beans;

import java.io.Serializable;

import org.easysoa.registry.services.DocumentService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.nuxeo.runtime.api.Framework;

@Name("documentService")
@Scope(ScopeType.EVENT)
@Install(precedence = Install.FRAMEWORK)
public class DocumentServiceWrapperBean implements Serializable {

    private static final long serialVersionUID = 1L;

    protected DocumentService documentService;

    @Unwrap
    public DocumentService getService() throws Exception {
        if (documentService == null) {
            documentService = Framework.getService(DocumentService.class);
        }
        return documentService;
    }

}
