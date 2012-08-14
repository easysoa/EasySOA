package org.easysoa.registry.types.adapters;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.adapters.AbstractDocumentAdapter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class EndpointAdapter extends AbstractDocumentAdapter implements Endpoint {

    public EndpointAdapter(DocumentModel documentModel) throws InvalidDoctypeException {
        super(documentModel);
    }

    @Override
    public String getDoctype() {
        return DOCTYPE;
    }
    
    public String getEnvironment() throws PropertyException, ClientException {
        return (String) documentModel.getPropertyValue(XPATH_ENVIRONMENT);
    }
}
