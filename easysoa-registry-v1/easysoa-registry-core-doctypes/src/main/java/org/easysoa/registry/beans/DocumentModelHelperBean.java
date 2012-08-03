package org.easysoa.registry.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.runtime.api.Framework;

@Name("documentModelHelperBean")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class DocumentModelHelperBean {

    private static final Log log = LogFactory.getLog(DocumentModelHelperBean.class);

    public String getDocumentTypeLabel(DocumentModel model) throws Exception {
        try {
            TypeManager typeManager = Framework.getService(TypeManager.class);
            return typeManager.getType(model.getType()).getLabel();
        } catch (Exception e) {
            log.warn("Failed to fetch document type label, falling back to type name instead.");
            return model.getType();
        }
    }

}
