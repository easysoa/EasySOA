package org.easysoa.registry.types;

import org.nuxeo.ecm.core.api.PathRef;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface Repository extends Document {
    
    public static final String DOCTYPE = "Repository";

    public static final String REPOSITORY_PATH = "/default-domain/repository";  
 
    public static final PathRef REPOSITORY_REF = new PathRef(REPOSITORY_PATH);
    
    public static final String REPOSITORY_TITLE = "Repository"; // TODO l10n

}
