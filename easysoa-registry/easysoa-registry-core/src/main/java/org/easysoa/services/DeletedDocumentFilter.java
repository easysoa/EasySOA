package org.easysoa.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;

/**
 * Filters all deleted documents.
 * 
 * @author mkalam-alami
 *
 */
public class DeletedDocumentFilter implements Filter {

    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(DeletedDocumentFilter.class);
    
    @Override
    public boolean accept(DocumentModel docModel) {
        try {
            return !("deleted".equals(docModel.getCurrentLifeCycleState()));
        } catch (ClientException e) {
            log.warn("Failed to get current document lifecycle state", e);
            return false;
        }
    }
    

}
