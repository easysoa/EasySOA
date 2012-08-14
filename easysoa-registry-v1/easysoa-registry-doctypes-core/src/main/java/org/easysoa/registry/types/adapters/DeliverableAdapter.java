package org.easysoa.registry.types.adapters;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.adapters.AbstractDocumentAdapter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;


/**
 * 
 * @author mkalam-alami
 *
 */
public class DeliverableAdapter extends AbstractDocumentAdapter implements Deliverable {

    public DeliverableAdapter(DocumentModel documentModel) throws InvalidDoctypeException {
        super(documentModel);
    }
    
    public String getDoctype() {
        return DOCTYPE;
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
