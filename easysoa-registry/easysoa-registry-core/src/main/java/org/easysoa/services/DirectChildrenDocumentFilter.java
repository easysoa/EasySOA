package org.easysoa.services;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * Only keeps direct children of the given document
 * 
 * @author mkalam-alami
 *
 */
public class DirectChildrenDocumentFilter extends DeletedDocumentFilter {

    private static final long serialVersionUID = 1L;
    
    private DocumentRef parent;
    
    private boolean filterDeleted;
    
    
    public DirectChildrenDocumentFilter(DocumentRef parent) {
        this(parent, true);
    }

    public DirectChildrenDocumentFilter(DocumentRef parent, boolean filterDeleted) {
        this.parent = parent;
        this.filterDeleted = filterDeleted;
    }
    
    @Override
    public boolean accept(DocumentModel docModel) {
        if (filterDeleted && !super.accept(docModel)) {
            return false;
        }
        return parent != null && parent.equals(docModel.getParentRef());
    }
    

}
