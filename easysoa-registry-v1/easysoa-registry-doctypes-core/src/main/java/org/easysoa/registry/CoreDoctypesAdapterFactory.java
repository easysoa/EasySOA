package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.Deliverable;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class CoreDoctypesAdapterFactory implements DocumentAdapterFactory {
    
    private static Logger logger = Logger.getLogger(CoreDoctypesAdapterFactory.class);

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        try {
            if (Deliverable.class.equals(itf)) {
                return new Deliverable(doc);
            }
        }
        catch (InvalidDoctypeException e) {
            logger.warn("Could not create adapter: " + e.getMessage());
        }
        return null;
    }
    
}
