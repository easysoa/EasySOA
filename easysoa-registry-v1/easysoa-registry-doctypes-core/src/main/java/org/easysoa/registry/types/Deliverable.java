package org.easysoa.registry.types;

import org.easysoa.registry.InvalidDoctypeException;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;


/**
 * 
 * @author mkalam-alami
 *
 */
public class Deliverable extends AbstractDocumentAdapter {

    public static final String DOCTYPE = "Deliverable";
    
    public static final String XPATH_NATURE = "del:nature";

    private static final String XPATH_APPLICATION = "del:application";
    
    public String getDoctype() {
        return DOCTYPE;
    }
    
    public Deliverable(DocumentModel documentModel) throws InvalidDoctypeException {
        super(documentModel);
    }

    public String getNature() throws ClientException {
        return (String) documentModel.getPropertyValue(XPATH_NATURE);
    }
    
    public void setNature(String nature) throws ClientException {
        documentModel.setPropertyValue(XPATH_NATURE, nature);
    }

    public String getApplication() throws ClientException {
        return (String) documentModel.getPropertyValue(XPATH_APPLICATION);
    }
    
    public void setApplication(String application) throws ClientException {
        documentModel.setPropertyValue(XPATH_NATURE, application);
    }

}
