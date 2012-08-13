package org.easysoa.registry.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public class DocumentModelHelper {

    public static final DocumentRef WORKSPACEROOT_REF = new PathRef("/default-domain/workspaces");
    
    private static final Log log = LogFactory.getLog(DocumentModelHelper.class);

    public static String getDocumentTypeLabel(String doctype) {
        try {
            TypeManager typeManager = Framework.getService(TypeManager.class);
            return typeManager.getType(doctype).getLabel();
        } catch (Exception e) {
            log.warn("Failed to fetch document type label, falling back to type name instead.");
            return doctype;
        }
    }

    /**
     * Returns the unique identifier of the document for EasySOA.
     * Not to be confused with the ID given by Nuxeo to each document.
     * @param docName
     * @return
     */
    public static String getIdentifier(String documentName) {
        // Remove eventual suffix added by Nuxeo if some proxies conflict
        // XXX: Side effect is that no document should end its "real" name with a dot followed by numbers
        return documentName.replaceAll("\\.[0-9]+$", "");
    }
}
