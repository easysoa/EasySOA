package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.MavenDeliverable;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class JavaDoctypesAdapterFactory implements DocumentAdapterFactory {
    
    private static Logger logger = Logger.getLogger(JavaDoctypesAdapterFactory.class);

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        try {
            if (MavenDeliverable.class.equals(itf)) {
                return new MavenDeliverable(doc);
            }
        }
        catch (InvalidDoctypeException e) {
            logger.warn("Could not create adapter: " + e.getMessage());
        }
        return null;
    }
    
}
