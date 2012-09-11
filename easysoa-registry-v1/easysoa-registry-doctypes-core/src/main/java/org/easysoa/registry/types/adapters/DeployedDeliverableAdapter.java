package org.easysoa.registry.types.adapters;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.DeployedDeliverable;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class DeployedDeliverableAdapter extends SoaNodeAdapter implements DeployedDeliverable {

    public DeployedDeliverableAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
    }

    @Override
    public String getDoctype() {
        return DeployedDeliverable.DOCTYPE;
    }
    
    public String getEnvironment() throws PropertyException, ClientException {
        return (String) documentModel.getPropertyValue(XPATH_ENVIRONMENT);
    }
}
