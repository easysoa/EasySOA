package org.easysoa.registry.types;

import org.easysoa.registry.InvalidDoctypeException;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class DeployedDeliverable extends AbstractDocumentAdapter {

    public static final String DOCTYPE = "DeployedDeliverable";

    public static final String XPATH_ENVIRONMENT = "env:environment";

    public DeployedDeliverable(DocumentModel documentModel) throws InvalidDoctypeException {
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
